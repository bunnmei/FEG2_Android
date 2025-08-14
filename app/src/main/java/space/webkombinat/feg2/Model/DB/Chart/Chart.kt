package space.webkombinat.feg2.Model.DB.Chart

data class Chart(
    val id: Long,
    val profileId: Long,
    val pointIndex: Int,
    val temp: Int
)
