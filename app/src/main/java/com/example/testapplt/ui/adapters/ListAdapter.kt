package com.example.testapplt.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testapplt.R
import com.example.testapplt.data.models.ListItem
import com.example.testapplt.databinding.ListItemBinding
import java.text.SimpleDateFormat
import java.util.*

class ListAdapter : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
    private var listener: AdaptersListener? = null
    fun setOnClickListener(onClickListener: AdaptersListener) {
        this.listener = onClickListener
    }
    private val differ = AsyncListDiffer(this, ItemComparator())

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val itemBinding =
            ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(
            itemBinding
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentItem = differ.currentList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun updateItems(newList: List<ListItem>) {
        differ.submitList(newList)
    }

    class ItemComparator:  DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }
    }

    inner class ListViewHolder(
        private val binding: ListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val image = binding.itemImage

        fun bind(value: ListItem) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.FRANCE)
            val date = dateFormat.parse(value.date)
            val formatter =  SimpleDateFormat("yyyy.MM.dd, HH:ss", Locale.FRANCE)
            val dateString = formatter.format(date as Date)

            binding.titleTextView.text = value.title
            binding.descriptionTextView.text = value.text
            binding.dateTextView.text = dateString

            Glide.with(image)
                .load("http://dev-exam.l-tech.ru/" + value.image)
                .error(R.drawable.no_image)
                .into(image)

            initListener(value)
        }

        private fun initListener(item: ListItem){
            binding.root.setOnClickListener {
                listener?.onClickItem(item)
            }
        }
    }
}

