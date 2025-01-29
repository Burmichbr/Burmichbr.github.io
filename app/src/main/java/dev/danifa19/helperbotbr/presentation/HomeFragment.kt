package dev.danifa19.helperbotbr.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import dev.danifa19.helperbotbr.databinding.FragmentHomeBinding
import dev.danifa19.helperbotbr.dto.CategoryDto
import dev.danifa19.helperbotbr.utils.Constant

class HomeFragment : Fragment() {
  private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
  private val appViewModel by lazy { (requireActivity() as MainActivity).appViewModel }
  private val database by lazy { FirebaseDatabase.getInstance().reference }
  private val categoryAdapter by lazy {
    CategoryAdapter { item ->
      requireActivity().intent.putExtra("id", item.id)
      requireActivity().intent.putExtra("title", item.title)
      findNavController().navigate(R.id.categoryFragment)
    }
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
    initCategories()
    initListeners()
  }

  private fun initListeners() {
    binding.btnTelegram.setOnClickListener {
      openTelegram()
    }
  }

  private fun openTelegram() {
    try {
      startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constant.TELEGRAM_LINK)))
    } catch (_: Exception) {
      Toast.makeText(requireContext(), "Произошла ошибка", Toast.LENGTH_SHORT).show()
    }
  }

  private fun initCategories() {
    binding.categoriesRv.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = categoryAdapter
    }
    appViewModel.categories.observe(viewLifecycleOwner) { list ->
      if (list.isEmpty()) {
        binding.categoriesRv.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
      } else {
        binding.categoriesRv.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
      }
      categoryAdapter.setList(list)
    }
    if (appViewModel.categories.value?.isEmpty() == true) {
      database.child(Constant.CATEGORIES)
        .addListenerForSingleValueEvent(object : ValueEventListener {
          override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
              for (item in snapshot.children) {
                val id = item.key.toString().trim()
                val title = item.child(Constant.TITLE).value.toString().trim()
                val preview = item.child(Constant.PREVIEW).value.toString().trim()
                val newItem = CategoryDto(id = id, title = title, preview = preview)
                appViewModel.addCategory(newItem)
              }
            }
          }

          override fun onCancelled(error: DatabaseError) {
          }

        })
    }
  }

}