package com.example.myshop.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap

object Constants {
    const val USERS : String = "users"
    const val FIRST_NAME = "firstName"
    const val LAST_NAME = "lastName"
    const val MYSHOP_PREFERENCES = "MyShopPrefs"
    const val LOGGED_IN_USERNAME = "logged_in_username"
    const val EXTRA_USER_DETAILS = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val MALE = "male"
    const val FEMALE = "female"
    const val MOBILE = "mobile"
    const val GENDER  = "gender"
    const val USER_PROFILE_IMAGE = "User_Profile_Image"
    const val IMAGE = "image"
    const val COMPLETE_PROFILE = "profileCompleted"
    const val PRODUCT_IMAGE = "Product_Image"
    const val PRODUCTS = "products"
    const val USER_ID = "user_id"

    fun showImageChooser(activity : Activity){
        val gallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(gallery, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity,uri : Uri?) : String?{
        return activity.contentResolver.getType(uri!!)!!.split("/")[1]
    }
}