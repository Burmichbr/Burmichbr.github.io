package dev.danifa19.helperbotbr.dto

import androidx.lifecycle.MutableLiveData

data class CategoryDto(
  val id: String,
  val title: String,
  val preview: String,
  val subCategories: MutableLiveData<List<CategoryDto>> = MutableLiveData(listOf()),
  val productsList: MutableLiveData<List<ProductDto>> = MutableLiveData(null),
)
