package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomStatus
import com.example.data.model.RoomWithGuest
import com.example.ui.theme.StatusCleaningBg
import com.example.ui.theme.StatusCleaningFg
import com.example.ui.theme.StatusDueBg
import com.example.ui.theme.StatusDueFg
import com.example.ui.theme.StatusOccupiedBg
import com.example.ui.theme.StatusOccupiedFg
import com.example.ui.theme.StatusOverdueBg
import com.example.ui.theme.StatusOverdueFg
import com.example.ui.theme.StatusVacantBg
import com.example.ui.theme.StatusVacantFg
import java.text.NumberFormat
import java.util.Locale

@Composable
fun RoomCard(
    roomWithGuest: RoomWithGuest,
    onCardClick: () -> Unit,
    onCheckInClick: () -> Unit,
    onMarkCleanedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val room = roomWithGuest.room
    val guest = roomWithGuest.activeGuest
    val isOccupied = roomWithGuest.isOccupied
    val isCleaning = roomWithGuest.isCleaning

    val currencyFormat = NumberFormat.getNumberInstance(Locale.US)

    // Determine status badge color and text
    val (statusBg, statusFg, statusLabel) = when {
        isCleaning -> Triple(StatusCleaningBg, StatusCleaningFg, "ጽዳት ላይ")
        !isOccupied -> Triple(StatusVacantBg, StatusVacantFg, "ክፍት")
        roomWithGuest.isOverdue -> Triple(StatusOverdueBg, StatusOverdueFg, "ጊዜው አልፏል!")
        roomWithGuest.isExpiringToday -> Triple(StatusDueBg, StatusDueFg, "ዛሬ ያበቃል")
        else -> Triple(StatusOccupiedBg, StatusOccupiedFg, "የተያዘ")
    }

    val cardBorder = when {
        roomWithGuest.isOverdue -> BorderStroke(1.5.dp, StatusOverdueFg)
        roomWithGuest.isExpiringToday -> BorderStroke(1.5.dp, StatusDueFg)
        isOccupied -> BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("room_card_${room.roomNumber}")
            .clickable(onClick = onCardClick)
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = cardBorder
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Room Number, Room Type, Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (isOccupied) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${room.roomNumber}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = if (isOccupied) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = room.roomName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = room.roomType,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status Badge Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusBg,
                    contentColor = statusFg
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(statusFg)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = statusLabel,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content section based on status
            if (isOccupied && guest != null) {
                // Occupied Guest Details Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Guest Name and Phone
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "እንግዳ",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = guest.fullName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            if (guest.phoneNumber.isNotBlank()) {
                                Text(
                                    text = guest.phoneNumber,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Countdown / Days Remaining Banner
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                roomWithGuest.isOverdue -> StatusOverdueBg
                                roomWithGuest.isExpiringToday -> StatusDueBg
                                else -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (roomWithGuest.isOverdue || roomWithGuest.isExpiringToday)
                                            Icons.Default.NotificationsActive else Icons.Default.Timer,
                                        contentDescription = "የቀረ ቀን",
                                        tint = when {
                                            roomWithGuest.isOverdue -> StatusOverdueFg
                                            roomWithGuest.isExpiringToday -> StatusDueFg
                                            else -> MaterialTheme.colorScheme.onSecondaryContainer
                                        },
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = roomWithGuest.remainingStatusText,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            roomWithGuest.isOverdue -> StatusOverdueFg
                                            roomWithGuest.isExpiringToday -> StatusDueFg
                                            else -> MaterialTheme.colorScheme.onSecondaryContainer
                                        }
                                    )
                                }

                                Text(
                                    text = "ቆይታ: ${guest.stayDays} ቀን",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Progress Bar showing elapsed stay duration
                        LinearProgressIndicator(
                            progress = { roomWithGuest.stayProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = when {
                                roomWithGuest.isOverdue -> StatusOverdueFg
                                roomWithGuest.isExpiringToday -> StatusDueFg
                                else -> MaterialTheme.colorScheme.primary
                            },
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Financial Info: Total Paid & Security Deposit (ማስያዣ)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Total Rent Paid
                            Column {
                                Text(
                                    text = "የተከፈለ ኪራይ",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${currencyFormat.format(guest.totalRentPaid)} ብር",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Security Deposit (ማስያዣ) Highlight
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "ማስያዣ",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column {
                                        Text(
                                            text = "ማስያዣ",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "${currencyFormat.format(guest.securityDeposit)} ብር",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Check-in & Expiry Dates Summary
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "መግቢያ: ${roomWithGuest.formattedCheckIn()}",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "መውጫ: ${roomWithGuest.formattedCheckOut()}",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else if (isCleaning) {
                // Cleaning State
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = StatusCleaningBg.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = "ጽዳት",
                            tint = StatusCleaningFg,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ክፍሉ በጽዳት እና ዝግጅት ላይ ነው",
                            fontSize = 12.sp,
                            color = StatusCleaningFg,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FilledTonalButton(
                            onClick = onMarkCleanedClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_clean_done_${room.roomNumber}"),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = StatusCleaningFg,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("ጽዳት ተጠናቋል (ክፍት አድርግ)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Vacant Room State
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Hotel,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "የቀን ሂሳብ:",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "${currencyFormat.format(room.dailyPrice)} ብር / ቀን",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = onCheckInClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_check_in_${room.roomNumber}"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = "እንግዳ አስገባ",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "እንግዳ አስገባ",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
