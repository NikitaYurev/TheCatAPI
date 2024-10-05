package com.example.catagentprofile.model

import com.squareup.moshi.Json

class ImageResultData (
    @field:Json(name = "url") val imageUrl: String,
    val breeds : List<CatBreedData>
)