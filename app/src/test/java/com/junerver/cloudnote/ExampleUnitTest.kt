package com.junerver.cloudnote

import com.dslx.digtalclassboard.net.BmobMethods
import com.edusoa.ideallecturer.createJsonRequestBody
import com.edusoa.ideallecturer.toBean
import com.edusoa.ideallecturer.toJson
import com.junerver.cloudnote.bean.GetAllNoteResp
import com.junerver.cloudnote.db.entity.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import okhttp3.RequestBody.Companion.asRequestBody
import org.junit.Assert
import org.junit.Test
import java.io.File

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
        //测试登录
//        val resp = BmobMethods.INSTANCE.login("test", "123")

        //测试查表
//        val resp = BmobMethods.INSTANCE.getNoteById("2352b66471")
//        val note = resp.toBean<Note>()
        //{"content":"这是一个测试","createdAt":"2020-12-28 20:36:41","dbId":1609159001601,"image":"/storage/emulated/0/1609158993902.png","objectId":"2352b66471","summary":"","title":"122212","updatedAt":"2021-10-14 09:35:58","userObjId":"12","video":"/storage/emulated/0/1609158191429.mp4"}

        //测试put编辑
//        val resp = BmobMethods.INSTANCE.putNoteById(
//            "2352b66471",
            """{"content": "这是一个测试new"}""".createJsonRequestBody()
//        )
        // {"updatedAt":"2021-10-14 09:35:58"}

        //测试新建
        val note = Note()
        note.content = "这是一个测试创建"
        note.createdAt= "2020-10-11 09:45:34"
        println(note.toJson(excludeFields = listOf( "_updatedTime", "_createdTime")))
        val resp = BmobMethods.INSTANCE.postNote(note.toJson(excludeFields = Constants.DEFAULT_EXCLUDE_FIELDS).createJsonRequestBody())
        //{"createdAt":"2021-10-14 09:46:25","objectId":"3bc606fc83"}

        //测试删除
//        val resp = BmobMethods.INSTANCE.delNoteById("8f6b56bbdd")
        //{"msg":"ok"}  //重复删除则400

        //查询某个用户的全部数据
//        val map = mapOf("userObjId" to "testw")
//        val resp = BmobMethods.INSTANCE.getAllNoteByUserId(map.toJson())
        //{"results":[{"content":"123456","createdAt":"2021-10-13 11:37:04","dbId":1634096224294,"objectId":"edbeac9ca2","summary":"123456","title":"123456","updatedAt":"2021-10-13 11:37:04","userObjId":"testw"}]}

        println(resp)
        delay(10000)
    }

    @Test
    fun testFile()=runBlocking {
        val file = File("/Users/houwenjun/wallhaven-o35e69.jpg")
        val resp = BmobMethods.INSTANCE.postFile("test.jpg",file.asRequestBody())

        println(resp)
        delay(10000)
    }

    @Test
    fun testFlow() = runBlocking {

        flow<String> {
            val map = mapOf("userObjId" to "testw")
            println(map)
            val resp = BmobMethods.INSTANCE.getAllNoteByUserId(map.toJson())
            emit(resp)
        }.map {

            println()
            println("${Thread.currentThread().name}: $it")

            val allNote = it.toBean<GetAllNoteResp>()
            allNote
        }.flatMapConcat {
            it.results.asFlow()
        }.onEach {
            println()
            println("${Thread.currentThread().name}: $it")
        }
            .flowOn(Dispatchers.IO)
            .onCompletion {
                println()
                println("${Thread.currentThread().name}: onCompletion")
            }
            .collect {
                println()
                println("${Thread.currentThread().name}: $it")
            }



        delay(10000)
    }
}