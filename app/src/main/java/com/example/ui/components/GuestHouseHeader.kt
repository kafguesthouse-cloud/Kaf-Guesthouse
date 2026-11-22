package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.StatusDueBg
import com.example.ui.theme.StatusDueFg
import com.example.ui.theme.StatusOccupiedBg
import com.example.ui.theme.StatusOccupiedFg
import com.example.ui.theme.StatusOverdueBg
import com.example.ui.theme.StatusOverdueFg
import com.example.ui.theme.StatusVacantBg
import com.example.ui.theme.StatusVacantFg
import com.example.ui.viewmodel.DashboardStats
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GuestHouseHeader(
    stats: DashboardStats,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getNumberInstance(Locale.US)
    val todayDateFormatted = remember {
        val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale("am", "ET"))
        sdf.format(Date())
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.88f)
                    )
                )
            )
            .padding(top = 16.dp, bottom = 14.dp)
    ) {
        // App Title Row with Brand Logo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Branded Logo Image
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_kaf_icon),
                        contentDescription = "ካፍ ገስት ሀውስ አርማ",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "ካፍ ሀላል ገስት ሀውስ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "የ16 ክፍሎች መቆጣጠሪያ ዳሽቦርድ",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.82f)
                    )
                }
            }

            // Live Date Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.18f),
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ዛሬ",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Horizontal scrolling KPI metric cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Metric 1: Occupied / Total
            MetricCard(
                title = "የተያዙ ክፍሎች",
                value = "${stats.occupiedRooms} / ${stats.totalRooms}",
                subtext = "${stats.vacantRooms} ክፍት",
                icon = Icons.Default.Hotel,
                bg = StatusOccupiedBg,
                fg = StatusOccupiedFg
            )

            // Metric 2: Expiring Today / Overdue
            MetricCard(
                title = "ዛሬ የሚያልቁ",
                value = "${stats.expiringTodayOrOverdue} ክፍል",
                subtext = if (stats.expiringTodayOrOverdue > 0) "ክትትል ይፈልጋል" else "ምንም የለም",
                icon = Icons.Default.NotificationsActive,
                bg = if (stats.expiringTodayOrOverdue > 0) StatusOverdueBg else StatusDueBg,
                fg = if (stats.expiringTodayOrOverdue > 0) StatusOverdueFg else StatusDueFg
            )

            // Metric 3: Total Rent Collected
            MetricCard(
                title = "የተሰበሰበ ኪራይ",
                value = "${currencyFormat.format(stats.totalRentCollected)} ብር",
                subtext = "የነቁ እንግዶች",
                icon = Icons.Default.Payments,
                bg = StatusVacantBg,
                fg = StatusVacantFg
            )

            // Metric 4: Total Security Deposit Held (ማስያዣ)
            MetricCard(
                title = "በእጅ ያለ ማስያዣ",
                value = "${currencyFormat.format(stats.totalDepositHeld)} ብር",
                subtext = "ተመላሽ የሚሆን",
                icon = Icons.Default.Shield,
                bg = MaterialTheme.colorScheme.secondaryContainer,
                fg = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    bg: Color,
    fg: Color
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = bg,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(fg.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = fg,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = fg.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = fg
                )
                Text(
                    text = subtext,
                    fontSize = 10.sp,
                    color = fg.copy(alpha = 0.75f)
                )
            }
        }
    }
}
