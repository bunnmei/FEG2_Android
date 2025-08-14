package space.webkombinat.feg2.Model.DB

import androidx.room.Embedded
import androidx.room.Relation
import space.webkombinat.feg2.Model.DB.Chart.ChartEntity
import space.webkombinat.feg2.Model.DB.Profile.ProfileEntity

data class ProfileLinkChart (
    @Embedded
    var profile: ProfileEntity,
    @Relation(parentColumn = "id", entityColumn = "profile_id")
    var chart: List<ChartEntity> = emptyList()
)