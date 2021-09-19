package com.example.postdatatoapi

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import com.example.postdatatoapi.model.UploadResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class MainActivity : AppCompatActivity(), UploadRequestBody.UploadCallback {
    var TAG = "URL"

    private lateinit var progressBar: ProgressBar
    private lateinit var layout: View


    private lateinit var postButton: Button
    private lateinit var ImageDisplay: ImageView
    private var selectedImage: Uri? = null

    companion object {
        val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.pb_ProgressBar)
        postButton = findViewById(R.id.btn_PostButton)
        ImageDisplay = findViewById(R.id.iv_toUpload)
        layout = findViewById(R.id.layout_root)

        ImageDisplay.setOnClickListener {
            pickImage()
        }

        postButton.setOnClickListener {
            upload()
        }

    }


    private fun upload() {
        if (selectedImage == null) {
            layout.snackbar("Select an Image")
            return
        }


        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(selectedImage!!, "r", null) ?: return

        val file = File(cacheDir, contentResolver.getFileName(selectedImage!!))
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        progressBar.progress = 0
        val body = UploadRequestBody(file, "image", this)

        MyAPI().uploadImage(
            MultipartBody.Part.createFormData("file", file.name, body), RequestBody.create(
                MediaType.parse("multipart/form-data"), "image from my device"
            )
        ).enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                progressBar.progress = 100

                if (response.body() != null) {
                    layout.snackbar(response.body()!!.message)
                    println(response.body()!!.downloadUri)

                } else {
                    layout.snackbar(response.message())
                }

            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                layout.snackbar(t.message!!)
            }
        })

    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE) {
            selectedImage = data?.data
            ImageDisplay.setImageURI(selectedImage)
        }
    }


    override fun onProgressUpdate(percentage: Int) {
        progressBar.progress = percentage
    }
}