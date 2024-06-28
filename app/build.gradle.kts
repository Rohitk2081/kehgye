plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.quotes.kehgye"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.quotes.kehgye"
        minSdk = 28
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    //canv
        implementation("com.google.firebase:firebase-firestore:24.3.0")
        implementation("com.google.firebase:firebase-auth:21.0.6")
        implementation("com.google.firebase:firebase-storage:20.0.0")
        implementation("com.github.bumptech.glide:glide:4.13.2")
        annotationProcessor("com.github.bumptech.glide:compiler:4.13.2")



    implementation("com.google.android.material:material:1.4.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")




    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.google.firebase:firebase-auth:21.0.1")
    implementation("com.google.firebase:firebase-database:20.0.3")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.car.ui.lib)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}