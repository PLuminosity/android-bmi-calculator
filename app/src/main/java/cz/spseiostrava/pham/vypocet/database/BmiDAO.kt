package cz.spseiostrava.pham.vypocet.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BmiDAO {
    @Insert
    suspend fun insertBmiInfo(bmiInfo: BmiInfoEntity)

    @Query("SELECT * FROM bmi_info_table WHERE profile_id = :profileID ORDER BY measure_date DESC")
    suspend fun getBmiInfoForProfile(profileID: Int): LiveData<List<BmiInfoEntity>>

    @Delete
    suspend fun deleteBmiInfo(bmiInfo: BmiInfoEntity)
}