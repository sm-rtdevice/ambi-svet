
import com.svet.processor.ImageProcessorUtils
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class ImageProcessorUtilsTest {

    private val rgbImagePath = "C:\\pictures\\rgb_image.jpg"
    private val grayImagePath = "C:\\pictures\\gray_image.jpg"
    private val avgImagePath = "C:\\pictures\\avg_image.jpg"

    @Test
    @Disabled
    fun toGrayTest() {
        val rgbImage = ImageProcessorUtils.load(rgbImagePath)
        val grayImage = ImageProcessorUtils.toGray(rgbImage)
        ImageProcessorUtils.saveJpg(grayImage, grayImagePath)
    }

    @Test
    @Disabled
    fun getAverageColorTest() {
        val rgbImage = ImageProcessorUtils.load(rgbImagePath)
        val avgColor = ImageProcessorUtils.getAverageColor(rgbImage)
        val result = ImageProcessorUtils.createMonotonousImage(rgbImage.width, rgbImage.height, rgbImage.type, avgColor)
        ImageProcessorUtils.saveJpg(result, avgImagePath)
    }

}
