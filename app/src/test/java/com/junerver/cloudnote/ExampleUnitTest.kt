package com.junerver.cloudnote

import com.dslx.digtalclassboard.net.BmobMethods
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import java.lang.Exception
import kotlin.Throws

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        Assert.assertEquals(4, (2 + 2).toLong())
    }

    @Test
    fun testApi() = runBlocking {
        val resp = BmobMethods.INSTANCE.login("test", "123")
        println(resp)
        delay(10000)
    }
}