package ph.edu.auf.marlon_jan.bondoc_quiz3.presentation.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ph.edu.auf.marlon_jan.bondoc_quiz3.data.realm.ExpenseEntity
import ph.edu.auf.marlon_jan.bondoc_quiz3.data.realm.ExpenseRepository

data class ExpenseUiState(
    val categoryId: String = "",
    val categoryName: String = "",
    val expenses: List<ExpenseEntity> = emptyList(),
    val sortByAmountDesc: Boolean = true,
    val fromDateMillis: Long? = null,
    val toDateMillis: Long? = null,
    val isAddEditOpen: Boolean = false,
    val editId: String? = null,
    val titleInput: String = "",
    val amountInput: String = "",
    val dateMillisInput: Long = System.currentTimeMillis()
)

class ExpenseListViewModel(
    private val repo: ExpenseRepository = ExpenseRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(ExpenseUiState())
    val state: StateFlow<ExpenseUiState> = _state.asStateFlow()

    fun init(categoryId: String, categoryName: String) {
        if (state.value.categoryId.isNotEmpty()) return
        _state.update { it.copy(categoryId = categoryId, categoryName = categoryName) }
        reload()
    }

    private fun reload() {
        val st = state.value
        viewModelScope.launch {
            repo.expensesByCategoryFlow(
                st.categoryId,
                st.sortByAmountDesc,
                st.fromDateMillis,
                st.toDateMillis
            ).collect { list ->
                _state.update { it.copy(expenses = list) }
            }
        }
    }

    fun toggleSort() {
        _state.update { it.copy(sortByAmountDesc = !it.sortByAmountDesc) }
        reload()
    }

    fun setFromDate(millis: Long?) { _state.update { it.copy(fromDateMillis = millis) }; reload() }
    fun setToDate(millis: Long?) { _state.update { it.copy(toDateMillis = millis) }; reload() }

    fun openAdd() {
        _state.update {
            it.copy(isAddEditOpen = true, editId = null, titleInput = "", amountInput = "", dateMillisInput = System.currentTimeMillis())
        }
    }

    fun openEdit(exp: ExpenseEntity) {
        _state.update {
            it.copy(
                isAddEditOpen = true,
                editId = exp.id,
                titleInput = exp.title,
                amountInput = exp.amount.toString(),
                dateMillisInput = exp.dateMillis
            )
        }
    }

    fun closeDialog() { _state.update { it.copy(isAddEditOpen = false) } }

    fun updateTitle(v: String) { _state.update { it.copy(titleInput = v) } }
    fun updateAmount(v: String) { _state.update { it.copy(amountInput = v) } }
    fun updateDateMillis(m: Long) { _state.update { it.copy(dateMillisInput = m) } }

    fun submit() {
        val st = state.value
        val amt = st.amountInput.toDoubleOrNull() ?: -1.0
        if (st.titleInput.isBlank() || amt <= 0.0) { closeDialog(); return }
        viewModelScope.launch {
            if (st.editId == null) {
                repo.addExpense(st.categoryId, st.titleInput, amt, st.dateMillisInput)
            } else {
                repo.updateExpense(st.editId, st.titleInput, amt, st.dateMillisInput)
            }
            closeDialog()
        }
    }

    fun delete(expenseId: String) {
        viewModelScope.launch { repo.deleteExpense(expenseId) }
    }
}
