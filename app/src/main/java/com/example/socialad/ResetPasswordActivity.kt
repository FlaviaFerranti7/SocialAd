package com.example.socialad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_find_users.*
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        Utils.setupUI(reset_password_layout, this)

        val mToolbar: Toolbar = forget_password_toolbar as Toolbar
        setSupportActionBar(mToolbar);
        supportActionBar?.title = "Recover Password";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance()

        send_link_btn.setOnClickListener {
            val user_email = insert_email.text.toString();
            if(TextUtils.isEmpty(user_email)){
                Toast.makeText(this, "Please write your email first", Toast.LENGTH_SHORT).show()
            }
            else{
                mAuth.sendPasswordResetEmail(user_email).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this, "Check your email now", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                    else{
                        val message = it.exception?.message?.trim()
                        Toast.makeText(this, "Error occured: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}