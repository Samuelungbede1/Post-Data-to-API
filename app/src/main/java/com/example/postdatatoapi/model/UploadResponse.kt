package com.example.postdatatoapi.model

data class UploadResponse(
    val status: Int,
    val message: String,
    val downloadUri: String,
)