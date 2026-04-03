package com.nts.financemanager.ui.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add Transaction",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // ── Income / Expense Toggle ───────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterChip(
                modifier = Modifier.weight(1f),
                selected = type == TransactionType.EXPENSE,
                onClick = { viewModel.formType.value = TransactionType.EXPENSE },
                label = { Text("Expense") }
            )
            FilterChip(
                modifier = Modifier.weight(1f),
                selected = type == TransactionType.INCOME,
                onClick = { viewModel.formType.value = TransactionType.INCOME },
                label = { Text("Income") }
            )
        }

        // ── Title ─────────────────────────────────
        OutlinedTextField(
            value = title,
            onValueChange = { viewModel.formTitle.value = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // ── Amount ────────────────────────────────
        OutlinedTextField(
            value = amount,
            onValueChange = { viewModel.formAmount.value = it },
            label = { Text("Amount (FCFA)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // ── Category Dropdown ─────────────────────
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = it }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
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
                // Add custom category option
                HorizontalDivider()
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Custom Category")
                        }
                    },
                    onClick = {
                        categoryExpanded = false
                        showAddCategoryDialog = true
                    }
                )
            }
        }

        // ── Date Picker ───────────────────────────
        OutlinedTextField(
            value = dateFormat.format(Date(date)),
            onValueChange = {},
            readOnly = true,
            label = { Text("Date") },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select date")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // ── Note (Optional) ──────────────────────
        OutlinedTextField(
            value = note,
            onValueChange = { viewModel.formNote.value = it },
            label = { Text("Note (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 3
        )

        // ── Save Button ──────────────────────────
        Button(
            onClick = { viewModel.saveTransaction(onTransactionSaved) },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && amount.isNotBlank() && category.isNotBlank()
        ) {
            Text("Save Transaction", modifier = Modifier.padding(vertical = 4.dp))
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // ── Date Picker Dialog ────────────────────────
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        viewModel.formDate.value = it
                    }
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

    // ── Add Category Dialog ───────────────────────
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
                    singleLine = true
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
