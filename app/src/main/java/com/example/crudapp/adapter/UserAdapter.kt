package com.example.crudapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.crudapp.databinding.UserDataBinding
import com.example.crudapp.model.UserInformation

class UserAdapter(private val dataList: List<UserInformation>, private val clickListener: OnItemClickListener) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
    }

    inner class ViewHolder(private val binding: UserDataBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.editIcon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onEditClick(position)
                }
            }
            binding.deleteIcon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onDeleteClick(position)
                }
            }
        }

        fun bind(item: UserInformation) {
            binding.firstName.text = item.firstName
            binding.lastName.text = item.lastName
            binding.age.text = item.age
            binding.town.text = item.town
            binding.gender.text = item.gender
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UserDataBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
