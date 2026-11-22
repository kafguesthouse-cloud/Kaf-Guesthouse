package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.DepositStatus
import com.example.data.model.GuestEntity
import com.example.data.model.RoomStatus
import com.example.data.model.RoomWithGuest
import com.example.data.repository.GuestHouseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class RoomFilter(val labelAmharic: String) {
    ALL("ሁሉም (16)"),
    OCCUPIED("የተያዙ"),
    VACANT("ክፍት"),
    EXPIRING("ዛሬ የሚያልቁ / ቀናቸው የደረሰ"),
    CLEANING("ጽዳት ላይ")
}

data class DashboardStats(
    val totalRooms: Int = 16,
    val occupiedRooms: Int = 0,
    val vacantRooms: Int = 0,
    val cleaningRooms: Int = 0,
    val expiringTodayOrOverdue: Int = 0,
    val totalRentCollected: Double = 0.0,
    val totalDepositHeld: Double = 0.0
)

data class GuestHouseUiState(
    val rooms: List<RoomWithGuest> = emptyList(),
    val filteredRooms: List<RoomWithGuest> = emptyList(),
    val filter: RoomFilter = RoomFilter.ALL,
    val searchQuery: String = "",
    val stats: DashboardStats = DashboardStats(),
    val selectedRoom: RoomWithGuest? = null,
    val guestsHistory: List<GuestEntity> = emptyList(),
    val isLoading: Boolean = false
)

class GuestHouseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GuestHouseRepository
    private val _filter = MutableStateFlow(RoomFilter.ALL)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedRoomNumber = MutableStateFlow<Int?>(null)

    private val _eventMessage = MutableSharedFlow<String>()
    val eventMessage = _eventMessage.asSharedFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = GuestHouseRepository(database.guestHouseDao())
        viewModelScope.launch {
            repository.ensureInitialized()
        }
    }

    val uiState: StateFlow<GuestHouseUiState> = combine(
        repository.roomsWithGuests,
        repository.guestsHistory,
        _filter,
        _searchQuery,
        _selectedRoomNumber
    ) { rooms, history, filter, query, selectedRoomNum ->
        // Compute stats
        val occupied = rooms.count { it.isOccupied }
        val vacant = rooms.count { it.isVacant }
        val cleaning = rooms.count { it.isCleaning }
        val expiring = rooms.count { it.isExpiringToday || it.isOverdue }

        val totalRent = rooms.filter { it.isOccupied }
            .sumOf { it.activeGuest?.totalRentPaid ?: 0.0 }
        val totalDeposit = rooms.filter { it.isOccupied }
            .sumOf { it.activeGuest?.securityDeposit ?: 0.0 }

        val stats = DashboardStats(
            totalRooms = rooms.size,
            occupiedRooms = occupied,
            vacantRooms = vacant,
            cleaningRooms = cleaning,
            expiringTodayOrOverdue = expiring,
            totalRentCollected = totalRent,
            totalDepositHeld = totalDeposit
        )

        // Filter and Search
        val filtered = rooms.filter { roomWithGuest ->
            val matchesFilter = when (filter) {
                RoomFilter.ALL -> true
                RoomFilter.OCCUPIED -> roomWithGuest.isOccupied
                RoomFilter.VACANT -> roomWithGuest.isVacant
                RoomFilter.EXPIRING -> roomWithGuest.isExpiringToday || roomWithGuest.isOverdue
                RoomFilter.CLEANING -> roomWithGuest.isCleaning
            }

            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                val q = query.trim().lowercase()
                roomWithGuest.room.roomNumber.toString().contains(q) ||
                    roomWithGuest.room.roomName.lowercase().contains(q) ||
                    roomWithGuest.room.roomType.lowercase().contains(q) ||
                    (roomWithGuest.activeGuest?.fullName?.lowercase()?.contains(q) == true) ||
                    (roomWithGuest.activeGuest?.phoneNumber?.contains(q) == true)
            }

            matchesFilter && matchesQuery
        }

        val selectedRoom = selectedRoomNum?.let { num ->
            rooms.firstOrNull { it.room.roomNumber == num }
        }

        GuestHouseUiState(
            rooms = rooms,
            filteredRooms = filtered,
            filter = filter,
            searchQuery = query,
            stats = stats,
            selectedRoom = selectedRoom,
            guestsHistory = history,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GuestHouseUiState(isLoading = true)
    )

    fun setFilter(filter: RoomFilter) {
        _filter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectRoom(roomNumber: Int?) {
        _selectedRoomNumber.value = roomNumber
    }

    fun checkInGuest(
        roomNumber: Int,
        fullName: String,
        phoneNumber: String,
        idCardOrPassport: String,
        stayDays: Int,
        checkInTime: Long,
        dailyRate: Double,
        totalRentPaid: Double,
        securityDeposit: Double,
        paymentMethod: String,
        notes: String
    ) {
        viewModelScope.launch {
            val expectedCheckOut = checkInTime + (stayDays * 24L * 60L * 60L * 1000L)
            val guest = GuestEntity(
                roomNumber = roomNumber,
                fullName = fullName.trim(),
                phoneNumber = phoneNumber.trim(),
                idCardOrPassport = idCardOrPassport.trim(),
                checkInTimestamp = checkInTime,
                stayDays = stayDays,
                expectedCheckOutTimestamp = expectedCheckOut,
                dailyRate = dailyRate,
                totalRentPaid = totalRentPaid,
                securityDeposit = securityDeposit,
                depositStatus = DepositStatus.HELD,
                paymentMethod = paymentMethod,
                notes = notes.trim(),
                isActive = true
            )
            repository.checkInGuest(guest)
            _selectedRoomNumber.value = null
            _eventMessage.emit("እንግዳ $fullName ክፍል $roomNumber በተሳካ ሁኔታ ገብተዋል!")
        }
    }

    fun checkOutGuest(
        guestId: Long,
        roomNumber: Int,
        refundDepositAmount: Double,
        depositStatus: DepositStatus,
        checkoutNotes: String,
        setCleaning: Boolean
    ) {
        viewModelScope.launch {
            repository.checkOutGuest(
                guestId = guestId,
                roomNumber = roomNumber,
                actualCheckout = System.currentTimeMillis(),
                refundDeposit = refundDepositAmount,
                depositStatus = depositStatus,
                checkoutNote = checkoutNotes,
                markForCleaning = setCleaning
            )
            _selectedRoomNumber.value = null
            _eventMessage.emit("ክፍል $roomNumber እንግዳ መውጣታቸው ተመዝግቧል!")
        }
    }

    fun extendStay(guestId: Long, roomNumber: Int, additionalDays: Int, additionalPayment: Double) {
        viewModelScope.launch {
            repository.extendStay(guestId, additionalDays, additionalPayment)
            _eventMessage.emit("የክፍል $roomNumber ቆይታ በ${additionalDays} ቀን ተራዝሟል!")
        }
    }

    fun switchRoom(guestId: Long, oldRoomNumber: Int, newRoomNumber: Int) {
        viewModelScope.launch {
            repository.switchRoom(guestId, oldRoomNumber, newRoomNumber)
            _selectedRoomNumber.value = newRoomNumber
            _eventMessage.emit("እንግዳ ከክፍል $oldRoomNumber ወደ ክፍል $newRoomNumber ተዛውረዋል!")
        }
    }

    fun markRoomCleaned(roomNumber: Int) {
        viewModelScope.launch {
            repository.updateRoomStatus(roomNumber, RoomStatus.VACANT)
            _eventMessage.emit("ክፍል $roomNumber ጽዳት ተጠናቆ ዝግጁ ሆኗል!")
        }
    }

    fun updateRoomConfig(roomNumber: Int, dailyPrice: Double, roomType: String) {
        viewModelScope.launch {
            repository.updateRoomConfig(roomNumber, dailyPrice, roomType)
            _eventMessage.emit("የክፍል $roomNumber መረጃ ተስተካክሏል!")
        }
    }
}
