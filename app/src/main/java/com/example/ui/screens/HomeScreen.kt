package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DepositStatus
import com.example.data.model.RoomWithGuest
import com.example.ui.components.CheckInDialog
import com.example.ui.components.CheckoutDialog
import com.example.ui.components.ExtendStayDialog
import com.example.ui.components.GuestHouseHeader
import com.example.ui.components.RoomCard
import com.example.ui.components.RoomDetailSheet
import com.example.ui.components.RoomSettingsDialog
import com.example.ui.components.SwitchRoomDialog
import com.example.ui.viewmodel.GuestHouseUiState
import com.example.ui.viewmodel.GuestHouseViewModel
import com.example.ui.viewmodel.RoomFilter
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    viewModel: GuestHouseViewModel,
    uiState: GuestHouseUiState,
    onNavigateToHistory: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Dialog control states
    var showCheckInDialog by remember { mutableStateOf(false) }
    var checkInRoomNumber by remember { mutableStateOf<Int?>(null) }

    var showExtendDialog by remember { mutableStateOf(false) }
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var showSwitchDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var activeDialogRoom by remember { mutableStateOf<RoomWithGuest?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    checkInRoomNumber = null
                    showCheckInDialog = true
                },
                modifier = Modifier
                    .testTag("fab_new_checkin")
                    .windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("እንግዳ አስገባ", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header with statistics and brand logo
            GuestHouseHeader(stats = uiState.stats)

            // Search Bar and Navigation Actions Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_room_input"),
                    placeholder = { Text("ክፍል 1-16፣ የእንግዳ ስም ወይም ስልክ...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "አፅዳ")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                // History Log Navigation Button
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(52.dp)
                ) {
                    IconButton(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.testTag("btn_history_nav")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "የእንግዶች ታሪክ",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Room Pricing Settings Button
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(52.dp)
                ) {
                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier.testTag("btn_room_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "የክፍል ዋጋ ማስተካከያ",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Filter Chips Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RoomFilter.entries.forEach { filter ->
                    val isSelected = uiState.filter == filter
                    val count = when (filter) {
                        RoomFilter.ALL -> uiState.rooms.size
                        RoomFilter.OCCUPIED -> uiState.stats.occupiedRooms
                        RoomFilter.VACANT -> uiState.stats.vacantRooms
                        RoomFilter.EXPIRING -> uiState.stats.expiringTodayOrOverdue
                        RoomFilter.CLEANING -> uiState.stats.cleaningRooms
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setFilter(filter) },
                        label = {
                            Text(
                                text = "${filter.labelAmharic} ($count)",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            // Room Grid (Adaptive for phones and tablets)
            if (uiState.filteredRooms.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Hotel,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "በዚህ ማጣሪያ የተገኘ ክፍል የለም",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 320.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.filteredRooms, key = { it.room.roomNumber }) { roomWithGuest ->
                        RoomCard(
                            roomWithGuest = roomWithGuest,
                            onCardClick = {
                                viewModel.selectRoom(roomWithGuest.room.roomNumber)
                            },
                            onCheckInClick = {
                                checkInRoomNumber = roomWithGuest.room.roomNumber
                                showCheckInDialog = true
                            },
                            onMarkCleanedClick = {
                                viewModel.markRoomCleaned(roomWithGuest.room.roomNumber)
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for Room Detail
    uiState.selectedRoom?.let { roomWithGuest ->
        RoomDetailSheet(
            roomWithGuest = roomWithGuest,
            onDismiss = { viewModel.selectRoom(null) },
            onCheckInClick = {
                viewModel.selectRoom(null)
                checkInRoomNumber = roomWithGuest.room.roomNumber
                showCheckInDialog = true
            },
            onExtendStayClick = {
                activeDialogRoom = roomWithGuest
                showExtendDialog = true
            },
            onSwitchRoomClick = {
                activeDialogRoom = roomWithGuest
                showSwitchDialog = true
            },
            onCheckoutClick = {
                activeDialogRoom = roomWithGuest
                showCheckoutDialog = true
            },
            onMarkCleanedClick = {
                viewModel.markRoomCleaned(roomWithGuest.room.roomNumber)
                viewModel.selectRoom(null)
            }
        )
    }

    // Check-In Dialog
    if (showCheckInDialog) {
        CheckInDialog(
            initialRoomNumber = checkInRoomNumber,
            availableRooms = uiState.rooms,
            onDismiss = { showCheckInDialog = false },
            onConfirm = { roomNum, name, phone, idCard, days, time, rate, rent, deposit, method, notes ->
                viewModel.checkInGuest(
                    roomNumber = roomNum,
                    fullName = name,
                    phoneNumber = phone,
                    idCardOrPassport = idCard,
                    stayDays = days,
                    checkInTime = time,
                    dailyRate = rate,
                    totalRentPaid = rent,
                    securityDeposit = deposit,
                    paymentMethod = method,
                    notes = notes
                )
                showCheckInDialog = false
            }
        )
    }

    // Extend Stay Dialog
    if (showExtendDialog && activeDialogRoom != null) {
        val r = activeDialogRoom!!
        ExtendStayDialog(
            roomWithGuest = r,
            onDismiss = { showExtendDialog = false },
            onConfirm = { extraDays, extraPayment ->
                r.activeGuest?.let { guest ->
                    viewModel.extendStay(guest.id, r.room.roomNumber, extraDays, extraPayment)
                }
                showExtendDialog = false
                viewModel.selectRoom(null)
            }
        )
    }

    // Checkout Dialog
    if (showCheckoutDialog && activeDialogRoom != null) {
        val r = activeDialogRoom!!
        CheckoutDialog(
            roomWithGuest = r,
            onDismiss = { showCheckoutDialog = false },
            onConfirm = { refund, status, notes, setCleaning ->
                r.activeGuest?.let { guest ->
                    viewModel.checkOutGuest(
                        guestId = guest.id,
                        roomNumber = r.room.roomNumber,
                        refundDepositAmount = refund,
                        depositStatus = status,
                        checkoutNotes = notes,
                        setCleaning = setCleaning
                    )
                }
                showCheckoutDialog = false
                viewModel.selectRoom(null)
            }
        )
    }

    // Switch Room Dialog
    if (showSwitchDialog && activeDialogRoom != null) {
        val r = activeDialogRoom!!
        SwitchRoomDialog(
            roomWithGuest = r,
            allRooms = uiState.rooms,
            onDismiss = { showSwitchDialog = false },
            onConfirm = { newRoomNum ->
                r.activeGuest?.let { guest ->
                    viewModel.switchRoom(guest.id, r.room.roomNumber, newRoomNum)
                }
                showSwitchDialog = false
            }
        )
    }

    // Room Settings Dialog
    if (showSettingsDialog) {
        RoomSettingsDialog(
            allRooms = uiState.rooms,
            initialRoomNumber = 1,
            onDismiss = { showSettingsDialog = false },
            onSave = { roomNum, dailyPrice, roomType ->
                viewModel.updateRoomConfig(roomNum, dailyPrice, roomType)
                showSettingsDialog = false
            }
        )
    }
}
