package space.webkombinat.feg2

import org.junit.Test

import org.junit.Assert.*
import space.webkombinat.feg2.Model.Constants
import space.webkombinat.feg2.Model.Constants.encodeTemp

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testEncodeTemp(){
        val temp1 = -200.0f
        val temp2 = -153.9f
        val temp3 = -10.3f
        val temp4 = -1.1f
        val temp5 = 0f
        val temp6 = 1.9f
        val temp7 = 1350.0f

        val encoded1 = encodeTemp(temp1)
        val encoded2 = encodeTemp(temp2)
        val encoded3 = encodeTemp(temp3)
        val encoded4 = encodeTemp(temp4)
        val encoded5 = encodeTemp(temp5)
        val encoded6 = encodeTemp(temp6)
        val encoded7 = encodeTemp(temp7)

        val result1 = 0
        val result2 = 461
        val result3 = 1897
        val result4 = 1989
        val result5 = 2000
        val result6 = 2019
        val result7 = 15500

        assertEquals(encoded1, result1)
        assertEquals(encoded2, result2)
        assertEquals(encoded3, result3)
        assertEquals(encoded4, result4)
        assertEquals(encoded5, result5)
        assertEquals(encoded6, result6)
        assertEquals(encoded7, result7)
    }

    fun packTemp(temp_f: Int, temp_s: Int): Int {
        return (temp_f.shl(14) or temp_s)
    }
}