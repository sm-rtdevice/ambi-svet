import com.svet.config.CaptureConfig
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.awt.Color
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage

class CaptureTest {
   // private lateinit var config: CaptureConfig

    private val screenRect = Rectangle(Toolkit.getDefaultToolkit().screenSize) // 1920*1800
    private lateinit var capture: BufferedImage

    private var startTime = 0L
    private var fps = 0L

    private var numLeds: Int = 94
    private val robot = Robot()

    @Test
    @Disabled
    fun captureFullHdFpsTest() { // fps: 30
        startTime = System.currentTimeMillis()

        while (true) {
            capture = robot.createScreenCapture(screenRect) // 1920*1800
            val color = Color(capture.getRGB(100, 100))

            ++fps
            if (System.currentTimeMillis() - startTime > 1000) {
                startTime = System.currentTimeMillis()
                println("$color; fps: $fps")
                fps = 0
            }
        }
    }

    @Test
    @Disabled
    fun capture4KFpsTest() { // fps: 10
        startTime = System.currentTimeMillis()

        while (true) {
            capture = robot.createScreenCapture(Rectangle(0, 0, 3840, 2160))
            val color = Color(capture.getRGB(100, 100))

            ++fps
            if (System.currentTimeMillis() - startTime > 1000) {
                startTime = System.currentTimeMillis()
                println("$color; fps: $fps")
                fps = 0
            }
        }
    }

    @Test
    @Disabled
    fun multiCaptureFpsTest() { // fps: 1
        startTime = System.currentTimeMillis()

        while (true) {
            lateinit var color: Color

            for (i in 1..numLeds) {
                capture = robot.createScreenCapture(Rectangle(0, 0, 100, 100))
                color = Color(capture.getRGB(5, 5))
            }

            ++fps
            if (System.currentTimeMillis() - startTime > 1000) {
                startTime = System.currentTimeMillis()
                println("$color; fps: $fps")
                fps = 0
            }
        }
    }

    @Test
    @Disabled
    fun multiCapture4RegionsFpsTest() { // fps: 16

        val config = CaptureConfig()
        startTime = System.currentTimeMillis()

        while (true) {
           capture = robot.createScreenCapture(Rectangle(0, 0, config.width, config.captureRegionHeight))
           capture = robot.createScreenCapture(Rectangle(0,config.height - config.captureRegionHeight, config.width, config.captureRegionHeight))
           capture = robot.createScreenCapture(Rectangle(0, 0, config.captureRegionWidth, config.height))
           capture = robot.createScreenCapture(Rectangle(config.width - config.captureRegionWidth, 0, config.captureRegionWidth, config.height))

            ++fps
            if (System.currentTimeMillis() - startTime > 1000) {
                startTime = System.currentTimeMillis()
                //println("$color; fps: $fps")
                println("fps: $fps")
                fps = 0
            }
        }
    }

}
