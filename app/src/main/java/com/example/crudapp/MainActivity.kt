package com.example.crudapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.crudapp.auth.Login
import com.example.crudapp.ui.Home

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        Handler().postDelayed({
            val token = sharedPreferences.getString("token", null)
            val intent = if (token != null) {
                Intent(this, Home::class.java)
            } else {
                Intent(this, Login::class.java)
            }
            startActivity(intent)
            finish()
        }, 3000)
    }
}
