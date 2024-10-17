import com.svet.config.CaptureConfig
import com.svet.config.ConfigHelper
import com.svet.config.SvetConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ConfigsTest {

    @Test
    fun loadConfigTest() {
        val svetConfig = SvetConfig()
        Assertions.assertNotNull(svetConfig.captureConfig)
        Assertions.assertNotNull(svetConfig.connectConfig)

//        ConfigHelper.saveConfig("capture-config.json", svetConfig.captureConfig)
//        ConfigHelper.saveConfig("connect-config.json", svetConfig.connectConfig)
    }

    @Test
    fun loadByDefaultTest() {
        val config = ConfigHelper.loadConfig("config/capture-config.json", CaptureConfig::class.java, CaptureConfig.defaultConfig())

        Assertions.assertNotNull(config)
    }

}
