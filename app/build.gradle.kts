import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
}

// Load keystore properties
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

// Load local.properties (gitignored) for dev overrides
val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

val vmUrl = "https://34-151-189-90.sslip.io"
val localUrl = localProperties.getProperty("LOCAL_API_URL") ?: vmUrl

android {
    namespace = "com.example.transcribeassistant"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.contentcategorise.transcribeassistant"
        minSdk = 26
        targetSdk = 35
        versionCode = 11
        versionName = "1.0.11"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = File(rootProject.projectDir, keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                storeType = "PKCS12"
            }
        }
    }

    buildTypes {
        debug {
            // Uses LOCAL_API_URL from local.properties if set, otherwise falls back to VM
            buildConfigField("String", "API_BASE_URL", "\"$localUrl\"")
            buildConfigField("String", "GOOGLE_PLAY_PRODUCT_ID_MONTHLY", "\"premium_monthly\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Production backend on GCE with HTTPS
            buildConfigField("String", "API_BASE_URL", "\"https://34-151-189-90.sslip.io\"")
            buildConfigField("String", "GOOGLE_PLAY_PRODUCT_ID_MONTHLY", "\"premium_monthly\"")
            signingConfig = signingConfigs.getByName("release")
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
    implementation(libs.androidx.compose.ui.text.google.fonts)
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

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging)
    implementation(libs.google.auth)
    implementation(libs.androidx.datastore.preferences)

    // Google Play Billing
    implementation(libs.billing)

    // Emoji rendering on older Android versions
    implementation(libs.androidx.emoji2.bundled)

}