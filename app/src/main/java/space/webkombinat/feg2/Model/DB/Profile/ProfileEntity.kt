package space.webkombinat.feg2.Model.DB.Profile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_entity")
data class ProfileEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    var name: String?,
    var description: String?,
    val createAt: Long
)