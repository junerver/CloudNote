package com.junerver.cloudnote.net


import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import com.junerver.cloudnote.Constants
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

/**
 * 优化过的Bmob报活提交
 */
object BmobMethods {

    private val mRetrofit: Retrofit
    private val BASE_URL = "https://api2.bmob.cn/"
    private val DEFAULT_TIMEOUT = 60L
    var INSTANCE: WebService

    init {
        val httpLoggingInterceptor = LoggingInterceptor.Builder()
            .setLevel(Level.BASIC)
            .log(Platform.INFO)
            .request("Request")
            .response("Response")
            .build()
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .pingInterval(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("X-Bmob-Application-Id", Constants.BMOB_APP_ID)
                    .addHeader("X-Bmob-REST-API-Key", Constants.BMOB_REST_KEY)
                    .build()
                chain.proceed(request)
            }
//            .addInterceptor(httpLoggingInterceptor)
        mRetrofit = Retrofit.Builder()
            .client(builder.build())
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
        INSTANCE = mRetrofit.create(WebService::class.java)
    }


    interface WebService {
        //登录
        @Headers("Content-Type: application/json")
        @GET("1/login")
        suspend fun login(
            @Query("username") username: String,
            @Query("password") password: String
        ): String

        //注册用户
        @Headers("Content-Type: application/json")
        @POST("1/users")
        suspend fun register(
            @Body body: RequestBody
        ): String

        //上传文件
        @POST("2/files/{fileName}")
        suspend fun postFile(
//            @Header("Content-Type") contentType: String,
            @Path("fileName") fileName: String,
            @Body body: RequestBody
        ): String

        //查询单条数据
        @Headers("Content-Type: application/json")
        @GET("1/classes/Note")
        suspend fun getAllNoteByUserId(@Query("where") where:String ): String

        //创建单条数据
        @Headers("Content-Type: application/json")
        @POST("1/classes/Note")
        suspend fun postNote(@Body body: RequestBody): String

        //查询单条数据
        @Headers("Content-Type: application/json")
        @GET("1/classes/Note/{id}")
        suspend fun getNoteById(@Path("id") id: String): String

        //更新单条数据
        @Headers("Content-Type: application/json")
        @PUT("1/classes/Note/{id}")
        suspend fun putNoteById(@Path("id") id: String, @Body body: RequestBody): String

        //查询单条数据
        @Headers("Content-Type: application/json")
        @DELETE("1/classes/Note/{id}")
        suspend fun delNoteById(@Path("id") id: String): String
    }
}


