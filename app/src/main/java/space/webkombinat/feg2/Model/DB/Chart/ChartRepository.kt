package space.webkombinat.feg2.Model.DB.Chart

class ChartRepository(
    private val chartDao: ChartDao
) {
    suspend fun insertChart(point: ChartEntity): Int {
        chartDao.create(point)
        return point.pointIndex
    }

    suspend fun deleteChart(point: ChartEntity) {
        chartDao.delete(point)
    }

}