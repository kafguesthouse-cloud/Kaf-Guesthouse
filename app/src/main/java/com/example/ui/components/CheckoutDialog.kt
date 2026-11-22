package com.example.ui.components

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DepositStatus
import com.example.data.model.RoomWithGuest
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CheckoutDialog(
    roomWithGuest: RoomWithGuest,
    onDismiss: () -> Unit,
    onConfirm: (
        refundAmount: Double,
        depositStatus: DepositStatus,
        checkoutNotes: String,
        setCleaning: Boolean
    ) -> Unit
) {
    val guest = roomWithGuest.activeGuest ?: return
    val initialDeposit = guest.securityDeposit

    // Refund mode: FULL or DEDUCT
    var isFullRefund by remember { mutableStateOf(true) }
    var refundAmountText by remember { mutableStateOf(initialDeposit.toInt().toString()) }
    var deductionReason by remember { mutableStateOf("") }
    var markForCleaning by remember { mutableStateOf(true) }
    var generalCheckoutNotes by remember { mutableStateOf("") }

    val parsedRefund by remember {
        derivedStateOf {
            if (isFullRefund) initialDeposit else (refundAmountText.toDoubleOrNull() ?: 0.0)
        }
    }

    val currencyFormat = NumberFormat.getNumberInstance(Locale.US)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
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
                                .background(MaterialTheme.colorScheme.errorContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ExitToApp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "እንግዳ ማሰናበቻ እና ሂሳብ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "ክፍል ${roomWithGuest.room.roomNumber} - ${guest.fullName}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "ዝጋ")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Security Deposit Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "የተያዘ ማስያዣ ገንዘብ:",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                            Text(
                                text = "${currencyFormat.format(initialDeposit)} ብር",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "የማስያዣ ተመላሽ ሁኔታ:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                // Radio: Full Refund
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isFullRefund = true }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isFullRefund,
                        onClick = { isFullRefund = true }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ሙሉ ማስያዣ ተመላሽ (${currencyFormat.format(initialDeposit)} ብር)",
                        fontSize = 13.5.sp,
                        fontWeight = if (isFullRefund) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Radio: Deduction
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isFullRefund = false }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !isFullRefund,
                        onClick = { isFullRefund = false }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ተቀናሽ አለ (ለጉዳት ወይም ተጨማሪ ወጪ)",
                        fontSize = 13.5.sp,
                        fontWeight = if (!isFullRefund) FontWeight.Bold else FontWeight.Normal
                    )
                }

                if (!isFullRefund) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = refundAmountText,
                        onValueChange = { refundAmountText = it },
                        label = { Text("ለእንግዳው የሚመለስ ትክክለኛ ብር") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("input_refund_amount"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = deductionReason,
                        onValueChange = { deductionReason = it },
                        label = { Text("የተቀነሰበት ምክንያት") },
                        placeholder = { Text("ምሳሌ፡ የቁልፍ መጥፋት ወይም የንብረት ጉዳት") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Cleaning Option Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { markForCleaning = !markForCleaning }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = markForCleaning,
                        onCheckedChange = { markForCleaning = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ክፍሉ ወደ 'ጽዳት ላይ' ሁኔታ ይቀየር",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = generalCheckoutNotes,
                    onValueChange = { generalCheckoutNotes = it },
                    label = { Text("የመውጫ ማስታወሻ (አማራጭ)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Button
                Button(
                    onClick = {
                        val status = if (isFullRefund) DepositStatus.REFUNDED
                        else if (parsedRefund > 0) DepositStatus.DEDUCTED
                        else DepositStatus.DEDUCTED

                        val notes = if (isFullRefund) generalCheckoutNotes
                        else "የተቀነሰበት: $deductionReason. $generalCheckoutNotes"

                        onConfirm(parsedRefund, status, notes, markForCleaning)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_confirm_checkout"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "መውጫውን አረጋግጥ እና ሂሳብ ዝጋ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    )
                }
            }
        }
    }
}
