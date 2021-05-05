package com.example.myshop.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.myshop.R
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity() {
    private var email = ""
    private var password = ""
    private lateinit var firebaseUser : FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        tv_login.setOnClickListener {
            onBackPressed()
        }

        setUpActionBar()
        btn_register.setOnClickListener {
            registerUser()

        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar_register_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        toolbar_register_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    private fun validateRegisterDetails(): Boolean {

        return when {
            TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(et_register_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(et_register_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(et_register_confirm_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true
                )
                false
            }

            et_register_password.text.toString()
                .trim { it <= ' ' } != et_register_confirm_password.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true
                )
                false
            }
            !cb_terms_and_condition.isChecked -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_agree_terms_and_condition),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    private fun registerUser() {
        if (validateRegisterDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            email = et_register_email.text.toString().trim { it <= ' ' }
            password = et_register_password.text.toString().trim { it <= ' ' }
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseUser = task.result!!.user
                        val user = User(
                            firebaseUser.uid,
                            et_first_name.text.toString().trim(),
                            et_last_name.text.toString().trim(),
                            email
                        )

                        FirestoreClass().registerUser(this@RegisterActivity,user)

                    } else {
                        hideProgressDialog()
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            task.exception?.message.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    fun userRegistrationSuccess(){
        hideProgressDialog()

        Toast.makeText(this,resources.getString(R.string.register_success),Toast.LENGTH_SHORT).show()
        startActivity(
            Intent(this, DashboardActivity::class.java).putExtra(
                "user_id",
                firebaseUser.uid
            ).putExtra("email_id", email)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()
    }


}