plugins {
    id("com.android.application")//
    id("com.google.gms.google-services")//
}

android {
    namespace = "vn.tlu.edu.phungxuanpphuong.btl"
    compileSdk = 35

    defaultConfig {
        applicationId = "vn.tlu.edu.phungxuanpphuong.btl"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))//
    implementation("com.google.firebase:firebase-analytics")//
    implementation("com.google.firebase:firebase-database")//
    implementation("com.google.firebase:firebase-auth")//
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation ("com.google.firebase:firebase-firestore:24.9.1")
    implementation ("com.google.code.gson:gson:2.10.1")


}