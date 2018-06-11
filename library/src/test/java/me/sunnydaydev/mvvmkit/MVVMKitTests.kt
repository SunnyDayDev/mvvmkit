package me.sunnydaydev.mvvmkit

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MVVMKitTests {

    @Test
    fun sorted_set_order() {
        val set: MutableSet<Int> = sortedSetOf(Comparator { l1, l2 -> l2.compareTo(l1) })
        set.add(3)
        set.add(1)
        set.add(2)
        assertEquals(3, set.first())
        assertEquals(2, set.drop(1).first())
        assertEquals(1, set.drop(2).first())
    }

}
