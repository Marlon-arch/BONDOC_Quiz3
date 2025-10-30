package ph.edu.auf.marlon_jan.bondoc_quiz3.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ph.edu.auf.marlon_jan.bondoc_quiz3.data.realm.CategoryEntity
import ph.edu.auf.marlon_jan.bondoc_quiz3.data.realm.ExpenseRepository

data class CategoryWithTotal(
    val category: CategoryEntity,
    val total: Double
)

data class CategoryUiState(
    val items: List<CategoryWithTotal> = emptyList(),
    val overallTotal: Double = 0.0,
    val isAddDialogOpen: Boolean = false,
    val newCategoryName: String = ""
)

class CategoryListViewModel(
    private val repo: ExpenseRepository = ExpenseRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryUiState())
    val state: StateFlow<CategoryUiState> = _state.asStateFlow()

    private var totalsById: Map<String, Double> = emptyMap()

    init {
        viewModelScope.launch {
            repo.categoriesFlow().collect { cats ->
                val items = cats.map { c ->
                    CategoryWithTotal(c, totalsById[c.id] ?: 0.0)
                }
                _state.update { it.copy(items = items) }
            }
        }
        viewModelScope.launch {
            repo.overallTotal().collect { total ->
                _state.update { it.copy(overallTotal = total) }
            }
        }
        viewModelScope.launch {
            repo.categoriesFlow().collect { cats ->
                val map = mutableMapOf<String, Double>()
                cats.forEach { cat ->
                    repo.totalForCategory(cat.id).take(1).collect { t ->
                        map[cat.id] = t
                    }
                }
                totalsById = map.toMap()
                _state.update { st ->
                    st.copy(items = st.items.map { it.copy(total = totalsById[it.category.id] ?: 0.0) })
                }
            }
        }
    }

    fun setAddDialog(open: Boolean) {
        _state.update { it.copy(isAddDialogOpen = open, newCategoryName = "") }
    }

    fun updateNewCategoryName(v: String) {
        _state.update { it.copy(newCategoryName = v) }
    }

    fun submitAddCategory() {
        val name = state.value.newCategoryName.trim()
        viewModelScope.launch {
            repo.addCategory(name)
            setAddDialog(false)
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch { repo.deleteCategory(id) }
    }
}
