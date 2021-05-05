package com.example.myshop.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.example.myshop.R
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.model.User
import com.example.myshop.util.Constants
import com.example.myshop.util.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mUserDetails : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setUpActionBar()
        tv_edit.setOnClickListener(this)
        btn_logout.setOnClickListener(this)
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar_settings_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
        }
        toolbar_settings_activity.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getUserDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this)
    }

    fun userDetailsSuccess(user: User) {
        mUserDetails = user
        hideProgressDialog()
        GlideLoader(this).loadUserPicture(user.image, iv_user_photo)
        user.apply {
            tv_name.text = "${firstName} ${lastName}"
            tv_gender.text = gender
            tv_email.text = email
            tv_mobile_number.text = mobile.toString()
        }
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this,LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.tv_edit -> {
                    val intent = Intent(this,UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS,mUserDetails)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}