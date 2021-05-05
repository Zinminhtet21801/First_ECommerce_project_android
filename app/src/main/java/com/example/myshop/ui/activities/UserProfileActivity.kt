package com.example.myshop.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.myshop.R
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.model.User
import com.example.myshop.util.Constants
import com.example.myshop.util.Constants.PICK_IMAGE_REQUEST_CODE
import com.example.myshop.util.Constants.READ_STORAGE_PERMISSION_CODE
import com.example.myshop.util.GlideLoader
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException


class UserProfileActivity : BaseActivity(), View.OnClickListener {
    private var mUserProfileImageURL: String = ""
    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mUserDetails: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        et_first_name.setText(mUserDetails.firstName)
        et_last_name.setText(mUserDetails.lastName)
        et_email.isEnabled = false
        et_email.setText(mUserDetails.email)
        if (mUserDetails.profileCompleted == 0) {
            tv_title.text = resources.getString(R.string.title_complete_profile)
            et_first_name.isEnabled = false
            et_last_name.isEnabled = false
        } else {
            setUpActionBar()
            tv_title.text = resources.getString(R.string.title_edit_profile)
            mUserDetails.apply {
                et_first_name.setText(firstName)
                et_last_name.setText(lastName)
                et_email.isEnabled = false
                et_email.setText(email)
                if (mobile != 0L) {
                    et_mobile_number.setText(mobile.toString())
                }
                if (gender == "male") {
                    rb_male.isChecked = true
                } else if (gender == "female") {
                    rb_female.isChecked = true
                }
                mUserProfileImageURL = image
                Glide.with(this@UserProfileActivity)
                    .load(mUserProfileImageURL)
                    .placeholder(R.drawable.ic_user_placeholder)
                    .centerCrop()
                    .into(iv_user_photo)
            }


        }


        iv_user_photo.setOnClickListener(this)

        btn_submit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_user_photo -> {
                    checkPermissions()
                }

                R.id.btn_submit -> {
                    if (validateUserProfileDetails()) {
                        showProgressDialog(resources.getString(R.string.please_wait))
                        if (mSelectedImageFileUri != null) {
                            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri, Constants.USER_PROFILE_IMAGE)
                        } else {
                            updateUserProfileDetails()
                        }
                    }

                }
            }
        }
    }

    private fun updateUserProfileDetails() {
        //                        showErrorSnackBar("Your details are valid. You can update them.",false)
        val userHashMap = HashMap<String, Any>()
        val firstName = et_first_name.text.toString().trim()
        val lastName = et_last_name.text.toString().trim()
        if(firstName != mUserDetails.firstName){
            userHashMap[Constants.FIRST_NAME] = firstName
        }
        if(lastName != mUserDetails.lastName){
            userHashMap[Constants.LAST_NAME] = lastName
        }
        val mobileNumber = et_mobile_number.text.toString().trim()
        val gender = if (rb_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }
        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }
        if(gender.isNotEmpty() && gender != mUserDetails.gender){
            userHashMap[Constants.GENDER] = gender
        }
        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        userHashMap[Constants.GENDER] = gender
        userHashMap[Constants.COMPLETE_PROFILE] = 1
//                        imageExtension = MimeTypeMap.getSingleton()
//                            .getExtensionFromMimeType(contentResolver.getType(imageUri!!))
//                        var storageRef =
//                            FirebaseStorage.getInstance().reference.child("image $imageUri.$imageExtension")
//                        storageRef.putFile(imageUri!!)
//                            .addOnSuccessListener {
//                                Log.d("ImageUpdate", "image $imageUri.$imageExtension")
//                            }
//                        userHashMap[Constants.IMAGE] = "image $imageUri.$imageExtension"

//        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
//            showErrorSnackBar("You already have the storage permission.", false)
            Constants.showImageChooser(this)
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION_CODE
            )
        }
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (READ_STORAGE_PERMISSION_CODE == requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                showErrorSnackBar("Storage Permission is granted", false)
                Constants.showImageChooser(this)
            } else {
                Toast.makeText(this, R.string.read_storage_permission_denied, Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                try {
                    mSelectedImageFileUri = data.data!!
//                    iv_user_photo.setImageURI(imageUri)
                    GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, iv_user_photo)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        resources.getString(R.string.image_selection_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_mobile_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            else -> true
        }
    }

    fun imageUploadSuccess(imageURL: String) {
//        hideProgressDialog()
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }

    fun setUpActionBar() {
        setSupportActionBar(toolbar_user_profile_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        toolbar_user_profile_activity.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


}