package com.example.socialad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        link_to_register.setOnClickListener {
            SendUserToRegisterActivity();
        }
    }

    private fun SendUserToRegisterActivity() {
        val loginIntent = Intent(this, RegisterActivity::class.java);
        startActivity(loginIntent);
    }
}