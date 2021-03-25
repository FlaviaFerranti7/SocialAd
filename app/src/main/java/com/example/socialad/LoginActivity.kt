package com.example.socialad

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth;
    private lateinit var loadingBar : ProgressDialog;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance();

        loadingBar = ProgressDialog(this);

        link_to_register.setOnClickListener {
            SendUserToRegisterActivity();
        }
        login_button.setOnClickListener {
            AllowingUserToLogin();
        }
    }

    private fun AllowingUserToLogin() {
        var email : String = login_email_field.text.toString();
        var password : String = login_psw_field.text.toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please write your email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please write your password", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Log in");
            loadingBar.setMessage("Please wait while we are allowing you Log in into your account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            loadingBar.dismiss();
                            Toast.makeText(this, "You are Logged in successfully", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();
                        }
                        else{
                            loadingBar.dismiss();
                            var message = it.exception?.message;
                            Toast.makeText(this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
        }
    }

    private fun SendUserToMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private fun SendUserToRegisterActivity() {
        val registerIntent = Intent(this, RegisterActivity::class.java);
        startActivity(registerIntent);
    }
}