package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guests")
data class GuestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val roomNumber: Int,
    val fullName: String,
    val phoneNumber: String,
    val idCardOrPassport: String = "",
    val checkInTimestamp: Long = System.currentTimeMillis(),
    val stayDays: Int = 1,
    val expectedCheckOutTimestamp: Long,
    val actualCheckOutTimestamp: Long? = null,
    val dailyRate: Double = 1500.0,
    val totalRentPaid: Double = 1500.0,
    val securityDeposit: Double = 500.0,
    val depositStatus: DepositStatus = DepositStatus.HELD,
    val depositRefundAmount: Double = 0.0,
    val paymentMethod: String = "በካሽ / Cash",
    val notes: String = "",
    val isActive: Boolean = true
)
