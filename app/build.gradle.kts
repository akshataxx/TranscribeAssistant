plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.transcribeassistant"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.transcribeassistant"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // For local development/testing
            buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080\"")
            buildConfigField("String", "STRIPE_PREMIUM_PRICE_ID", "\"price_1SDccWBIj51ZSIefUfPLTqxf\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // For production - UPDATE THIS when you deploy backend
            buildConfigField("String", "API_BASE_URL", "\"https://your-production-domain.com\"")
            buildConfigField("String", "STRIPE_PREMIUM_PRICE_ID", "\"price_1SDccWBIj51ZSIefUfPLTqxf\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.okhttp.logging)
    implementation(libs.material.icons.extended)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.hilt.android)
    implementation(libs.androidx.media3.database)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core.android)
    implementation (libs.play.services.auth)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.room.compiler)
    kapt(libs.moshi.kotlin.codegen)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.firebase.auth)
    implementation(libs.google.auth)
    implementation(libs.androidx.datastore.preferences)
}
