package dev.danifa19.helperbotbr.adapter

import android.annotation.SuppressLint
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.danifa19.helperbotbr.databinding.ItemCategoryBinding
import dev.danifa19.helperbotbr.databinding.ItemProductBinding
import dev.danifa19.helperbotbr.dto.ProductDto
import dev.danifa19.helperbotbr.holder.CategoryHolder
import dev.danifa19.helperbotbr.holder.ProductHolder

class ProductAdapter(
  val showMap: (ProductDto) -> Unit, val showStatistics: (ProductDto) -> Unit,
  val showCategory: (ProductDto) -> Unit, val showImages: (ProductDto) -> Unit
) :
  RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  private val adapterList = mutableListOf<ProductDto>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    if (viewType == 0) {
      return CategoryHolder(
        ItemCategoryBinding.inflate(
          LayoutInflater.from(parent.context),
          parent,
          false
        )
      )
    } else {
      return ProductHolder(
        ItemProductBinding.inflate(
          LayoutInflater.from(parent.context),
          parent,
          false
        )
      )
    }
  }

  override fun getItemViewType(position: Int): Int {
    return if (adapterList[position].subcategory) 0 else 1
  }

  override fun getItemCount(): Int {
    return adapterList.size
  }

  override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
    val item = adapterList[position]
    if (item.subcategory) {
      val holder = viewHolder as CategoryHolder
      with(holder.binding) {
        Glide.with(holder.itemView.context)
          .load(
            "https://firebasestorage.googleapis.com/v0/b/helper-bot-br.appspot.com/o/categories%2F" +
              "${item.preview}" +
              "?alt=media"
          ).into(itemPreview)
        itemTitle.text = item.title
        root.setOnClickListener {
          showCategory(item)
        }
      }
    } else {
      val holder = viewHolder as ProductHolder
      with(holder.binding) {
        Glide.with(holder.itemView.context)
          .load(
            "https://firebasestorage.googleapis.com/v0/b/helper-bot-br.appspot.com/o/categories%2F" +
              "${item.preview}" +
              "?alt=media"
          ).into(itemPreview)
        itemDesc.text = Html.fromHtml(item.desc, 0)
        if (item.map != null) {
          itemMap.visibility = View.VISIBLE
        } else {
          itemMap.visibility = View.GONE
        }
        if (item.statistics != null) {
          itemStatistics.visibility = View.VISIBLE
        } else {
          itemStatistics.visibility = View.GONE
        }
        if (item.images != null) {
          itemImages.visibility = View.VISIBLE
        } else {
          itemImages.visibility = View.GONE
        }
        if (item.images != null || item.statistics != null || item.map != null) {
          itemSpace.visibility = View.VISIBLE
        } else {
          itemSpace.visibility = View.GONE
        }
        itemMap.setOnClickListener {
          showMap(item)
        }
        itemStatistics.setOnClickListener {
          showStatistics(item)
        }
        itemImages.setOnClickListener {
          showImages(item)
        }
      }
    }
  }

  @SuppressLint("NotifyDataSetChanged")
  fun setList(list: List<ProductDto>) {
    adapterList.clear()
    adapterList.addAll(list)
    notifyDataSetChanged()
  }
}