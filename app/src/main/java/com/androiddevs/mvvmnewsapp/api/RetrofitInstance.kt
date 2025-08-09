package com.androiddevs.mvvmnewsapp.api

import com.androiddevs.mvvmnewsapp.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A singleton object to provide a single, app-wide instance of Retrofit.
 */
class RetrofitInstance {

    companion object {

        // `lazy` ensures the Retrofit instance is created only when it's first needed.
        private val retrofit by lazy {
            // Create a logging interceptor to see request and response logs in Logcat.
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            // Create an OkHttpClient and add the logging interceptor.
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            // Build the Retrofit instance.
            Retrofit.Builder()
                .baseUrl(BASE_URL) // Set the base URL for all requests.
                .addConverterFactory(GsonConverterFactory.create()) // Use Gson to convert JSON to Kotlin objects.
                .client(client) // Set the custom OkHttpClient.
                .build()
        }

        /**
         * Provides a lazily-initialized implementation of the NewsApi interface.
         */
        val api by lazy {
            retrofit.create(NewsApi::class.java)
        }
    }

}