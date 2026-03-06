plugins {
    id("com.android.application")
}

android {
    namespace = "com.commit451.modalbottomsheetdialogfragment.sample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.commit451.modalbottomsheetdialogfragment.sample"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.1")

    implementation(project(":modalbottomsheetdialogfragment"))
}
