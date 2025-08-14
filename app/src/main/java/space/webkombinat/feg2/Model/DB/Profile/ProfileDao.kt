package space.webkombinat.feg2.Model.DB.Profile

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import space.webkombinat.feg2.Model.DB.ProfileLinkChart

@Dao
interface ProfileDao {

    @Insert
    suspend fun create(profile: ProfileEntity): Long

    @Query("SELECT * FROM profile_entity WHERE id = :id")
    suspend fun read(id: Long): ProfileEntity?

    @Update
    suspend fun update(profile: ProfileEntity)

    @Delete
    suspend fun delete(profile: ProfileEntity)


    @Query("SELECT * FROM profile_entity order by createAt desc")
    fun getProfileAll(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profile_entity WHERE id = :id")
    fun profileAndChart(id: Long): ProfileLinkChart
}