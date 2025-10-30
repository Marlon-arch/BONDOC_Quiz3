@file:OptIn(ExperimentalMaterial3Api::class)

package ph.edu.auf.marlon_jan.bondoc_quiz3.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ph.edu.auf.marlon_jan.bondoc_quiz3.presentation.expense.ExpenseListViewModel
import ph.edu.auf.marlon_jan.bondoc_quiz3.ui.theme.GreyCard
import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

private fun formatDate(millis: Long): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(millis))

@Composable
fun ExpenseListScreen(vm: ExpenseListViewModel) {
    val state by vm.state.collectAsState()
    val ctx = LocalContext.current

    fun showDatePicker(onPick: (Long) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            ctx,
            { _, y, m, d ->
                cal.set(y, m, d, 0, 0, 0)
                cal.set(Calendar.MILLISECOND, 0)
                onPick(cal.timeInMillis)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.categoryName) },
                actions = {
                    TextButton(onClick = { vm.toggleSort() }) {
                        Text(if (state.sortByAmountDesc) "Sort: High→Low" else "Sort: Low→High")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { vm.openAdd() },
                text = { Text("Add Expense") },
                icon = {}
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(12.dp)) {

            // Filters row
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FilterChip(label = "From: ${state.fromDateMillis?.let { formatDate(it) } ?: "-"}") {
                    showDatePicker { vm.setFromDate(it) }
                }
                FilterChip(label = "To: ${state.toDateMillis?.let { formatDate(it) } ?: "-"}") {
                    showDatePicker { vm.setToDate(it) }
                }
                TextButton(onClick = { vm.setFromDate(null); vm.setToDate(null) }) {
                    Text("Clear")
                }
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.expenses, key = { it.id }) { e ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.fillMaxWidth().background(GreyCard).padding(16.dp)) {
                            Text(e.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(4.dp))
                            Text("Amount: ₱${"%.2f".format(e.amount)}")
                            Text("Date: ${formatDate(e.dateMillis)}")
                            Spacer(Modifier.height(8.dp))
                            Row {
                                TextButton(onClick = { vm.openEdit(e) }) { Text("Edit") }
                                Spacer(Modifier.width(8.dp))
                                TextButton(onClick = { vm.delete(e.id) }) { Text("Delete") }
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.isAddEditOpen) {
        AlertDialog(
            onDismissRequest = { vm.closeDialog() },
            title = { Text(if (state.editId == null) "Add Expense" else "Edit Expense") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = state.titleInput,
                        onValueChange = vm::updateTitle,
                        label = { Text("Title (e.g., Lunch)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.amountInput,
                        onValueChange = vm::updateAmount,
                        label = { Text("Amount (₱)") },
                        singleLine = true
                    )
                    Row {
                        Text("Date: ${formatDate(state.dateMillisInput)}")
                        Spacer(Modifier.width(12.dp))
                        TextButton(onClick = {
                            val cal = Calendar.getInstance()
                            DatePickerDialog(
                                ctx,
                                { _, y, m, d ->
                                    cal.set(y, m, d, 0, 0, 0)
                                    cal.set(Calendar.MILLISECOND, 0)
                                    vm.updateDateMillis(cal.timeInMillis)
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }) { Text("Pick Date") }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { vm.submit() }) { Text("Save") } },
            dismissButton = { TextButton(onClick = { vm.closeDialog() }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun FilterChip(label: String, onClick: () -> Unit) {
    AssistChip(onClick = onClick, label = { Text(label) })
}
