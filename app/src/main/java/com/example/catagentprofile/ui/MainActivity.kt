package com.example.catagentprofile.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.catagentprofile.utils.GlideImageLoader
import com.example.catagentprofile.utils.ImageLoader
import com.example.catagentprofile.R
import com.example.catagentprofile.api.TheCatApiService
import com.example.catagentprofile.model.ImageResultData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(
                MoshiConverterFactory.create()
            ).build()
    }

    private val theCatApiService by lazy {
        retrofit.create(TheCatApiService::class.java)
    }

    private val agentBreedView: TextView by lazy {
        findViewById(R.id.main_agent_breed_value)
    }

    private val profileImageView: ImageView by lazy {
        findViewById(R.id.main_profile_image)
    }

    private val imageLoader: ImageLoader by lazy {
        GlideImageLoader(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Load initial data
        getCatImageResponse()
    }

    // Create the options menu with the reload button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Handle the toolbar menu item clicks
    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        return when (item.itemId) {
            R.id.action_reload -> {
                // Start the animation
                val reloadIcon = findViewById<View>(R.id.action_reload)
                val rotation = AnimationUtils.loadAnimation(this, R.anim.rotate)
                reloadIcon.startAnimation(rotation)

                // Call reloadData() after starting animation
                reloadData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun reloadData() {
        // Code to reload the data, we call here getCatImageResponse() again.
        getCatImageResponse()
    }

    private fun getCatImageResponse() {
        val call = theCatApiService.searchImages(1, "full")
        call.enqueue(object : Callback<List<ImageResultData>> {
            override fun onFailure(call: Call<List<ImageResultData>>, t: Throwable) {
                Log.e("MainActivity", "Failed to get search results", t)
            }

            override fun onResponse(
                call: Call<List<ImageResultData>>, response: Response<List<ImageResultData>>
            ) {
                if (response.isSuccessful) {
                    val imageResults = response.body()
                    val firstImageUrl =
                        imageResults?.firstOrNull()?.imageUrl.orEmpty()
                    if (!firstImageUrl.isBlank()) {
                        imageLoader.loadImage(firstImageUrl, profileImageView)
                    } else {
                        Log.d("MainActivity", "Missing image URL")
                    }
                    agentBreedView.text = imageResults?.firstOrNull()?.breeds?.firstOrNull()
                        ?.name ?: "Unknown"
                } else {
                    Log.e(
                        "MainActivity",
                        "Failed to get search results\n${
                            response.errorBody()?.string().orEmpty()
                        }"
                    )
                }
            }
        })
    }
}