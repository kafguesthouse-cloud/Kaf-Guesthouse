package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rooms")
data class RoomEntity(
    @PrimaryKey
    val roomNumber: Int, // 1 to 16
    val roomName: String, // e.g. "ክፍል 01"
    val roomType: String, // e.g. "መደበኛ (Standard)", "ዴሉክስ (Deluxe)", "ቪአይፒ (VIP)"
    val dailyPrice: Double, // in ETB
    val status: RoomStatus = RoomStatus.VACANT,
    val currentGuestId: Long? = null
)
