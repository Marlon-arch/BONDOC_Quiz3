package ph.edu.auf.marlon_jan.bondoc_quiz3.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import ph.edu.auf.marlon_jan.bondoc_quiz3.presentation.category.CategoryListViewModel
import ph.edu.auf.marlon_jan.bondoc_quiz3.presentation.expense.ExpenseListViewModel
import ph.edu.auf.marlon_jan.bondoc_quiz3.ui.screens.CategoryListScreen
import ph.edu.auf.marlon_jan.bondoc_quiz3.ui.screens.ExpenseListScreen

@Composable
fun AppNavHost(nav: NavHostController) {
    NavHost(navController = nav, startDestination = NavRoutes.CATEGORY_LIST) {
        composable(NavRoutes.CATEGORY_LIST) {
            val vm: CategoryListViewModel = viewModel()
            CategoryListScreen(
                vm = vm,
                onOpenCategory = { id, name -> nav.navigate(NavRoutes.expenseListRoute(id, name)) }
            )
        }
        composable(
            NavRoutes.EXPENSE_LIST,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")!!
            val name = backStackEntry.arguments?.getString("name")!!
            val vm: ExpenseListViewModel = viewModel()
            vm.init(id, name)
            ExpenseListScreen(vm = vm)
        }
    }
}
