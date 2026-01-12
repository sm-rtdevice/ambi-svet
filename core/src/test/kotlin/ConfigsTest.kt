import com.svet.config.CaptureConfig
import com.svet.config.ConfigHelper
import com.svet.config.SvetConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ConfigsTest {

    @Test
    fun loadConfigTest() {
        Assertions.assertNotNull(SvetConfig.captureConfig())
        Assertions.assertNotNull(SvetConfig.connectConfig())
    }

    @Test
    fun loadByDefaultTest() {
        val config = ConfigHelper.load("config/capture-config.json", CaptureConfig::class.java)

        Assertions.assertNull(config)
    }

}
