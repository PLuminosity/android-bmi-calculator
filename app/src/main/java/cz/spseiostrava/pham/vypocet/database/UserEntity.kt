package cz.spseiostrava.pham.vypocet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "user_table")
class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    val userID: Int = 1,
    @ColumnInfo(name = "first_name")
    var firstName: String,
    @ColumnInfo(name = "last_name")
    var lastName: String,
    @ColumnInfo(name = "email")
    var email: String,
    @ColumnInfo(name = "password_hash")
    var passwordHash: String,
    @ColumnInfo(name = "created_at")
    var createdAt: Long = System.currentTimeMillis()


)