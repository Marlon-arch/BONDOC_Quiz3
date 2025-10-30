package ph.edu.auf.marlon_jan.bondoc_quiz3.ui.navigation

object NavRoutes {
    const val CATEGORY_LIST = "categories"
    const val EXPENSE_LIST = "expenses/{id}/{name}"
    fun expenseListRoute(id: String, name: String) = "expenses/$id/$name"
}