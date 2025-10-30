package ph.edu.auf.marlon_jan.bondoc_quiz3.data.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.RealmList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ph.edu.auf.marlon_jan.bondoc_quiz3.App

class ExpenseRepository(
    private val realm: Realm = App.realm
) {

    fun categoriesFlow(): Flow<List<CategoryEntity>> =
        realm.query<CategoryEntity>().asFlow().map { it.list.toList() }

    suspend fun addCategory(name: String) {
        if (name.isBlank()) return
        realm.write {
            copyToRealm(CategoryEntity().apply { this.name = name.trim() })
        }
    }

    suspend fun deleteCategory(id: String) {
        realm.write {
            val exps = query<ExpenseEntity>("categoryId == $0", id).find()
            delete(exps)

            val cat = query<CategoryEntity>("id == $0", id).first().find()
            if (cat != null) {
                delete(cat)
            }
        }
    }

    fun expensesByCategoryFlow(
        categoryId: String,
        sortByAmountDesc: Boolean,
        fromDateMillis: Long?,
        toDateMillis: Long?
    ): Flow<List<ExpenseEntity>> {
        var q = realm.query<ExpenseEntity>("categoryId == $0", categoryId)
        if (fromDateMillis != null) q = q.query("dateMillis >= $0", fromDateMillis)
        if (toDateMillis != null) q = q.query("dateMillis <= $0", toDateMillis)
        val sortOrder = if (sortByAmountDesc) Sort.DESCENDING else Sort.ASCENDING
        return q.sort("amount", sortOrder).asFlow().map { it.list.toList() }
    }

    suspend fun addExpense(categoryId: String, title: String, amount: Double, dateMillis: Long) {
        if (title.isBlank() || amount <= 0.0) return
        realm.write {
            val cat = query<CategoryEntity>("id == $0", categoryId).first().find()
            if (cat != null) {
                val exp = copyToRealm(
                    ExpenseEntity().apply {
                        this.title = title.trim()
                        this.amount = amount
                        this.dateMillis = dateMillis
                        this.categoryId = categoryId
                    }
                )
                findLatest(cat)?.expenses?.add(exp)
            }
        }
    }

    suspend fun updateExpense(expenseId: String, title: String, amount: Double, dateMillis: Long) {
        if (title.isBlank() || amount <= 0.0) return
        realm.write {
            val exp = query<ExpenseEntity>("id == $0", expenseId).first().find()
            exp?.let {
                it.title = title.trim()
                it.amount = amount
                it.dateMillis = dateMillis
            }
        }
    }

    suspend fun deleteExpense(expenseId: String) {
        realm.write {
            val exp = query<ExpenseEntity>("id == $0", expenseId).first().find()
            exp?.let { delete(it) }
        }
    }

    fun totalForCategory(categoryId: String): Flow<Double> =
        realm.query<ExpenseEntity>("categoryId == $0", categoryId)
            .asFlow()
            .map { resultsChange: ResultsChange<ExpenseEntity> ->
                resultsChange.list.sumOf { it.amount }
            }

    fun overallTotal(): Flow<Double> =
        realm.query<ExpenseEntity>()
            .asFlow()
            .map { it.list.sumOf { e -> e.amount } }
}
