package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomStatus
import com.example.data.model.RoomWithGuest
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailSheet(
    roomWithGuest: RoomWithGuest,
    onDismiss: () -> Unit,
    onCheckInClick: () -> Unit,
    onExtendStayClick: () -> Unit,
    onSwitchRoomClick: () -> Unit,
    onCheckoutClick: () -> Unit,
    onMarkCleanedClick: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val room = roomWithGuest.room
    val guest = roomWithGuest.activeGuest
    val isOccupied = roomWithGuest.isOccupied
    val isCleaning = roomWithGuest.isCleaning

    val currencyFormat = NumberFormat.getNumberInstance(Locale.US)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row: Room number, name, type, close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                if (isOccupied) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${room.roomNumber}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = if (isOccupied) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = room.roomName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${room.roomType} • ${currencyFormat.format(room.dailyPrice)} ብር/ቀን",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = when {
                        isCleaning -> MaterialTheme.colorScheme.tertiaryContainer
                        !isOccupied -> StatusVacantBg
                        roomWithGuest.isOverdue -> StatusOverdueBg
                        roomWithGuest.isExpiringToday -> StatusDueBg
                        else -> StatusOccupiedBg
                    }
                ) {
                    Text(
                        text = when {
                            isCleaning -> "ጽዳት ላይ"
                            !isOccupied -> "ክፍት ክፍል"
                            roomWithGuest.isOverdue -> "ጊዜው አልፏል"
                            roomWithGuest.isExpiringToday -> "ዛሬ ያበቃል"
                            else -> "የተያዘ"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isCleaning -> MaterialTheme.colorScheme.onTertiaryContainer
                            !isOccupied -> StatusVacantFg
                            roomWithGuest.isOverdue -> StatusOverdueFg
                            roomWithGuest.isExpiringToday -> StatusDueFg
                            else -> StatusOccupiedFg
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

            if (isOccupied && guest != null) {
                // Active Guest Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = guest.fullName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (guest.idCardOrPassport.isNotBlank()) {
                                        Text(
                                            text = "መታወቂያ/ፓስፖርት: ${guest.idCardOrPassport}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Dial Phone Button
                            if (guest.phoneNumber.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        dialPhoneNumber(context, guest.phoneNumber)
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "ደውል",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Countdown Banner
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                roomWithGuest.isOverdue -> StatusOverdueBg
                                roomWithGuest.isExpiringToday -> StatusDueBg
                                else -> MaterialTheme.colorScheme.secondaryContainer
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Timer,
                                            contentDescription = null,
                                            tint = when {
                                                roomWithGuest.isOverdue -> StatusOverdueFg
                                                roomWithGuest.isExpiringToday -> StatusDueFg
                                                else -> MaterialTheme.colorScheme.onSecondaryContainer
                                            },
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "የቀሪ ጊዜ ሁኔታ:",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Text(
                                        text = roomWithGuest.remainingStatusText,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = when {
                                            roomWithGuest.isOverdue -> StatusOverdueFg
                                            roomWithGuest.isExpiringToday -> StatusDueFg
                                            else -> MaterialTheme.colorScheme.onSecondaryContainer
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                LinearProgressIndicator(
                                    progress = { roomWithGuest.stayProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = when {
                                        roomWithGuest.isOverdue -> StatusOverdueFg
                                        roomWithGuest.isExpiringToday -> StatusDueFg
                                        else -> MaterialTheme.colorScheme.primary
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Grid of key requested parameters:
                        // 1. መቼ እንደገቡ (Check-in)
                        // 2. መቼ እንደሚያልቅ (Expiry)
                        // 3. ስንት ቀን እንደሚቆዩ (Total Stay)
                        // 4. ስንት እንደከፈሉ (Total Paid)
                        // 5. ማስያዣ ስንት ብር እንደከፈሉ (Security Deposit)
                        DetailItemRow("መቼ እንደገቡ:", roomWithGuest.formattedCheckIn())
                        DetailItemRow("መቼ ጊዜያቸው እንደሚያልቅ:", roomWithGuest.formattedCheckOut())
                        DetailItemRow("አጠቃላይ የቆይታ ጊዜ:", "${guest.stayDays} ቀን")
                        DetailItemRow("የቀን ተመን:", "${currencyFormat.format(guest.dailyRate)} ብር")
                        DetailItemRow("ስንት እንደከፈሉ (ኪራይ):", "${currencyFormat.format(guest.totalRentPaid)} ብር", isBold = true)
                        DetailItemRow(
                            label = "ማስያዣ ስንት ብር እንደከፈሉ:",
                            value = "${currencyFormat.format(guest.securityDeposit)} ብር (${guest.depositStatus.labelAmharic})",
                            isBold = true,
                            highlightColor = MaterialTheme.colorScheme.secondary
                        )
                        DetailItemRow("የክፍያ ዘዴ:", guest.paymentMethod)
                        if (guest.notes.isNotBlank()) {
                            DetailItemRow("ማስታወሻ:", guest.notes)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons for occupied room
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = onExtendStayClick,
                        modifier = Modifier.weight(1f).testTag("btn_extend_stay"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.EditCalendar, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ቀን አራዝም", fontSize = 12.5.sp)
                    }

                    FilledTonalButton(
                        onClick = onSwitchRoomClick,
                        modifier = Modifier.weight(1f).testTag("btn_switch_room"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ክፍል ቀይር", fontSize = 12.5.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Primary Checkout Button
                Button(
                    onClick = onCheckoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_checkout_action"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("እንግዳ አሰናብት / የማስያዣ ሂሳብ ዝጋ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Share Receipt/Info Button
                OutlinedButton(
                    onClick = {
                        shareStayDetails(context, room.roomNumber, guest.fullName, guest.phoneNumber, roomWithGuest.formattedCheckIn(), roomWithGuest.formattedCheckOut(), guest.stayDays, guest.totalRentPaid, guest.securityDeposit)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("የእንግዳውን መረጃ አጋራ / ላክ", fontSize = 13.sp)
                }

            } else if (isCleaning) {
                // Room is cleaning
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CleaningServices,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ክፍል ${room.roomNumber} በጽዳት ላይ ነው",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "ጽዳት ሲጠናቀቅ ክፍሉን ለቀጣይ እንግዳ ዝግጁ ማድረግ ይችላሉ።",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onMarkCleanedClick,
                            modifier = Modifier.fillMaxWidth().testTag("btn_finish_cleaning"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("ጽዳት ተጠናቋል (ክፍት አድርግ)")
                        }
                    }
                }
            } else {
                // Vacant room
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = StatusVacantBg.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ይህ ክፍል ክፍት እና ለአዳዲስ እንግዶች ዝግጁ ነው",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = StatusVacantFg
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "የቀን ሂሳብ: ${currencyFormat.format(room.dailyPrice)} ብር / ቀን",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onCheckInClick,
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_sheet_checkin"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("አዲስ እንግዳ አስገባ", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailItemRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    highlightColor: androidx.compose.ui.graphics.Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = highlightColor ?: MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun dialPhoneNumber(context: Context, phoneNumber: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

private fun shareStayDetails(
    context: Context,
    roomNumber: Int,
    name: String,
    phone: String,
    checkIn: String,
    checkOut: String,
    days: Int,
    rent: Double,
    deposit: Double
) {
    try {
        val text = """
            🏨 ካፍ ሀላል ገስት ሀውስ (Kaaf Halal Guest House)
            ━━━━━━━━━━━━━━━━━━━━━━
            🚪 ክፍል ቁጥር: ክፍል $roomNumber
            👤 የእንግዳ ስም: $name
            📞 ስልክ: $phone
            📅 መግቢያ ቀን: $checkIn
            📅 መውጫ ቀን: $checkOut
            ⏳ የቆይታ ጊዜ: $days ቀን
            💰 የተከፈለ ኪራይ: ${rent.toInt()} ብር
            🛡️ የተቀመጠ ማስያዣ: ${deposit.toInt()} ብር
            ━━━━━━━━━━━━━━━━━━━━━━
            ስለመረጡን እናመሰግናለን!
        """.trimIndent()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "የካፍ ገስት ሀውስ እንግዳ መረጃ")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "መረጃ አጋራ"))
    } catch (_: Exception) {
    }
}
