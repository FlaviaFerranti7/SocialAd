package com.example.socialad

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class ChooseAvatarActivity : AppCompatActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val view = MyView(this)
        view.id = R.id.chooseAvatarView;

        val extras = intent.extras
        if (extras != null) {
            val value = extras.getString("Activity_Name").toString()
            view.setactivity(value);
        }

        setContentView(view);

    }

}