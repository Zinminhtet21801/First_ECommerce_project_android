package com.example.myshop.firestore

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.myshop.activities.LoginActivity
import com.example.myshop.activities.RegisterActivity
import com.example.myshop.model.User
import com.example.myshop.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        mFirestore.collection(Constants.USERS).document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user",
                    it
                )
            }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity) {
        mFirestore.collection(Constants.USERS).document(getCurrentUserID()).get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val user = document.toObject(User::class.java)
                val sharedPreferences = activity.getSharedPreferences(
                    Constants.MYSHOP_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user!!.firstName}${user!!.lastName}"
                ).apply()
                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user!!)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }
}