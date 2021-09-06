package com.example.dgtechhealthcare.utils

import com.example.dgtechhealthcare.signin.Userdata
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface SignInRetrofit {

    @GET("{id}.json")
    fun getAccountType(@Path("id") id:String) : Call<Userdata>

    companion object {
        val BASE_URL = "https://testdatabase-8dfa3-default-rtdb.firebaseio.com/Users/"

        fun getInstance() : SignInRetrofit {

            val builder = Retrofit.Builder()
            builder.addConverterFactory(GsonConverterFactory.create())
            builder.baseUrl(BASE_URL)

            val retrofit = builder.build()
            return retrofit.create(SignInRetrofit::class.java)
        }
    }
}