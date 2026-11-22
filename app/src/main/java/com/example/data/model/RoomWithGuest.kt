package com.example.data.model

import androidx.room.Embedded
import androidx.room.Relation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max

data class RoomWithGuest(
    @Embedded val room: RoomEntity,
    @Relation(
        parentColumn = "roomNumber",
        entityColumn = "roomNumber"
    )
    val guests: List<GuestEntity> = emptyList()
) {
    // The currently active guest in this room, if any
    val activeGuest: GuestEntity?
        get() = guests.firstOrNull { it.isActive }

    val isOccupied: Boolean
        get() = room.status == RoomStatus.OCCUPIED && activeGuest != null

    val isCleaning: Boolean
        get() = room.status == RoomStatus.CLEANING

    val isVacant: Boolean
        get() = room.status == RoomStatus.VACANT && activeGuest == null

    /**
     * Milliseconds remaining until expected checkout
     */
    val millisRemaining: Long
        get() {
            val guest = activeGuest ?: return 0L
            return guest.expectedCheckOutTimestamp - System.currentTimeMillis()
        }

    /**
     * Estimated whole/fractional days remaining
     */
    val daysRemaining: Int
        get() {
            val guest = activeGuest ?: return 0
            val millis = millisRemaining
            return if (millis <= 0) {
                0
            } else {
                max(1, ceil(millis.toDouble() / (24 * 60 * 60 * 1000.0)).toInt())
            }
        }

    val isExpiringToday: Boolean
        get() {
            val guest = activeGuest ?: return false
            val millis = millisRemaining
            return millis in 0..(24 * 60 * 60 * 1000)
        }

    val isOverdue: Boolean
        get() {
            val guest = activeGuest ?: return false
            return millisRemaining < 0
        }

    /**
     * Progress of the stay between 0.0 (just checked in) and 1.0 (checkout time reached)
     */
    val stayProgress: Float
        get() {
            val guest = activeGuest ?: return 0f
            val total = (guest.expectedCheckOutTimestamp - guest.checkInTimestamp).toFloat()
            if (total <= 0f) return 1f
            val elapsed = (System.currentTimeMillis() - guest.checkInTimestamp).toFloat()
            return (elapsed / total).coerceIn(0f, 1f)
        }

    val remainingStatusText: String
        get() {
            if (!isOccupied) return "ክፍት ክፍል"
            if (isOverdue) {
                val overdueHours = (-millisRemaining) / (60 * 60 * 1000)
                return if (overdueHours < 24) {
                    "ጊዜው አልፏል (${overdueHours} ሰዓት)"
                } else {
                    val days = (overdueHours / 24).toInt()
                    "ጊዜው አልፏል (${days} ቀን)"
                }
            }
            if (isExpiringToday) {
                val hoursLeft = max(1, (millisRemaining / (60 * 60 * 1000)).toInt())
                return "ዛሬ ያበቃል (${hoursLeft} ሰዓት ቀረው)"
            }
            val days = daysRemaining
            return if (days == 1) "1 ቀን ይቀራል" else "$days ቀናት ይቀራሉ"
        }

    fun formattedCheckIn(): String {
        val guest = activeGuest ?: return "-"
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(guest.checkInTimestamp))
    }

    fun formattedCheckOut(): String {
        val guest = activeGuest ?: return "-"
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(guest.expectedCheckOutTimestamp))
    }
}
