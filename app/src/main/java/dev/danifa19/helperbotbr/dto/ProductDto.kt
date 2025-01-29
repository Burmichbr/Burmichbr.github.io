package dev.danifa19.helperbotbr.dto

data class ProductDto(
  val id: String,
  val desc: String? = null,
  val level: String? = null,
  val title: String? = null,
  var preview: String,
  val subcategory: Boolean,
  val map: SliderDto? = null,
  val statistics: SliderDto? = null,
  val images: List<String> ?= null
)
