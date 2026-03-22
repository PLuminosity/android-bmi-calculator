package cz.spseiostrava.pham.vypocet.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BmiDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBmiInfo(bmiInfo: BmiInfoEntity)

    /**
     * Returns a Flow that emits a fresh list every time the table changes.
     * Note: Flow-returning queries must NOT be suspend functions.
     */
    @Query("SELECT * FROM bmi_info_table WHERE profile_id = :profileID ORDER BY measure_date DESC")
    fun getBmiInfoForProfile(profileID: Int): Flow<List<BmiInfoEntity>>

    @Delete
    suspend fun deleteBmiInfo(bmiInfo: BmiInfoEntity)

    @Query("DELETE FROM bmi_info_table WHERE profile_id = :profileID")
    suspend fun deleteAllForProfile(profileID: Int)
}
