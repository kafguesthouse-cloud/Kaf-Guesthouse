package com.example.data.repository

import com.example.data.dao.GuestHouseDao
import com.example.data.model.DepositStatus
import com.example.data.model.GuestEntity
import com.example.data.model.RoomEntity
import com.example.data.model.RoomStatus
import com.example.data.model.RoomWithGuest
import kotlinx.coroutines.flow.Flow

class GuestHouseRepository(private val dao: GuestHouseDao) {

    val roomsWithGuests: Flow<List<RoomWithGuest>> = dao.getRoomsWithGuests()

    val guestsHistory: Flow<List<GuestEntity>> = dao.getAllGuestsHistory()

    fun getRoomWithGuest(roomNumber: Int): Flow<RoomWithGuest?> = dao.getRoomWithGuest(roomNumber)

    suspend fun checkInGuest(guest: GuestEntity): Long = dao.checkInGuest(guest)

    suspend fun checkOutGuest(
        guestId: Long,
        roomNumber: Int,
        actualCheckout: Long,
        refundDeposit: Double,
        depositStatus: DepositStatus,
        checkoutNote: String,
        markForCleaning: Boolean
    ) = dao.checkOutGuest(
        guestId = guestId,
        roomNumber = roomNumber,
        actualCheckout = actualCheckout,
        refundDeposit = refundDeposit,
        depositStatus = depositStatus,
        checkoutNote = checkoutNote,
        markForCleaning = markForCleaning
    )

    suspend fun extendStay(guestId: Long, additionalDays: Int, additionalPayment: Double) {
        dao.extendStay(guestId, additionalDays, additionalPayment)
    }

    suspend fun switchRoom(guestId: Long, oldRoomNumber: Int, newRoomNumber: Int) {
        dao.switchRoom(guestId, oldRoomNumber, newRoomNumber)
    }

    suspend fun updateRoomStatus(roomNumber: Int, status: RoomStatus) {
        dao.updateRoomStatus(roomNumber, status)
    }

    suspend fun updateRoomConfig(roomNumber: Int, dailyPrice: Double, roomType: String) {
        dao.updateRoomConfig(roomNumber, dailyPrice, roomType)
    }

    suspend fun updateGuest(guest: GuestEntity) {
        dao.updateGuest(guest)
    }

    suspend fun ensureInitialized() {
        if (dao.getRoomCount() == 0) {
            com.example.data.database.AppDatabase.populateInitialRoomsAndGuests(dao)
        }
    }
}
