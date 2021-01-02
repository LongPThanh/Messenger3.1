package com.longae.messengerkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        button_backtoregister.setOnClickListener {
            finish()
        }
        button_login.setOnClickListener {
            val email = text_email_login.text.toString()
            val password = text_password_login.text.toString()
            Firebase.auth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener {

                }
        }
    }
}