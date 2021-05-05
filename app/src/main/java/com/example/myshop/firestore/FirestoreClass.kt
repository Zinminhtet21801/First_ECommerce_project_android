package com.example.myshop.firestore

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.myshop.model.Product
import com.example.myshop.model.User
import com.example.myshop.ui.activities.*
import com.example.myshop.ui.fragments.ProductsFragment
import com.example.myshop.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

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
        Log.d("ERROR", getCurrentUserID())
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
                    is SettingsActivity -> {
                        activity.userDetailsSuccess(user!!)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
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

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFirestore.collection(Constants.USERS).document(getCurrentUserID()).update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }

                }
            }
            .addOnFailureListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }

                }
                Log.e(activity.javaClass.simpleName, "Error while updating user details", it)
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileUri: Uri?, imageType: String) {
        val sRef = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(
                activity,
                imageFileUri
            )
        )
        sRef.putFile(imageFileUri!!).addOnSuccessListener {
            Log.e(
                "Firebase Image URL",
                it.metadata!!.reference!!.downloadUrl.toString()
            )

            it.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.e(
                    "Downloadable Image URL",
                    uri.toString()
                )
                when (activity) {
                    is UserProfileActivity -> {
                        activity.imageUploadSuccess(uri.toString())
                    }
                    is AddProductActivity -> {
                        activity.imageUploadSuccess(uri.toString())
                    }
                }
            }
        }
            .addOnFailureListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    it.message,
                    it
                )
            }
    }

    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {
        mFirestore.collection(Constants.PRODUCTS)
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading product details",
                    it
                )
            }
    }

    fun getProductsList(fragment: Fragment) {
        mFirestore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID()).get()
            .addOnSuccessListener { document ->
                Log.e("Products List",document.documents.toString())
                val productsList : ArrayList<Product> = ArrayList()
                for (i in document.documents){
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }
                when(fragment){
                    is ProductsFragment ->{
                        fragment.successProductsListFromFireStore(productsList)
                    }
                }
            }
    }
}