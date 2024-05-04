package com.example.crudapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crudapp.adapter.UserAdapter
import com.example.crudapp.databinding.FragmentDisplayDataBinding
import com.example.crudapp.model.UserInformation


class DisplayDataFragment : Fragment(), UserAdapter.OnItemClickListener {
    private lateinit var binding: FragmentDisplayDataBinding
    private lateinit var adapter: UserAdapter
    private val userArrayList = mutableListOf<UserInformation>()

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

        populateSampleData()
    }

    private fun populateSampleData() {
        userArrayList.add(UserInformation("1", "John", "Doe", "30", "New York", "Male"))
        userArrayList.add(UserInformation("2", "Jane", "Smith", "25", "Los Angeles", "Female"))
        userArrayList.add(UserInformation("3", "Michael", "Johnson", "35", "Chicago", "Male"))
        userArrayList.add(UserInformation("1", "John", "Doe", "30", "New York", "Male"))
        userArrayList.add(UserInformation("2", "Jane", "Smith", "25", "Los Angeles", "Female"))
        userArrayList.add(UserInformation("3", "Michael", "Johnson", "35", "Chicago", "Male"))
        userArrayList.add(UserInformation("1", "John", "Doe", "30", "New York", "Male"))
        userArrayList.add(UserInformation("2", "Jane", "Smith", "25", "Los Angeles", "Female"))
        userArrayList.add(UserInformation("3", "Michael", "Johnson", "35", "Chicago", "Male"))
        userArrayList.add(UserInformation("1", "John", "Doe", "30", "New York", "Male"))
        userArrayList.add(UserInformation("2", "Jane", "Smith", "25", "Los Angeles", "Female"))
        userArrayList.add(UserInformation("3", "Michael", "Johnson", "35", "Chicago", "Male"))


        adapter.notifyDataSetChanged()
    }

    override fun onEditClick(position: Int) {
        Toast.makeText(requireContext(), "Edit clicked for position $position", Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteClick(position: Int) {
        Toast.makeText(requireContext(), "Delete clicked for position $position", Toast.LENGTH_SHORT).show()
    }
}
