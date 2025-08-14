package space.webkombinat.feg2.Model.DB.Chart

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert

@Dao
interface ChartDao {
    @Insert
    suspend fun create(point: ChartEntity): Long

    @Delete
    suspend fun delete(point: ChartEntity)
}