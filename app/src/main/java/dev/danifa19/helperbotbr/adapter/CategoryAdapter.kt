package dev.danifa19.helperbotbr.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.danifa19.helperbotbr.databinding.ItemCategoryBinding
import dev.danifa19.helperbotbr.dto.CategoryDto

class CategoryAdapter(val onClick: (CategoryDto) -> Unit) :
  RecyclerView.Adapter<CategoryAdapter.ItemHolder>() {
  private val adapterList = mutableListOf<CategoryDto>()

  class ItemHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
    return ItemHolder(
      ItemCategoryBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      )
    )
  }

  override fun getItemCount(): Int {
    return adapterList.size
  }

  override fun onBindViewHolder(holder: ItemHolder, position: Int) {
    val item = adapterList[position]
    with(holder.binding) {
      Glide.with(holder.itemView.context)
        .load(
          "https://firebasestorage.googleapis.com/v0/b/helper-bot-br.appspot.com/o/categories%2F" +
                  "${item.preview}" +
                  "?alt=media"
        ).into(itemPreview)
      itemTitle.text = item.title
      root.setOnClickListener {
        onClick(item)
      }
    }
  }

  @SuppressLint("NotifyDataSetChanged")
  fun setList(list: List<CategoryDto>) {
    adapterList.clear()
    adapterList.addAll(list)
    notifyDataSetChanged()
  }
}