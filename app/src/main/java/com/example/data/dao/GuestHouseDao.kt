package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.DepositStatus
import com.example.data.model.GuestEntity
import com.example.data.model.RoomEntity
import com.example.data.model.RoomStatus
import com.example.data.model.RoomWithGuest
import kotlinx.coroutines.flow.Flow

@Dao
interface GuestHouseDao {

    @Transaction
    @Query("SELECT * FROM rooms ORDER BY roomNumber ASC")
    fun getRoomsWithGuests(): Flow<List<RoomWithGuest>>

    @Transaction
    @Query("SELECT * FROM rooms WHERE roomNumber = :roomNumber")
    fun getRoomWithGuest(roomNumber: Int): Flow<RoomWithGuest?>

    @Query("SELECT * FROM rooms WHERE roomNumber = :roomNumber")
    suspend fun getRoom(roomNumber: Int): RoomEntity?

    @Query("SELECT COUNT(*) FROM rooms")
    suspend fun getRoomCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRooms(rooms: List<RoomEntity>)

    @Update
    suspend fun updateRoom(room: RoomEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuest(guest: GuestEntity): Long

    @Update
    suspend fun updateGuest(guest: GuestEntity)

    @Query("SELECT * FROM guests WHERE id = :guestId")
    suspend fun getGuestById(guestId: Long): GuestEntity?

    @Query("SELECT * FROM guests ORDER BY checkInTimestamp DESC")
    fun getAllGuestsHistory(): Flow<List<GuestEntity>>

    @Transaction
    suspend fun checkInGuest(guest: GuestEntity): Long {
        val guestId = insertGuest(guest)
        val room = getRoom(guest.roomNumber)
        if (room != null) {
            updateRoom(
                room.copy(
                    status = RoomStatus.OCCUPIED,
                    currentGuestId = guestId
                )
            )
        }
        return guestId
    }

    @Transaction
    suspend fun checkOutGuest(
        guestId: Long,
        roomNumber: Int,
        actualCheckout: Long,
        refundDeposit: Double,
        depositStatus: DepositStatus,
        checkoutNote: String,
        markForCleaning: Boolean
    ) {
        val guest = getGuestById(guestId)
        if (guest != null) {
            val updatedNotes = if (checkoutNote.isBlank()) {
                guest.notes
            } else {
                if (guest.notes.isBlank()) checkoutNote else "${guest.notes} | መውጫ: $checkoutNote"
            }
            updateGuest(
                guest.copy(
                    actualCheckOutTimestamp = actualCheckout,
                    depositRefundAmount = refundDeposit,
                    depositStatus = depositStatus,
                    notes = updatedNotes,
                    isActive = false
                )
            )
        }

        val room = getRoom(roomNumber)
        if (room != null) {
            updateRoom(
                room.copy(
                    status = if (markForCleaning) RoomStatus.CLEANING else RoomStatus.VACANT,
                    currentGuestId = null
                )
            )
        }
    }

    @Transaction
    suspend fun extendStay(guestId: Long, additionalDays: Int, additionalPayment: Double) {
        val guest = getGuestById(guestId) ?: return
        val newDuration = guest.stayDays + additionalDays
        val additionalMillis = additionalDays * 24L * 60L * 60L * 1000L
        val newCheckout = guest.expectedCheckOutTimestamp + additionalMillis
        val newTotalRent = guest.totalRentPaid + additionalPayment

        updateGuest(
            guest.copy(
                stayDays = newDuration,
                expectedCheckOutTimestamp = newCheckout,
                totalRentPaid = newTotalRent
            )
        )
    }

    @Transaction
    suspend fun switchRoom(guestId: Long, oldRoomNumber: Int, newRoomNumber: Int) {
        val guest = getGuestById(guestId) ?: return
        val oldRoom = getRoom(oldRoomNumber)
        val newRoom = getRoom(newRoomNumber) ?: return

        // Update guest room number
        updateGuest(guest.copy(roomNumber = newRoomNumber))

        // Free old room
        if (oldRoom != null) {
            updateRoom(oldRoom.copy(status = RoomStatus.CLEANING, currentGuestId = null))
        }

        // Occupy new room
        updateRoom(newRoom.copy(status = RoomStatus.OCCUPIED, currentGuestId = guestId))
    }

    @Query("UPDATE rooms SET status = :status WHERE roomNumber = :roomNumber")
    suspend fun updateRoomStatus(roomNumber: Int, status: RoomStatus)

    @Query("UPDATE rooms SET dailyPrice = :dailyPrice, roomType = :roomType WHERE roomNumber = :roomNumber")
    suspend fun updateRoomConfig(roomNumber: Int, dailyPrice: Double, roomType: String)
}
