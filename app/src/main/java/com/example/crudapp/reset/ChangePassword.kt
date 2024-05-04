package com.example.crudapp.reset

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.crudapp.auth.Login
import com.example.crudapp.network.RetrofitClient
import com.example.crudapp.databinding.ActivityChangePasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePassword : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private var email: String? = null
    private var otp: String? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        progressDialog = ProgressDialog(this)

        // Retrieve email and OTP from intent
        email = intent.getStringExtra("email")
        otp = intent.getStringExtra("otp")

        binding.btnNext.setOnClickListener {
            val newPassword = binding.passEt.text.toString().trim()
            val confirmPassword = binding.confirmPassEt.text.toString().trim()

            // Validate passwords
            if (newPassword == confirmPassword) {
                // Passwords match, proceed with reset
                resetPassword(email, otp, newPassword)
            } else {
                // Passwords do not match, show error message
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetPassword(email: String?, otp: String?, newPassword: String) {
        progressDialog?.setMessage("Resetting Password...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()

        val resetData = mapOf(
            "email" to email,
            "reset_code" to otp,
            "new_password" to newPassword
        )

        val call = RetrofitClient.instance.resetPassword(resetData)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                progressDialog?.dismiss()
                if (response.isSuccessful) {
                    // Password reset successful, show success message
                    Toast.makeText(
                        this@ChangePassword,
                        "Password reset successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Navigate to login screen
                    startActivity(Intent(this@ChangePassword, Login::class.java))
                    finish()
                } else {
                    // Password reset failed, show error message
                    Toast.makeText(
                        this@ChangePassword,
                        "Failed to reset password. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                progressDialog?.dismiss()
                // Password reset failed due to network error, show error message
                Toast.makeText(
                    this@ChangePassword,
                    "Failed to reset password. Please check your network connection and try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
