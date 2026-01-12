import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class UtilsTest {

    @Test
    fun crcTest() {
        val hi = 0
        val lo = 0
        val chk = 0x55
        Assertions.assertEquals(chk, hi xor lo xor 0x55)
    }

}
