import com.android.build.gradle.internal.api.ApkVariantOutputImpl

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.gms.google-services")
}

android {
  namespace = "dev.danifa19.helperbotbr"
  compileSdk = 34

  defaultConfig {
    applicationId = "dev.danifa19.helperbotbr"
    minSdk = 24
    targetSdk = 34
    versionCode = 2
    versionName = "1.1"
    viewBinding.isEnabled = true

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    getByName("debug") {
      isMinifyEnabled = false
    }
    getByName("release") {
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  applicationVariants.all {
    outputs.all {
      val output = this as ApkVariantOutputImpl

      output.outputFileName = "HelperBotBR.apk"
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }
}

dependencies {
  implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
  implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
  implementation("com.github.bumptech.glide:glide:4.16.0")

  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("com.google.firebase:firebase-database:21.0.0")
  implementation("com.google.firebase:firebase-storage:21.0.0")
}