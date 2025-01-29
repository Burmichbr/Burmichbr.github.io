package dev.danifa19.helperbotbr.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.danifa19.helperbotbr.databinding.ItemImageBinding
import dev.danifa19.helperbotbr.databinding.ItemProductBinding

class ImagesAdapter(val adapterList: List<String>) :
  RecyclerView.Adapter<ImagesAdapter.ItemHolder>() {

  class ItemHolder(val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
    return ItemHolder(
      ItemImageBinding.inflate(
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
                  "${item}" +
                  "?alt=media"
        ).into(itemImage)
    }
  }
}