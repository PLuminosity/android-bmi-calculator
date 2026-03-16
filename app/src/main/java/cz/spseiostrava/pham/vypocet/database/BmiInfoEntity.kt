package cz.spseiostrava.pham.vypocet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "bmi_info_table",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["profileID"],
            childColumns = ["profileID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class BmiInfoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "bmi_info_id")
    val bmiInfoID: Int,
    @ColumnInfo(name = "profile_id")
    val profileID: Int,
    @ColumnInfo(name = "measure_date")
    val measureDate: Long,
    @ColumnInfo(name = "height")
    val height: Float,
    @ColumnInfo(name = "weight")
    val weight: Float,
    @ColumnInfo(name = "bmi_result")
    val bmiResult: Float
)

