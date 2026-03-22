package cz.spseiostrava.pham.vypocet.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ProfileDao {

    /** Returns the full profile row for a user (profileID == userID). */
    @Query("SELECT * FROM profile_table WHERE profile_id = :userID")
    suspend fun getProfile(userID: Long): ProfileEntity?

    @Query("UPDATE profile_table SET profile_bio = :bio WHERE profile_id = :userID")
    suspend fun updateBio(userID: Long, bio: String)

    @Query("UPDATE profile_table SET username = :username WHERE profile_id = :userID")
    suspend fun updateUsername(userID: Long, username: String)
}
