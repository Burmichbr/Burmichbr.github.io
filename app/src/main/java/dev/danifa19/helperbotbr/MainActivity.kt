package dev.danifa19.helperbotbr

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dev.danifa19.helperbotbr.viewmodel.AppViewModel

class MainActivity : AppCompatActivity() {
  val appViewModel: AppViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }
}