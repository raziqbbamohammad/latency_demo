package com.cheapflightsapp.flightbooking.network.volley

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleyApiClient constructor(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: VolleyApiClient? = null


        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: VolleyApiClient(context).also {
                    INSTANCE = it
                }
            }

    }

    val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.setShouldCache(false)
        requestQueue.add(req)
    }
}