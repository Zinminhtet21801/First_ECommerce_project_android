package com.example.myshop.activities

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.myshop.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : BaseActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var email = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        btn_submit.setOnClickListener {
            email = et_email_forgot.text.toString().trim()
            if (email.isNotEmpty()){
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Toast.makeText(this,"Email-Reset link has been successfully sent to your email!!!",Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        Toast.makeText(this,task.exception!!.message,Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                et_email_forgot.error = "Please Enter Email Address..."
                et_email_forgot.requestFocus()
                return@setOnClickListener
            }
        }
    }
}