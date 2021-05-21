package com.example.socialad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ChooseAvatarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(MyView(this));
    }
}