package cz.spseiostrava.pham.vypocet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "profile_table",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],     // SQL column name in user_table
            childColumns = ["profile_id"],   // SQL column name in profile_table
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "profile_id")
    val profileID: Int,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "profile_bio")
    var bio: String?
)
