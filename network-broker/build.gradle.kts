plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.atlasot.netbroker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.atlasot.netbroker"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0-p0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { aidl = true }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    testOptions { animationsDisabled = true }
}

dependencies {
    implementation(project(":core-domain"))
    testImplementation(kotlin("test-junit"))
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:core-ktx:1.6.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
}
