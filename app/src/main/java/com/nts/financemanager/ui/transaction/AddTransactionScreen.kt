package com.nts.financemanager.ui.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nts.financemanager.data.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: TransactionViewModel,
    onTransactionSaved: () -> Unit
) {
    val categories by viewModel.listUiState.collectAsState()
    val title by viewModel.formTitle.collectAsState()
    val amount by viewModel.formAmount.collectAsState()
    val type by viewModel.formType.collectAsState()
    val category by viewModel.formCategory.collectAsState()
    val date by viewModel.formDate.collectAsState()
    val note by viewModel.formNote.collectAsState()

    var categoryExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New Entry", fontWeight = FontWeight.Bold) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ── 1. Type Toggle (M3 Segmented Button alternative) ──
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = type == TransactionType.EXPENSE,
                    onClick = { viewModel.formType.value = TransactionType.EXPENSE },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Expense")
                }
                SegmentedButton(
                    selected = type == TransactionType.INCOME,
                    onClick = { viewModel.formType.value = TransactionType.INCOME },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Income")
                }
            }

            // ── 2. Primary Form Card ──
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Amount (Large Input)
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { viewModel.formAmount.value = it },
                        label = { Text("Amount (FCFA)") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (type == TransactionType.EXPENSE) MaterialTheme.colorScheme.error 
                                    else MaterialTheme.colorScheme.primary
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = MaterialTheme.shapes.large
                    )

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { viewModel.formTitle.value = it },
                        label = { Text("Description") },
                        placeholder = { Text("e.g. Lunch at McD") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        shape = MaterialTheme.shapes.large,
                        singleLine = true
                    )

                    // Category
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = MaterialTheme.shapes.large
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            categories.categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name) },
                                    onClick = {
                                        viewModel.formCategory.value = cat.name
                                        categoryExpanded = false
                                    }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Add Custom...", color = MaterialTheme.colorScheme.primary) },
                                onClick = {
                                    categoryExpanded = false
                                    showAddCategoryDialog = true
                                }
                            )
                        }
                    }

                    // Date
                    OutlinedTextField(
                        value = dateFormat.format(Date(date)),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Date") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Select date")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    )

                    // Note
                    OutlinedTextField(
                        value = note,
                        onValueChange = { viewModel.formNote.value = it },
                        label = { Text("Note (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        minLines = 3
                    )
                }
            }

            // ── 3. Action Button ──
            Button(
                onClick = { viewModel.saveTransaction(onTransactionSaved) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = MaterialTheme.shapes.large,
                enabled = title.isNotBlank() && amount.isNotBlank() && category.isNotBlank()
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Confirm Entry", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.formDate.value = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showAddCategoryDialog) {
        var newCategoryName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("New Category") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Category name") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.large
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.addCustomCategory(newCategoryName)
                    viewModel.formCategory.value = newCategoryName.trim()
                    showAddCategoryDialog = false
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) { Text("Cancel") }
            }
        )
    }
}
