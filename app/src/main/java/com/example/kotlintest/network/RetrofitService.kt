package com.example.kotlintest.network

import com.example.kotlintest.data.ItemListResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetrofitService{

    @Headers("Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNTgyNDUwMzMwLCJqdGkiOiIzMTBkYTc3MzVjNGU0OGY2YWUyNzNjMGJmZTE5NGM1NCIsInVzZXJfaWQiOjUzMX0.o5KcZde1m2m02pZ7RVCxo_m23GOkW2ERiTWyykQi_0o",
        "content-type: application/json")
    @POST("fetch/cosmetics/")
    fun getCosmetics(@Body body: RequestBody): Call<ItemListResponse>
}