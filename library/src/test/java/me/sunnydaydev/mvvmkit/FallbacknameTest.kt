package me.sunnydaydev.mvvmkit

import junit.framework.Assert.assertEquals
import org.junit.Test

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 05.09.2018.
 * mail: mail@sunnydaydev.me
 */

class FallbacknameTest {

    @Test
    fun fallbackName() {
        val rawName = "isFlagged"
        assertEquals("flagged", rawName[2].toLowerCase() + rawName.substring(3))
    }

}