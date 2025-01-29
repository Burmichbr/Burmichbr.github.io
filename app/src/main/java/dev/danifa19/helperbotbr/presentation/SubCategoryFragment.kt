package dev.danifa19.helperbotbr.presentation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dev.danifa19.helperbotbr.MainActivity
import dev.danifa19.helperbotbr.R
import dev.danifa19.helperbotbr.adapter.ImagesAdapter
import dev.danifa19.helperbotbr.adapter.ProductAdapter
import dev.danifa19.helperbotbr.databinding.FragmentSubCategoryBinding
import dev.danifa19.helperbotbr.dto.ProductDto
import dev.danifa19.helperbotbr.dto.SliderDto
import dev.danifa19.helperbotbr.utils.Constant

class SubCategoryFragment : Fragment() {
  private val binding by lazy { FragmentSubCategoryBinding.inflate(layoutInflater) }
  private val appViewModel by lazy { (requireActivity() as MainActivity).appViewModel }
  private val database by lazy { FirebaseDatabase.getInstance().reference }
  private val productAdapter by lazy {
    ProductAdapter(showMap = { item ->
      item.map?.let { showDialogSlider(it.images, descText = it.desc) }
    },
      showStatistics = { item ->
        item.statistics?.let { showDialogSlider(it.images, descText = it.desc) }
      },
      showCategory = { item ->
        requireActivity().intent.putExtra("isSubSub", true)
        requireActivity().intent.putExtra("subSubId", item.id)
        requireActivity().intent.putExtra("subSubTitle", item.title)
        findNavController().navigate(R.id.subCategoryFragment)
      },
      showImages = { item ->
        item.images?.let { showDialogSlider(it, true) }
      })
  }

  private val selectedCategoryId by lazy {
    requireActivity().intent.getStringExtra("id") ?: "0"
  }
  private val selectedCategoryTitle by lazy {
    requireActivity().intent.getStringExtra("title") ?: "0"
  }
  private val selectedSubCategoryId by lazy {
    requireActivity().intent.getStringExtra("subId") ?: "0"
  }
  private val selectedSubCategoryTitle by lazy {
    requireActivity().intent.getStringExtra("subTitle") ?: "0"
  }
  private val isSubSub by lazy {
    requireActivity().intent.getBooleanExtra("isSubSub", false)
  }
  private val selectedSubSubCategoryId by lazy {
    requireActivity().intent.getStringExtra("subSubId") ?: "0"
  }
  private val selectedSubSubCategoryTitle by lazy {
    requireActivity().intent.getStringExtra("subSubTitle") ?: "0"
  }
  private val transportClasses =
    listOf(
      "нет",
      "низкий",
      "средний",
      "высокий",
      "грузовой",
      "водный",
      "мотоциклы",
      "уникальный",
      "админ",
      "black pass"
    )
  private val skinClasses =
    listOf("нет", "магазин", "донат", "БП", "рабочий класс", "остальное")
  private val currentClasses by lazy {
    if (selectedCategoryTitle == "Транспорт") transportClasses else skinClasses
  }
  private var selectedLevel = "нет"
  private var subSubProducts = mutableListOf<ProductDto>()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initialize()
  }

  private fun initialize() {
    initProducts()
    initSpinner()
    initListeners()
  }

  private fun initSpinner() {
    val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, currentClasses)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    binding.inputLevel.adapter = adapter
  }

  private fun initListeners() {
    binding.inputLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(
        parent: AdapterView<*>?, itemSelected: View?, selectedItemPosition: Int, selectedId: Long
      ) {
        selectedLevel = currentClasses[selectedItemPosition]
        filterProducts()
      }

      override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
    binding.inputSearch.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      }

      override fun afterTextChanged(s: Editable?) {
        filterProducts()
      }
    })
  }

  private fun initProducts() {
    if ("Транспорт" != selectedCategoryTitle && "Скины • Аксессуары" != selectedCategoryTitle) {
      binding.inputLevel.visibility = View.GONE
      binding.inputLevelTitle.visibility = View.GONE
    } else {
      binding.inputLevel.visibility = View.VISIBLE
      binding.inputLevelTitle.visibility = View.VISIBLE
    }
    if (isSubSub) {
      binding.title.text = selectedSubSubCategoryTitle
    } else {
      binding.title.text = selectedSubCategoryTitle
    }
    binding.productsRv.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = productAdapter
    }
    if (isSubSub) {
      setSubSubProducts()
    } else {
      setSubProducts()
    }
  }

  private fun setSubProducts() {
    appViewModel.categories.value?.find { it.id == selectedCategoryId }?.let { currentCategory ->
      currentCategory.subCategories.value?.find { it.id == selectedSubCategoryId }
        ?.let { currentSubCategory ->
          currentSubCategory.productsList.observe(viewLifecycleOwner) { list ->
            if (list != null) if (list.isNotEmpty()) {
              filterProducts()
              binding.productsRv.visibility = View.VISIBLE
              binding.progressBar.visibility = View.GONE
            } else {
              binding.progressBar.visibility = View.VISIBLE
              binding.productsRv.visibility = View.GONE
            }
          }
          database.child(Constant.PRODUCTS).child(selectedSubCategoryId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
              override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                  val products = mutableListOf<ProductDto>()
                  for (item in snapshot.children) {
                    val id = item.key.toString().trim()
                    val subcategory =
                      item.child(Constant.SUBCATEGORY).value?.toString()?.toBoolean() ?: false
                    Log.d("log1", subcategory.toString())
                    val title = item.child(Constant.TITLE).value?.toString()?.trim()
                    val desc = item.child(Constant.DESC).value?.toString()?.trim()
                    val preview = item.child(Constant.PREVIEW).value.toString().trim()
                    val level = item.child(Constant.LEVEL).value?.toString()?.trim()
                    val mapDesc =
                      item.child(Constant.MAP).child(Constant.DESC).value?.toString()
                    val mapImages =
                      item.child(Constant.MAP).child(Constant.IMAGES).value?.toString()
                    val statisticsDesc =
                      item.child(Constant.STATISTICS).child(Constant.DESC).value?.toString()
                    val statisticsImages =
                      item.child(Constant.STATISTICS).child(Constant.IMAGES).value?.toString()
                    val map = if (mapDesc == null && mapImages == null) null else SliderDto(
                      images = mapImages.toString().split("|||"), desc = mapDesc.toString()
                    )
                    val statistics =
                      if (mapDesc == null && mapImages == null) null else SliderDto(
                        images = statisticsImages.toString().split("|||"),
                        desc = statisticsDesc.toString()
                      )
                    val images = item.child(Constant.IMAGES).value?.toString()?.split("|||")
                    val newItem = ProductDto(
                      id = id,
                      desc = desc,
                      preview = preview,
                      level = level,
                      map = map,
                      title = title,
                      statistics = statistics,
                      subcategory = subcategory,
                      images = images
                    )
                    products.add(newItem)
                  }
                  currentSubCategory.productsList.value = products
                }
              }

              override fun onCancelled(error: DatabaseError) {
              }

            })
        }
    }
  }

  private fun setSubSubProducts() {
    database.child(Constant.PRODUCTS).child(selectedSubSubCategoryId)
      .addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
          if (snapshot.exists()) {
            subSubProducts.clear()
            for (item in snapshot.children) {
              val id = item.key.toString().trim()
              val subcategory =
                item.child(Constant.SUBCATEGORY).value?.toString()?.toBoolean() ?: false
              val title = item.child(Constant.TITLE).value?.toString()?.trim()
              val desc = item.child(Constant.DESC).value?.toString()?.trim()
              val preview = item.child(Constant.PREVIEW).value.toString().trim()
              val level = item.child(Constant.LEVEL).value?.toString()?.trim()
              val mapDesc =
                item.child(Constant.MAP).child(Constant.DESC).value?.toString()
              val mapImages =
                item.child(Constant.MAP).child(Constant.IMAGES).value?.toString()
              val statisticsDesc =
                item.child(Constant.STATISTICS).child(Constant.DESC).value?.toString()
              val statisticsImages =
                item.child(Constant.STATISTICS).child(Constant.IMAGES).value?.toString()
              val map = if (mapDesc == null && mapImages == null) null else SliderDto(
                images = mapImages.toString().split("|||"), desc = mapDesc.toString()
              )
              val statistics =
                if (mapDesc == null && mapImages == null) null else SliderDto(
                  images = statisticsImages.toString().split("|||"),
                  desc = statisticsDesc.toString()
                )
              val images = item.child(Constant.IMAGES).value?.toString()?.split("|||")
              val newItem = ProductDto(
                id = id,
                desc = desc,
                preview = preview,
                level = level,
                map = map,
                title = title,
                statistics = statistics,
                subcategory = subcategory,
                images = images
              )
              subSubProducts.add(newItem)
            }
            binding.progressBar.visibility = View.GONE
            binding.productsRv.visibility = View.VISIBLE
            filterProducts()
          }
        }

        override fun onCancelled(error: DatabaseError) {
        }

      })
  }

  private fun filterProducts() {
    val search = binding.inputSearch.text.toString().trim()
    if (isSubSub) {
      val newProducts = mutableListOf<ProductDto>()
      if (selectedLevel == "нет") {
        subSubProducts.forEach { item ->
          if (item.subcategory) {
            newProducts.add(item)
          } else {
            if (item.desc?.lowercase()?.contains(search.lowercase()) == true) {
              newProducts.add(item)
            }
          }
        }
      } else {
        subSubProducts.forEach { item ->
          if (item.subcategory) {
            newProducts.add(item)
          } else {
            if (item.level == selectedLevel && item.desc?.lowercase()
                ?.contains(search.lowercase()) == true
            ) {
              newProducts.add(item)
            }
          }
        }
      }
      productAdapter.setList(newProducts)
    } else {
      appViewModel.categories.value?.find { it.id == selectedCategoryId }?.let { currentCategory ->
        currentCategory.subCategories.value?.find { it.id == selectedSubCategoryId }
          ?.let { currentSubCategory ->
            val products = currentSubCategory.productsList.value ?: listOf()
            val newProducts = mutableListOf<ProductDto>()
            if (selectedLevel == "нет") {
              products.forEach { item ->
                if (item.subcategory) {
                  newProducts.add(item)
                } else {
                  if (item.desc?.lowercase()?.contains(search.lowercase()) == true) {
                    newProducts.add(item)
                  }
                }
              }
            } else {
              products.forEach { item ->
                if (item.subcategory) {
                  newProducts.add(item)
                } else {
                  if (item.level == selectedLevel && item.desc?.lowercase()
                      ?.contains(search.lowercase()) == true
                  ) {
                    newProducts.add(item)
                  }
                }
              }
            }
            productAdapter.setList(newProducts)
          }
      }
    }
  }

  private fun showDialogSlider(
    list: List<String>,
    hideDesc: Boolean = false,
    descText: String = ""
  ) {
    val dialogView =
      LayoutInflater.from(requireContext()).inflate(R.layout.dialog_slider, null)
    val dialogBuilder = AlertDialog.Builder(requireActivity()).setView(dialogView)
    val dialog = dialogBuilder.show()
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setCancelable(true)
    val imagesAdapter = ImagesAdapter(list)
    val dialogRv = dialogView.findViewById<RecyclerView>(R.id.dialog_rv)
    dialogRv.apply {
      layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
      adapter = imagesAdapter
    }
    if (hideDesc) {
      dialogView.findViewById<TextView>(R.id.dialog_desc).visibility = View.GONE
    } else {
      dialogView.findViewById<TextView>(R.id.dialog_desc).text = descText
    }
    dialogView.findViewById<Button>(R.id.dialog_close).setOnClickListener {
      dialog.dismiss()
    }
  }

  override fun onDestroyView() {
    requireActivity().intent.removeExtra("isSubSub")
    super.onDestroyView()
  }
}