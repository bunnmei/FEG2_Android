package space.webkombinat.feg2.Model.DB

import androidx.room.Database
import androidx.room.RoomDatabase
import space.webkombinat.feg2.Model.DB.Chart.ChartDao
import space.webkombinat.feg2.Model.DB.Chart.ChartEntity
import space.webkombinat.feg2.Model.DB.Profile.ProfileDao
import space.webkombinat.feg2.Model.DB.Profile.ProfileEntity

@Database(
    entities = [ProfileEntity::class, ChartEntity::class],
    version = 1,
    exportSchema = true
)
abstract class ProfileDatabase: RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun chartDao(): ChartDao
}