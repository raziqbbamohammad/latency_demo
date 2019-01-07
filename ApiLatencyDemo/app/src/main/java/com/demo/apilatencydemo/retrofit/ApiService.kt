package com.demo.apilatencydemo.retrofit

import com.demo.apilatencydemo.model.MoviesResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("sample_response")
    fun getMovies(): Call<MoviesResponse>
}