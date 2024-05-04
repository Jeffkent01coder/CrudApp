package com.example.crudapp.ui.fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crudapp.R
import com.example.crudapp.adapter.UserAdapter
import com.example.crudapp.auth.Login
import com.example.crudapp.databinding.FragmentDisplayDataBinding
import com.example.crudapp.model.GetUserResponse
import com.example.crudapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DisplayDataFragment : Fragment(), UserAdapter.OnItemClickListener {
    private lateinit var binding: FragmentDisplayDataBinding
    private lateinit var adapter: UserAdapter
    private val userArrayList = mutableListOf<GetUserResponse>()
    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDisplayDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = UserAdapter(userArrayList, this)
        binding.userRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.userRecycler.adapter = adapter

        // Show loading dialog
        showLoadingDialog()
        fetchUserData()

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        // Show loading dialog
        showLoadingDialog()

        // Remove token from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("token")
        editor.apply()

        // Navigate to login screen after a delay to allow time for the loading dialog to show
        view?.postDelayed({
            // Dismiss loading dialog
            dismissLoadingDialog()

            // Show toast message for successful logout
            Toast.makeText(requireContext(), "User logged out successfully", Toast.LENGTH_SHORT).show()

            // Navigate to login screen
            startActivity(Intent(requireContext(), Login::class.java))
            requireActivity().finish()
        }, 1000)
    }


    private fun fetchUserData() {
        val token = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("token", "")
        val call = RetrofitClient.instance.getUser(token)
        call.enqueue(object : Callback<GetUserResponse> {
            override fun onResponse(call: Call<GetUserResponse>, response: Response<GetUserResponse>) {
                // Dismiss loading dialog on response
                dismissLoadingDialog()
                Log.d("userdet", "$response")
                if (response.isSuccessful) {
                    response.body()?.let {
                        userArrayList.add(it)
                        adapter.notifyDataSetChanged()
                        updateEmptyState() // Check and update empty state after adding data
                    }
                } else {
                    showDialog("No user information found. Please update user information!", "Empty User Information")
                    updateEmptyState() // Check and update empty state on failure
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                // Dismiss loading dialog on failure
                dismissLoadingDialog()
                Toast.makeText(requireContext(), "Failed to fetch user data: ${t.message}", Toast.LENGTH_SHORT).show()
                updateEmptyState() // Check and update empty state on failure
            }
        })
    }

    private fun updateEmptyState() {
        if (userArrayList.isEmpty()) {
            // Show empty state UI
            binding.tvNoData.visibility = View.VISIBLE
            binding.userRecycler.visibility = View.GONE
        } else {
            // Hide empty state UI and show RecyclerView
            binding.tvNoData.visibility = View.GONE
            binding.userRecycler.visibility = View.VISIBLE
        }
    }

    override fun onEditClick(position: Int) {
        val selectedUser = userArrayList[position]
        val fragment = AddDataFragment().apply {
            arguments = Bundle().apply {
                putString("action", "edit")
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .addToBackStack(null)  // Optional: Adds the transaction to the back stack
            .commit()
    }

    override fun onDeleteClick(position: Int) {
        val token = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("token", "")

        val call = RetrofitClient.instance.deleteUser("$token")
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Remove the deleted user from the list
                    userArrayList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    showDialog("Deleted Successfully!", "Deletion Status")
                    updateEmptyState() // Check and update empty state after deleting user
                } else {
                    Toast.makeText(requireContext(), "Failed to delete user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to delete user: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDialog(message: String, title: String) {
        val dialogBuilder = android.app.AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage(message)
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
        val alert = dialogBuilder.create()
        alert.setTitle(title)
        alert.show()
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
