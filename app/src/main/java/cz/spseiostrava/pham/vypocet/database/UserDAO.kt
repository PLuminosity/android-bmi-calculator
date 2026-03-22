package cz.spseiostrava.pham.vypocet.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import org.mindrot.jbcrypt.BCrypt

@Dao
interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProfile(profile: ProfileEntity)

    /** Full registration: creates a UserEntity + matching ProfileEntity in one transaction. */
    @Transaction
    suspend fun createUserWithProfile(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        username: String,
        bio: String
    ) {
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
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
                username = username,
                bio = bio.ifBlank { null }
            )
        )
    }

    /** Look up a user by e-mail address (used during login). */
    @Query("SELECT * FROM user_table WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    /** Fetch all columns for a user by ID. */
    @Query("SELECT * FROM user_table WHERE user_id = :userID")
    suspend fun getUser(userID: Long): UserEntity?

    @Query("UPDATE user_table SET email = :email WHERE user_id = :userID")
    suspend fun updateEmail(userID: Long, email: String)

    @Query("UPDATE user_table SET password_hash = :passwordHash WHERE user_id = :userID")
    suspend fun updatePasswordHash(userID: Long, passwordHash: String)

    @Transaction
    suspend fun changePassword(userID: Long, newPlainTextPassword: String) {
        val newHash = BCrypt.hashpw(newPlainTextPassword, BCrypt.gensalt())
        updatePasswordHash(userID, newHash)
    }
}
