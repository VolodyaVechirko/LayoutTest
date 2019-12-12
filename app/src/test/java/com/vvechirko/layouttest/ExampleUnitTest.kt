package com.vvechirko.layouttest

import org.junit.Test

import org.junit.Assert.*

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
    fun test() {
        val size = 10
        val spans = 2
        val p = 0

        for (i in p until size step spans) {
//            println("i - $i")
            if (size - i <= spans) {
                // last item
                println("set $i null")
            } else {
                println("set $i from ${i + spans}")
            }
        }
        assertTrue(true)
    }
}
