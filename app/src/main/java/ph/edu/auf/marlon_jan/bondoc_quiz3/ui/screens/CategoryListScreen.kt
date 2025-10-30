@file:OptIn(ExperimentalMaterial3Api::class)

package ph.edu.auf.marlon_jan.bondoc_quiz3.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ph.edu.auf.marlon_jan.bondoc_quiz3.presentation.category.CategoryListViewModel
import ph.edu.auf.marlon_jan.bondoc_quiz3.ui.theme.BluePrimary
import ph.edu.auf.marlon_jan.bondoc_quiz3.ui.theme.GreyCard
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

@Composable
fun CategoryListScreen(
    vm: CategoryListViewModel,
    onOpenCategory: (String, String) -> Unit
) {
    val state by vm.state.collectAsState()

    var pendingDeleteId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Smart Expense Tracker") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { vm.setAddDialog(true) },
                text = { Text("Add Category") },
                icon = {}
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Box(
                Modifier.fillMaxWidth().background(BluePrimary).padding(16.dp)
            ) {
                Text(
                    text = "Overall Total: ₱${"%.2f".format(state.overallTotal)}",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            LazyColumn(
                Modifier.fillMaxSize().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = state.items,
                    key = { it.category.id }
                ) { item ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 0.dp)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(GreyCard)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                Modifier
                                    .weight(1f)
                                    .clickable { onOpenCategory(item.category.id, item.category.name) }
                            ) {
                                Text(item.category.name, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text("Total: ₱${"%.2f".format(item.total)}")
                            }

                            IconButton(onClick = { pendingDeleteId = item.category.id }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete category"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.isAddDialogOpen) {
        AlertDialog(
            onDismissRequest = { vm.setAddDialog(false) },
            title = { Text("New Category") },
            text = {
                OutlinedTextField(
                    value = state.newCategoryName,
                    onValueChange = vm::updateNewCategoryName,
                    label = { Text("Category name (e.g., Food)") },
                    singleLine = true
                )
            },
            confirmButton = { TextButton(onClick = { vm.submitAddCategory() }) { Text("Save") } },
            dismissButton = { TextButton(onClick = { vm.setAddDialog(false) }) { Text("Cancel") } }
        )
    }

    if (pendingDeleteId != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text("Delete Category") },
            text = { Text("This will remove the category and all of its expenses. Continue?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteCategory(pendingDeleteId!!)
                    pendingDeleteId = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) { Text("Cancel") }
            }
        )
    }
}
