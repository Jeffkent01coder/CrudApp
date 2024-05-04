package com.example.crudapp.reset

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.crudapp.databinding.ActivityForgotPasswordBinding
import com.example.crudapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPassword : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        progressDialog = ProgressDialog(this)

        binding.sendOtpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            sendOtp(email)
        }

        binding.btnNext.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val otp = binding.otpEditText.text.toString().trim()

            val intent = Intent(this, ChangePassword::class.java).apply {
                putExtra("email", email)
                putExtra("otp", otp)
            }
            startActivity(intent)
            finish()
        }

    }

    private fun sendOtp(email: String) {
        progressDialog?.setMessage("Sending OTP...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()

        val call = RetrofitClient.instance.forgotPassword(mapOf("email" to email))

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                progressDialog?.dismiss()
                if (response.isSuccessful) {
                    // Display OTP sent dialog
                    showSuccessDialog("OTP Sent", "An OTP has been sent to $email")
                } else {
                    // Display error message dialog
                    showErrorDialog("Error", "Failed to send OTP. Please try again.")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                progressDialog?.dismiss()
                // Display error message dialog
                showErrorDialog(
                    "Error",
                    "Failed to send OTP. Please check your internet connection and try again."
                )
            }
        })
    }

    private fun showSuccessDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
