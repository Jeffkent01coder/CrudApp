package com.example.crudapp.auth

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.crudapp.databinding.ActivityLoginBinding
import com.example.crudapp.network.RetrofitClient
import com.example.crudapp.reset.ForgotPassword
import com.example.crudapp.ui.Home
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        binding.btnLogin.setOnClickListener {
            val username = binding.usernameEt.text.toString().trim()
            val password = binding.passEt.text.toString().trim()
            performLogin(username, password)
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
            finish()
        }

        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
        }
    }

    private fun performLogin(username: String, password: String) {
        showLoadingDialog()
        val loginModel = mapOf("username" to username, "password" to password)
        val call = RetrofitClient.instance.loginUser(loginModel)

        call.enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                dismissLoadingDialog()
                if (response.isSuccessful) {
                    // Login successful, extract token from response body
                    val token = response.body()?.get("token")
                    if (token != null) {
                        // Save token to SharedPreferences
                        saveTokenToSharedPreferences(token)
                        startActivity(Intent(this@Login, Home::class.java))
                        finish()
                    } else {
                        showErrorDialog("Login Failed", "No token received")
                    }
                } else {
                    // Login failed, show error dialog
                    showErrorDialog("Login Failed", response.message())
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                dismissLoadingDialog()
                // Request failed, show error dialog
                showErrorDialog("Login Failed", t.message ?: "Unknown error")
            }
        })
    }

    private fun saveTokenToSharedPreferences(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.apply()
    }

    private fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showLoadingDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Logging in...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    private fun dismissLoadingDialog() {
        progressDialog?.dismiss()
    }
}
