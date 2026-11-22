package com.example

import com.example.data.model.DepositStatus
import com.example.data.model.GuestEntity
import com.example.data.model.RoomEntity
import com.example.data.model.RoomStatus
import com.example.data.model.RoomWithGuest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testRoomWithGuestCalculations() {
        val now = System.currentTimeMillis()
        val dayMillis = 24L * 60L * 60L * 1000L

        val room = RoomEntity(
            roomNumber = 3,
            roomName = "ክፍል 03",
            roomType = "መደበኛ (Standard)",
            dailyPrice = 1500.0,
            status = RoomStatus.OCCUPIED,
            currentGuestId = 1L
        )

        val guest = GuestEntity(
            id = 1L,
            roomNumber = 3,
            fullName = "አሊ ኡመር",
            phoneNumber = "0911000000",
            checkInTimestamp = now,
            stayDays = 4,
            expectedCheckOutTimestamp = now + (4 * dayMillis),
            dailyRate = 1500.0,
            totalRentPaid = 6000.0,
            securityDeposit = 1000.0,
            depositStatus = DepositStatus.HELD,
            isActive = true
        )

        val roomWithGuest = RoomWithGuest(room = room, guests = listOf(guest))

        assertTrue(roomWithGuest.isOccupied)
        assertFalse(roomWithGuest.isVacant)
        assertEquals(4, roomWithGuest.daysRemaining)
        assertFalse(roomWithGuest.isOverdue)
        assertEquals("4 ቀናት ይቀራሉ", roomWithGuest.remainingStatusText)
    }

    @Test
    fun testOverdueStayCalculation() {
        val now = System.currentTimeMillis()
        val dayMillis = 24L * 60L * 60L * 1000L

        val room = RoomEntity(
            roomNumber = 8,
            roomName = "ክፍል 08",
            roomType = "ድርብ አልጋ (Twin)",
            dailyPrice = 1800.0,
            status = RoomStatus.OCCUPIED
        )

        val guest = GuestEntity(
            id = 2L,
            roomNumber = 8,
            fullName = "ያሲን ኸድር",
            phoneNumber = "0922000000",
            checkInTimestamp = now - (3 * dayMillis),
            stayDays = 3,
            expectedCheckOutTimestamp = now - (2 * 3600 * 1000L),
            dailyRate = 1800.0,
            totalRentPaid = 5400.0,
            securityDeposit = 1000.0,
            isActive = true
        )

        val roomWithGuest = RoomWithGuest(room = room, guests = listOf(guest))

        assertTrue(roomWithGuest.isOverdue)
        assertEquals(0, roomWithGuest.daysRemaining)
        assertTrue(roomWithGuest.remainingStatusText.startsWith("ጊዜው አልፏል"))
    }
}
