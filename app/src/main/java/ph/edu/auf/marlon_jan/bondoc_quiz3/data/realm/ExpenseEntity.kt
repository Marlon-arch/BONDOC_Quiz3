package ph.edu.auf.marlon_jan.bondoc_quiz3.data.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.UUID

class ExpenseEntity : RealmObject {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var title: String = ""
    var amount: Double = 0.0
    var dateMillis: Long = System.currentTimeMillis()
    var categoryId: String = ""
}
