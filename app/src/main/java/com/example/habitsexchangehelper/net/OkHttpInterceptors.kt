package com.example.habitsexchangehelper.net

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain
            .request()
            .url()
            .newBuilder()
            .addQueryParameter("apikey", "03b94ce0-60fa-11ec-9daa-9561c48637db")
            .build()

        val request = chain
            .request()
            .newBuilder()
            .url(url)
            .build();
        return chain.proceed(request)
    }
}

internal class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val t1 = System.nanoTime()
        Log.i(NETWORK_LOG_TAG,
            java.lang.String.format(
                "Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()
            )
        )
        val response = chain.proceed(request)
        val t2 = System.nanoTime()
        Log.i(NETWORK_LOG_TAG,
            String.format(
                "Received response for %s in %.1fms",
                response.request().url(), (t2 - t1) / 1e6
            )
        )
        return response
    }
}

private const val NETWORK_LOG_TAG = "NETWORK"