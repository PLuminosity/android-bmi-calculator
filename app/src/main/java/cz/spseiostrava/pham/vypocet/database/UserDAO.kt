package cz.spseiostrava.pham.vypocet.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Transaction
import org.mindrot.jbcrypt.BCrypt
import androidx.room.Query

@Dao
interface UserDAO {
    @Insert
    suspend fun insertUser(user: UserEntity): Long

    @Insert
    suspend fun insertProfile(profile: ProfileEntity)


    @Transaction
    suspend fun createUserWithProfile(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        name: String,
        bio: String
    ) {
        var hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        val generatedID = insertUser(
            UserEntity(
                firstName = firstName,
                lastName = lastName,
                email = email,
                passwordHash = hashedPassword
            )
        )

        insertProfile(
            ProfileEntity(
                profileID = generatedID.toInt(),
                username = name,
                bio = bio
            )
        )
    }

    @Query("UPDATE user_table SET email = :email WHERE user_id = :userID")
    suspend fun updateEmail(userID: Long, email: String)

    @Query("UPDATE user_table SET password_hash = :passwordHash WHERE user_id = :userID")
    suspend fun updatePasswordHash(userID: Long, passwordHash: String)

    @Transaction
    suspend fun changePassword(userID: Long, newPlainTextPassword: String) {
        val newHash = BCrypt.hashpw(newPlainTextPassword, BCrypt.gensalt())

        updatePasswordHash(userID, newHash)
    }
    @Query("SELECT user_table.first_name, user_table.last_name, user_table.email FROM user_table WHERE user_id = :userID")
    suspend fun getUser(userID: Long): UserEntity

    @Query("SELECT user_table.password_hash FROM user_table WHERE user_id = :userID")
    suspend fun getPasswordHash(userID: Long): String
}

