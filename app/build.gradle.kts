plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.cinehive"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.cinehive"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "TMDB_API_KEY",
            if (project.hasProperty("TMDB_API_KEY")) project.property("TMDB_API_KEY").toString() else "")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }



}

dependencies {
    val nav_version = "2.8.4"
    val fragment_version = "1.8.5"
    val lifecycle_version = "2.8.7"

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Views/Fragments Integration
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    // Fragments Integration
    implementation("androidx.fragment:fragment-ktx:$fragment_version")

    // ViewModel Integration
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")

    // API Integration
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
}