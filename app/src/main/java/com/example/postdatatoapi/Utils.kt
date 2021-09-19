package com.example.postdatatoapi

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.snackbar (message: String){
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).also { snackbar ->
        snackbar.setAction("ok"){
            snackbar.dismiss()
        }
    }.show()
}

fun ContentResolver.getFileName(uri: Uri) : String{
    var name= ""
    var cursor= query(uri, null, null, null, null )
    if (cursor!=null){
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        name =cursor.getString(nameIndex)
        cursor.close()
    }
    return name
}