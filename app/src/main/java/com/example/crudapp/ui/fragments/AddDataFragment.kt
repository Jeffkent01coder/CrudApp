package com.example.crudapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.example.crudapp.R
import com.example.crudapp.databinding.FragmentAddDataBinding
import com.google.android.material.textfield.TextInputEditText

class AddDataFragment : Fragment() {

    private lateinit var binding: FragmentAddDataBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAddDataBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.genderEt.setOnClickListener {
            showGenderOptions(it)
        }
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
}
