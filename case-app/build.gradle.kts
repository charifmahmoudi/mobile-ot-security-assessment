plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.atlasot.scout"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.atlasot.scout"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0-p0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { aidl = true }
    sourceSets["androidTest"].assets.srcDir(rootProject.file("testdata/research"))

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
    // SQLCipher 4.18.0 declares minCompileSdk 37; keep this line compatible with the compileSdk 35 baseline.
    implementation("net.zetetic:sqlcipher-android:4.17.0@aar")
    implementation("androidx.sqlite:sqlite:2.6.2")
    testImplementation(kotlin("test-junit"))
    // The test-only content provider is instantiated in the instrumentation APK's
    // process before the target application. Package the runtime explicitly so a
    // real content:// PCAP import can execute independently of the target APK.
    androidTestImplementation(kotlin("stdlib"))
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:core-ktx:1.6.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
}
