package com.example.socialad

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient;
    private lateinit var mAuth : FirebaseAuth;
    private lateinit var loadingBar : ProgressDialog;
    private var RC_SIGN_IN = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Utils.setupUI(login_layout,this);

        mAuth = FirebaseAuth.getInstance();

        loadingBar = ProgressDialog(this);

        link_to_register.setOnClickListener {
            SendUserToRegisterActivity();
        }
        login_button.setOnClickListener {
            AllowingUserToLogin();
        }

        forget_password_link.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        google_logo.setOnClickListener {
            signIn();
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent;
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("Login activity", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
                Toast.makeText(this, "Please wait, while we are getting your auth result", Toast.LENGTH_SHORT).show();
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Login activity", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("Login activity", "signInWithCredential:success")
                        //val user = mAuth.currentUser
                        SendUserToMainActivity();

                    } else {
                        Log.w("Login activity", "signInWithCredential:failure", task.exception)
                        SendUserToLoginActivity();
                        Toast.makeText(this, "Not authenticated: "+ task.exception, Toast.LENGTH_SHORT).show();
                    }
                }
    }


    override fun onStart() {
        super.onStart()
        var currentUser : FirebaseUser? = mAuth.currentUser;
        if (currentUser != null){
            SendUserToMainActivity();
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
                            Toast.makeText(this, "Error occured: $message", Toast.LENGTH_SHORT).show();
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

    private fun SendUserToLoginActivity() {
        val loginIntent = Intent(this, LoginActivity::class.java);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private fun SendUserToRegisterActivity() {
        val registerIntent = Intent(this, RegisterActivity::class.java);
        startActivity(registerIntent);
    }
}