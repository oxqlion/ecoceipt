package com.example.ecoceipt.ui.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ecoceipt.ui.viewmodels.DashboardViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecoceipt.models.ItemModel
import com.example.ecoceipt.models.PurchasedItemModel
import com.example.ecoceipt.models.ReceiptModel
import com.example.ecoceipt.ui.viewmodels.ReceiptsViewModel
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardView(
    navController: NavController,
    viewModel: DashboardViewModel = viewModel(),
    viewModelReceipt: ReceiptsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModelReceipt.getRecommendation("3s8mnAExkbJHYOVnVrfQ")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            HeaderSection(
                userName = uiState.userName,
                onProfileClick = { navController.navigate("profile") },
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.aiRecommendation.isNotBlank()) {
                AiRecommendationSection(recommendation = uiState.aiRecommendation)
                Spacer(modifier = Modifier.height(24.dp))
            }

            TimePeriodSelector(
                selectedPeriod = uiState.selectedPeriod,
                onPeriodSelected = { period -> viewModel.updatePeriod(period) }
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                RevenueChartSection(
                    revenueData = uiState.revenueData,
                    totalRevenue = uiState.totalRevenue
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            ReceiptHistorySection(receipts = uiState.receipts)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- THIS IS THE ONLY COMPOSABLE THAT WAS CHANGED ---
@Composable
fun RevenueBarChart(data: List<Pair<String, Double>>) {
    // Find the maximum value from the data.
    val maxValue = data.maxOfOrNull { it.second } ?: 0.0

    // CRITICAL FIX: Create a "safe" maximum value that is at least 1.0 to prevent division by zero.
    val safeMaxValue = if (maxValue > 0.0) maxValue else 1.0

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        data.forEach { (label, value) ->
            // Use the safe maximum value for the calculation.
            val percentage = (value / safeMaxValue).toFloat()

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    modifier = Modifier.width(50.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                LinearProgressIndicator(
                    progress = { percentage },
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Butt
                )
                Text(
                    text = formatCurrency(value),
                    modifier = Modifier
                        .width(80.dp)
                        .padding(start = 8.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}


// The rest of the file is unchanged.
// ...
@Composable
fun AiRecommendationSection(recommendation: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = "AI Recommendation",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = recommendation,
                color = MaterialTheme.colorScheme.onSurface,
                fontStyle = FontStyle.Italic,
                fontSize = 14.sp
            )
        }
    }
}


@Composable
fun HeaderSection(userName: String, onProfileClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Hello, $userName",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Scan it neat, make waste obsolete",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
        IconButton(
            onClick = onProfileClick,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Profile",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePeriodSelector(selectedPeriod: String, onPeriodSelected: (String) -> Unit) {
    val periods = listOf("Weekly", "Monthly", "Yearly")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        periods.forEach { period ->
            val isSelected = selectedPeriod == period
            FilterChip(
                onClick = { onPeriodSelected(period) },
                label = {
                    Text(
                        text = period,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                selected = isSelected,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = Color.Transparent,
                    selectedBorderColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun RevenueChartSection(
    revenueData: List<Pair<String, Double>>,
    totalRevenue: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Revenue Overview",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Total for this period",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Text(
                    text = formatCurrency(totalRevenue),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            RevenueBarChart(data = revenueData)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptHistorySection(receipts: List<ReceiptModel>) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    val selectedDateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
    val filteredReceipts = receipts.filter { isSameDay(it.date, selectedDateMillis) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Receipt History",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            DateFilterChip(
                selectedDateMillis = selectedDateMillis,
                onClick = { showDatePicker = true }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (filteredReceipts.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                filteredReceipts.forEach { receipt ->
                    ReceiptItemCard(receipt = receipt)
                }
            }
        } else {
            Text(
                text = "No receipts for this day.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { showDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun DateFilterChip(selectedDateMillis: Long, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = CircleShape,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = "Select Date",
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = formatDateForDisplay(selectedDateMillis),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ReceiptItemCard(receipt: ReceiptModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                contentDescription = "Receipt",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Receipt #${receipt.id.takeLast(6)}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${receipt.items.size} items",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Text(
                text = formatCurrency(receipt.totalAmount),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
        }
    }
}

fun createDummyReceipts(): List<ReceiptModel> {
    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    val twoDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -2) }
    return listOf(
        ReceiptModel(id = "TXN1001", date = today.timeInMillis, totalAmount = 75000.0, items = listOf(
            PurchasedItemModel(), PurchasedItemModel())),
        ReceiptModel(id = "TXN1002", date = today.timeInMillis, totalAmount = 120000.0, items = listOf(PurchasedItemModel(), PurchasedItemModel(), PurchasedItemModel())),
        ReceiptModel(id = "TXN1003", date = yesterday.timeInMillis, totalAmount = 50000.0, items = listOf(PurchasedItemModel())),
        ReceiptModel(id = "TXN1004", date = yesterday.timeInMillis, totalAmount = 250000.0, items = (1..5).map { PurchasedItemModel() }),
        ReceiptModel(id = "TXN1005", date = twoDaysAgo.timeInMillis, totalAmount = 95000.0, items = (1..3).map { PurchasedItemModel() })
    )
}

fun isSameDay(millis1: Long, millis2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = millis1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = millis2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun formatDateForDisplay(millis: Long): String {
    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    val selectedCal = Calendar.getInstance().apply { timeInMillis = millis }
    return when {
        isSameDay(millis, today.timeInMillis) -> "Today"
        isSameDay(millis, yesterday.timeInMillis) -> "Yesterday"
        else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))
    }
}

fun formatCurrency(amount: Double): String {
    return when {
        amount >= 1_000_000_000 -> "Rp${(amount / 1_000_000_000).toInt()}B"
        amount >= 1_000_000 -> "Rp${"%.1f".format(amount / 1_000_000)}M"
        amount >= 1_000 -> "Rp${(amount / 1_000).toInt()}K"
        else -> "Rp${amount.toInt()}"
    }
}