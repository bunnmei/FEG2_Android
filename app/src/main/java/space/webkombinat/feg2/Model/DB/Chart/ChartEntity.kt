package space.webkombinat.feg2.Model.DB.Chart

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chart_entity")
data class ChartEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "profile_id")
    val profileId: Long,
    val pointIndex: Int,
    val temp: Int
)