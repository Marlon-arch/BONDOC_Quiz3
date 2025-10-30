package ph.edu.auf.marlon_jan.bondoc_quiz3.data.realm

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.UUID

class CategoryEntity : RealmObject {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var name: String = ""
    var expenses: RealmList<ExpenseEntity> = realmListOf()
}
