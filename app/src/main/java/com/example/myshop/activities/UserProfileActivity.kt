package com.example.myshop.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myshop.R
import com.example.myshop.model.User
import com.example.myshop.util.Constants
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        var userDetails = User()
        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            userDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }
        userDetails.apply {
            et_first_name.isEnabled = false
            et_first_name.setText(firstName)
            et_last_name.isEnabled = false
            et_last_name.setText(lastName)
            et_email.isEnabled = false
            et_email.setText(email)
        }

    }
}