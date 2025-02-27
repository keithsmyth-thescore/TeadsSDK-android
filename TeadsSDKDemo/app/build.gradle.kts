import tv.teads.AndroidLibConfig
import tv.teads.Libs
import tv.teads.versionCode
import tv.teads.versionName

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "tv.teads.teadssdkdemo"
    compileSdk = AndroidLibConfig.compileSdk

    defaultConfig {
        applicationId = "tv.teads.teadssdkdemo"
        minSdk = AndroidLibConfig.minSdk
        targetSdk = AndroidLibConfig.targetSdk
        versionCode = project.versionCode
        versionName = project.versionName
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Libs.AndroidX.APPCOMPAT)
    implementation(Libs.AndroidX.CONSTRAINT_LAYOUT)
    implementation(Libs.COROUTINES_CORE)
    implementation(Libs.AndroidX.CARDVIEW)
    implementation(Libs.AndroidX.WEBKIT)
    implementation(Libs.MATERIAL)

    // Teads SDK
    implementation(Libs.Teads.sdk(project.versionName)) {
        isTransitive = true
    }
    // Teads Adapters
    implementation(Libs.Teads.admobAdapter(project.versionName))
    implementation(Libs.Teads.applovinAdapter(project.versionName))
    implementation(Libs.Teads.smartAdapter(project.versionName))
    implementation(Libs.Teads.prebidAdapter(project.versionName))

    implementation(Libs.PLAY_SERVICES_ADS)
    implementation(Libs.APPLOVIN_SDK)
    implementation(Libs.SMART_CORE_SDK)
    implementation(Libs.SMART_DISPLAY_SDK) {
        isTransitive = true
    }
    implementation(Libs.PREBID_SDK)

    implementation(projects.webviewhelper)

    //Huawei ads identifier sdk
    implementation(Libs.HUAWEI_IDENTIFIER)

    implementation("com.squareup.leakcanary:leakcanary-android:2.14")

    testImplementation(Libs.Test.JUNIT)
}
