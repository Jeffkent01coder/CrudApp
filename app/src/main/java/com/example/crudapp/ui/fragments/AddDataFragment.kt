package com.example.crudapp.ui.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.crudapp.R
import com.example.crudapp.databinding.FragmentAddDataBinding
import com.example.crudapp.model.GetUserResponse
import com.example.crudapp.model.UserInformation
import com.example.crudapp.network.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddDataFragment : Fragment() {

    private lateinit var binding: FragmentAddDataBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var editMode = false
    private var userId: String? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Check if edit mode is enabled and get user data if necessary
        if (arguments?.containsKey("action") == true && arguments?.getString("action") == "edit") {
            editMode = true
            userId = arguments?.getString("userId")
            fetchUserData()
        }

        binding.genderEt.setOnClickListener {
            showGenderOptions(it)
        }

        binding.btnSubmit.setOnClickListener {
            submitProfileDetails()
        }
    }

    private fun fetchUserData() {
        showLoadingDialog()
        val token = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("token", "")
        val call = RetrofitClient.instance.getUser(token)
        call.enqueue(object : Callback<GetUserResponse> {
            override fun onResponse(call: Call<GetUserResponse>, response: Response<GetUserResponse>) {
                dismissLoadingDialog()
                if (response.isSuccessful) {
                    Log.d("userdet", "${response.body()}")
                    response.body()?.let {
                        preFillUserData(it)
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                dismissLoadingDialog()
                Toast.makeText(requireContext(), "Failed to fetch user data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun preFillUserData(user: GetUserResponse) {
        binding.firstNameEt.setText(user.first_name)
        binding.lastNameEt.setText(user.last_name)
        binding.ageEt.setText(user.age)
        binding.TownEt.setText(user.town)
        binding.genderEt.setText(user.gender)
    }

    private fun submitProfileDetails() {
        showLoadingDialog()
        val token = sharedPreferences.getString("token", "")
        val firstName = binding.firstNameEt.text.toString().trim()
        val lastName = binding.lastNameEt.text.toString().trim()
        val age = binding.ageEt.text.toString().trim()
        val town = binding.TownEt.text.toString().trim()
        val gender = binding.genderEt.text.toString().trim()

        val userInformation = UserInformation(firstName, lastName, age, town, gender)

        val call: Call<Void> = if (editMode) {
            // Update profile if in edit mode
            RetrofitClient.instance.updateProfile("$token", userInformation)
        } else {
            // Create new profile if not in edit mode
            RetrofitClient.instance.updateProfile("$token", userInformation)
        }

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                dismissLoadingDialog()
                if (response.isSuccessful) {
                    // Profile updated or created successfully
                    showDialog("Success", "Profile ${if (editMode) "updated" else "created"} successfully")
                    clearFields()
                } else {
                    // Profile update or creation failed
                    showErrorDialog("Error", "Failed to ${if (editMode) "update" else "create"} profile. Please try again.")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                dismissLoadingDialog()
                // Request failed
                showErrorDialog("Error", "Failed to ${if (editMode) "update" else "create"} profile. Please check your internet connection and try again.")
            }
        })
    }

    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun clearFields() {
        binding.firstNameEt.text = null
        binding.lastNameEt.text = null
        binding.ageEt.text = null
        binding.TownEt.text = null
        binding.genderEt.text = null
    }

    private fun showGenderOptions(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.gender_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_male -> {
                    (view as TextInputEditText).setText("Male")
                    true
                }
                R.id.menu_female -> {
                    (view as TextInputEditText).setText("Female")
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showLoadingDialog() {
        progressDialog = ProgressDialog(requireContext())
        progressDialog?.setMessage("Loading...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    private fun dismissLoadingDialog() {
        progressDialog?.dismiss()
    }
}
