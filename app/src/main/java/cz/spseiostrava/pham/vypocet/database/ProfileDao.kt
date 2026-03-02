package cz.spseiostrava.pham.vypocet.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ProfileDao {
    @Query("UPDATE profile_table SET profile_bio = :bio WHERE profile_id = :userID")
    suspend fun updateBio(userID: Long, bio: String)

    @Query("UPDATE profile_table SET username = :username WHERE profile_id = :userID")
    suspend fun updateUsername(userID: Long, username: String)

    @Query("SELECT profile_table.username, profile_table.profile_bio FROM profile_table WHERE profile_id = :userID")
    suspend fun getProfile(userID: Long): ProfileEntity
}