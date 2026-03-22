package cz.spseiostrava.pham.vypocet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bmi_info_table",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["profile_id"],  // SQL column name in profile_table
            childColumns = ["profile_id"],   // SQL column name in bmi_info_table
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["profile_id"])]
)
data class BmiInfoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "bmi_info_id")
    val bmiInfoID: Int = 0,         // 0 → Room generates the ID on insert
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
