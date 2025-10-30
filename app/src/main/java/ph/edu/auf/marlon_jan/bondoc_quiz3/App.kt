package ph.edu.auf.marlon_jan.bondoc_quiz3

import android.app.Application
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import ph.edu.auf.marlon_jan.bondoc_quiz3.data.realm.CategoryEntity
import ph.edu.auf.marlon_jan.bondoc_quiz3.data.realm.ExpenseEntity

class App : Application() {
    companion object {
        lateinit var realm: Realm
            private set
    }

    override fun onCreate() {
        super.onCreate()
        val config = RealmConfiguration.Builder(
            schema = setOf(CategoryEntity::class, ExpenseEntity::class)
        )
            .name("smart_expense_tracker.realm")
            .build()
        realm = Realm.open(config)
    }
}
