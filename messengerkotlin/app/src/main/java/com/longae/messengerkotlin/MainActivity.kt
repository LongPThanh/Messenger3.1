package com.longae.messengerkotlin

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnRegister.setOnClickListener {
            performRegister()
        }
        btnSelectProfile.setOnClickListener {
            Log.d("Main", "try to show Image selector")
        //Confix Select Images
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
        buttonHaveAccount.setOnClickListener {
            Log.d("Main", "try to Login Account")
            val intent = Intent(baseContext, LoginActivity::class.java)
            startActivity(intent)
        }
    }
    var selectedPhotoUri:Uri? = null
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            try {
                selectedPhotoUri?.let {
                    if(Build.VERSION.SDK_INT < 28) {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            selectedPhotoUri
                        )
                        imageviewCircle.setImageBitmap(bitmap)
                    } else {
                        val source = ImageDecoder.createSource(
                            this.contentResolver,
                            selectedPhotoUri!!
                        )
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        imageviewCircle.setImageBitmap(bitmap)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun performRegister() {
        val email= TextEmailRegister.text.toString()
        val password = textPasswordRegister.text.toString()
        Log.d("Main", "Email is $email")
        Log.d("Main", "Password is $password")
        if (email.isEmpty()||password.isEmpty()){
            Toast.makeText(
                baseContext, "Please enter EMAIL | PASSWORD",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        //Firebase Authentication to create a user
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful)return@addOnCompleteListener
                Log.d("Main", "Successfully created user with uid: ${it.result?.user?.uid}")
                Toast.makeText(baseContext, "Success", Toast.LENGTH_SHORT).show()
                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Log.d("Main", "Failed to create new user : ${it.message}")
                Toast.makeText(
                    baseContext,
                    "Failed to create new user : ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun uploadImageToFirebaseStorage() {
        if(selectedPhotoUri == null) return
        val storage = Firebase.storage
        val storageRef = storage.reference
        val filename =UUID.randomUUID().toString()
        val spaceRef = storageRef.child("/image/$filename")
        spaceRef.putFile(selectedPhotoUri!!).addOnCompleteListener{
            Log.d("Register"," Register Successfully Image: ${it.result?.metadata?.path}")
            spaceRef.downloadUrl.addOnCompleteListener {
                Log.d("Mainactivity","File location: ${it.result.toString()}")
            saveUserToFirebaseDatabase(it.result.toString())
            }
                .addOnFailureListener {
                    /////
                }
        }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl:String) {
        val uid = Firebase.auth.uid?:""
        val database: DatabaseReference= Firebase.database.reference
        val ref = database.child("users").child(uid)
        val user =User(uid,TextPersonName.text.toString(),profileImageUrl)
        ref.setValue(user).addOnCompleteListener {
            Log.d("Mainactivity","saved user to Database")
            val intent = Intent(this,LatestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }
}