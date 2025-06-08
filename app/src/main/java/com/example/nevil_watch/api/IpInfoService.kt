package com.example.nevil_watch.api

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Headers

data class IpInfoResponse(
    val ip: String,
    val city: String,
    val region: String,
    val country: String,
    val loc: String,
    val org: String,
    val postal: String,
    val timezone: String
)

interface IpInfoService {
    @Headers("Accept: application/json")
    @GET("lite/{ip}")
    suspend fun getIpInfo(
        @Query("ip") ip: String,
        @Query("token") token: String = "224f15b933e521"
    ): IpInfoResponse
}

object IpInfoApi {
    private const val BASE_URL = "https://api.ipinfo.io/"
    
    private val retrofit = retrofit2.Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
        .build()

    val service: IpInfoService = retrofit.create(IpInfoService::class.java)
} 