package me.sunnydaydev.mvvmkit

import androidx.test.runner.AndroidJUnit4
import me.sunnydaydev.mvvmkit.binding.ImageViewBindings
import org.hamcrest.Matchers.`is`

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.net.URL

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ImageViewBindingsTest {

    @Test
    fun convertStringToUri() {

        val url = "https://some.host/path?query=1"
        val uri = ImageViewBindings.convertStringToUri(url)

        assertThat(uri?.toString(), `is`(url))

    }

    @Test
    fun convertURLToUri() {

        val urlString = "https://some.host/path?query=1"
        val url = URL(urlString)
        val uri = ImageViewBindings.convertURLToUri(url)

        assertThat(uri?.toString(), `is`(urlString))

    }

}