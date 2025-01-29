package dev.danifa19.helperbotbr.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.danifa19.helperbotbr.dto.CategoryDto
import dev.danifa19.helperbotbr.dto.ProductDto

class AppViewModel : ViewModel() {
  private val _categories = MutableLiveData(listOf<CategoryDto>())
  val categories: LiveData<List<CategoryDto>> = _categories

  fun addCategory(categoryDto: CategoryDto) {
    categories.value?.let {
      val tempList = mutableListOf<CategoryDto>()
      tempList.addAll(it)
      tempList.add(categoryDto)
      _categories.value = tempList
    } ?: run {
      _categories.value = listOf(categoryDto)
    }
  }
}