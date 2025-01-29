package dev.danifa19.helperbotbr.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dev.danifa19.helperbotbr.MainActivity
import dev.danifa19.helperbotbr.R
import dev.danifa19.helperbotbr.adapter.CategoryAdapter
import dev.danifa19.helperbotbr.databinding.FragmentCategoryBinding
import dev.danifa19.helperbotbr.dto.CategoryDto
import dev.danifa19.helperbotbr.utils.Constant

class CategoryFragment : Fragment() {
  private val binding by lazy { FragmentCategoryBinding.inflate(layoutInflater) }
  private val appViewModel by lazy { (requireActivity() as MainActivity).appViewModel }
  private val database by lazy { FirebaseDatabase.getInstance().reference }
  private val categoryAdapter by lazy {
    CategoryAdapter { item ->
      requireActivity().intent.putExtra("subId", item.id)
      requireActivity().intent.putExtra("subTitle", item.title)
      findNavController().navigate(R.id.subCategoryFragment)
    }
  }
  private val selectedCategoryId by lazy {
    requireActivity().intent.getStringExtra("id") ?: "0"
  }
  private val selectedCategoryTitle by lazy {
    requireActivity().intent.getStringExtra("title") ?: "0"
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initialize()
  }

  private fun initialize() {
    initSubCategories()
  }

  private fun initSubCategories() {
    binding.title.text = selectedCategoryTitle
    binding.categoriesRv.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = categoryAdapter
    }
    appViewModel.categories.value?.find { it.id == selectedCategoryId }?.let { currentCategory ->
      currentCategory.subCategories.observe(viewLifecycleOwner) { list ->
        if (list.isNotEmpty()) {
          categoryAdapter.setList(list)
          binding.categoriesRv.visibility = View.VISIBLE
          binding.progressBar.visibility = View.GONE
        } else {
          binding.progressBar.visibility = View.VISIBLE
          binding.categoriesRv.visibility = View.GONE
        }
      }
      if (currentCategory.subCategories.value?.isEmpty() == true)
        database.child(Constant.SUBCATEGORIES).child(selectedCategoryId)
          .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
              if (snapshot.exists()) {
                val subCategories = mutableListOf<CategoryDto>()
                for (item in snapshot.children) {
                  val id = item.key.toString().trim()
                  val title = item.child(Constant.TITLE).value.toString().trim()
                  val preview = item.child(Constant.PREVIEW).value.toString().trim()
                  val newItem = CategoryDto(id = id, title = title, preview = preview)
                  subCategories.add(newItem)
                }
                currentCategory.subCategories.value = subCategories
              }
            }

            override fun onCancelled(error: DatabaseError) {
            }

          })
    }
  }
}