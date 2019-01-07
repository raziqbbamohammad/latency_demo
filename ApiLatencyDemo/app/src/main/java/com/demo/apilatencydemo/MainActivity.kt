package com.demo.apilatencydemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.android.volley.Request
import com.cheapflightsapp.flightbooking.network.volley.GsonRequest
import com.cheapflightsapp.flightbooking.network.volley.VolleyApiClient
import com.demo.apilatencydemo.model.MoviesResponse
import com.demo.apilatencydemo.retrofit.ApiService
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {

    var retrofitClient: Retrofit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRetrofitCall()

        setupVolleyCall()
    }

    private fun setupVolleyCall() {
        btHitWithVolley?.setOnClickListener {
            val url = "$BASE_URL/sample_response"

            val callStartedAt = Calendar.getInstance().timeInMillis
            showVolleyLoader(true)

            val gsonRequest = GsonRequest(
                url,
                Request.Method.GET,
                MoviesResponse::class.java,
                null,
                null,
                com.android.volley.Response.Listener {
                    showVolleyLoader(false)
                    tvVolleyResponse?.text = getString(
                        R.string.volley_success_message,
                        (Calendar.getInstance().timeInMillis - callStartedAt).toString()
                    )

                },
                com.android.volley.Response.ErrorListener {
                    showVolleyLoader(false)
                    tvVolleyResponse?.text = getString(R.string.failed)
                })
            VolleyApiClient.getInstance(this).addToRequestQueue(gsonRequest)

        }
    }

    private fun showVolleyLoader(show: Boolean) {
        btHitWithVolley?.visibility = if (show) View.INVISIBLE else View.VISIBLE
        pbVolley?.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun setupRetrofitCall() {
        btHitWithRetrofit?.setOnClickListener {
            if (retrofitClient == null) {
                val retrofitBuilder = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())

                val okHttpClientBuilder = OkHttpClient.Builder()
                    .readTimeout(API_REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .connectTimeout(API_REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)

                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.level = HttpLoggingInterceptor.Level.BODY
                    okHttpClientBuilder.addInterceptor(logging)
                }

                retrofitClient = retrofitBuilder.client(okHttpClientBuilder.build()).build()
            }

            val call = retrofitClient?.create(ApiService::class.java)?.getMovies()
            val callStartedAt = Calendar.getInstance().timeInMillis
            showRetrofitLoader(true)
            call?.enqueue(object : Callback<MoviesResponse> {
                override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                    showRetrofitLoader(false)
                    tvRetroofitResponse?.text = getString(R.string.failed)
                }

                override fun onResponse(call: Call<MoviesResponse>, response: Response<MoviesResponse>) {
                    showRetrofitLoader(false)
                    if (response.code() == HttpsURLConnection.HTTP_OK) {
                        tvRetroofitResponse?.text = getString(
                            R.string.retrofit_success_message,
                            (Calendar.getInstance().timeInMillis - callStartedAt).toString()
                        )
                    } else {
                        tvRetroofitResponse?.text = getString(R.string.failed)
                    }
                }

            })
        }
    }

    private fun showRetrofitLoader(show: Boolean) {
        btHitWithRetrofit?.visibility = if (show) View.INVISIBLE else View.VISIBLE
        pbRetrofit?.visibility = if (show) View.VISIBLE else View.GONE
    }

    companion object {
        const val BASE_URL = "https://raw.githubusercontent.com/raziqbbamohammad/latency_demo/master/"
        const val API_REQUEST_TIMEOUT = 60
    }
}
