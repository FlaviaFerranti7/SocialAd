package com.example.socialad

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth;
    private lateinit var loadingBar : ProgressDialog;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Utils.setupUI(register_layout,this);

        mAuth = FirebaseAuth.getInstance();

        loadingBar = ProgressDialog(this);

        register_button.setOnClickListener {
            CreateNewAccount();
        }
    }

    override fun onStart() {
        super.onStart()
        var currentUser : FirebaseUser? = mAuth.currentUser;
        if (currentUser != null){
            SendUserToMainActivity();
        }
    }

    private fun CreateNewAccount() {
        var email : String = register_email_field.text.toString();
        var password : String = register_psw_field.text.toString();
        var confirmPassword : String = register_confirm_psw_field.text.toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please write your email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please write your password", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
        }
        else if(password != confirmPassword){
            Toast.makeText(this, "Password and confirm password should match", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Creating new Account");
            loadingBar.setMessage("Please wait while we create your account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            loadingBar.dismiss();
                            Toast.makeText(this@RegisterActivity, "You are successfully registered", Toast.LENGTH_SHORT).show();
                            SendUserToSetupActivity();

                        }
                        else{
                            loadingBar.dismiss();
                            var message = it.exception?.message;
                            Toast.makeText(this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
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

    private fun SendUserToSetupActivity() {
        val setupIntent = Intent(this, SetupActivity::class.java);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}