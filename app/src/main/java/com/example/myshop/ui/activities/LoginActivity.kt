package com.example.myshop.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.myshop.R
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.model.User
import com.example.myshop.util.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(), View.OnClickListener {
    private var email = ""
    private var password = ""
    private val firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        tv_forgot_password.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        tv_register.setOnClickListener(this)

    }


    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.tv_forgot_password -> {
                    startActivity(Intent(this, ForgotPasswordActivity::class.java))
                }
                R.id.btn_login -> {
                    loginUser()
                }

                R.id.tv_register -> {
                    startActivity(Intent(this, RegisterActivity::class.java))

                }
            }
        }

    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_login_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(et_login_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun loginUser() {
        if (validateLoginDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            email = et_login_email.text.toString().trim()
            password = et_login_password.text.toString().trim()
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirestoreClass().getUserDetails(this)
                } else {
                    hideProgressDialog()
                    showErrorSnackBar(task.exception!!.message.toString(), true)
                }
            }
        }
    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()
        if (user.profileCompleted == 0) {
            startActivity(
                Intent(this, UserProfileActivity::class.java)
                    .putExtra(Constants.EXTRA_USER_DETAILS,user)
            )
        } else {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        finish()

    }

}