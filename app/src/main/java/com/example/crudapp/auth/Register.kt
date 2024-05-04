package com.example.crudapp.auth

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.crudapp.R
import com.example.crudapp.auth.Login
import com.example.crudapp.databinding.ActivityRegisterBinding
import com.example.crudapp.model.RegisterModel
import com.example.crudapp.network.ApiService
import com.example.crudapp.network.RetrofitClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var apiService: ApiService
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        progressDialog = ProgressDialog(this)

        // Initialize ApiService instance
        apiService = RetrofitClient.instance

        binding.btnRegister.setOnClickListener {
            val username = binding.usernameEt.text.toString().trim()
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passEt.text.toString().trim()
            val confirmPassword = binding.confirmPassEt.text.toString().trim()

            if (validateInputs(username, email, password, confirmPassword)) {
                val registerModel = RegisterModel(username, email, password)
                registerUser(registerModel)
            }
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    private fun validateInputs(username: String, email: String, password: String, confirmPassword: String): Boolean {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun registerUser(registerModel: RegisterModel) {
        progressDialog?.setMessage("Registering User...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()

        apiService.registerUser(registerModel).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                progressDialog?.dismiss()
                if (response.isSuccessful) {
                    // Show toast for successful registration
                    Toast.makeText(this@Register, "Registration Successful", Toast.LENGTH_SHORT).show()
                    // Navigate to login screen
                    startActivity(Intent(this@Register, Login::class.java))
                    finish()
                } else {
                    val errorMessage = parseErrorMessage(response)
                    showDialogOnUIThread("Registration Failed", errorMessage)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                progressDialog?.dismiss()
                showDialogOnUIThread("Registration Error", "Failed to connect to server: $t")
                Log.d("erroret", "$t")
            }
        })
    }


    private fun parseErrorMessage(response: Response<Void>): String {
        val errorBody = response.errorBody()?.string()
        return if (!errorBody.isNullOrEmpty()) {
            try {
                JSONObject(errorBody).getString("message")
            } catch (e: JSONException) {
                "Failed to register user"
            }
        } else {
            "Failed to register user"
        }
    }

    private fun showDialogOnUIThread(title: String, message: String) {
        runOnUiThread {
            showDialog(title, message)
        }
    }


    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
