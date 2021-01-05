import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.2")

    defaultConfig {
        applicationId = "nl.jcraane.simpleimagecast"
        minSdkVersion(22)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            resValue("string", "cast_app_id", getAppIdFromLocalProperties())
        }

        getByName("debug") {
            resValue("string", "cast_app_id", getAppIdFromLocalProperties())
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

fun getAppIdFromLocalProperties(): String {
    val properties = Properties()
    val localPropertyFile = File("local.properties")
    return if (localPropertyFile.exists()) {
        localPropertyFile.inputStream().use {
            properties.load(it)
        }

        return properties.getProperty("cast_app_id")
    } else {
        ""
    }
}

dependencies {
    implementation("com.github.bumptech.glide:glide:4.10.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.10.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.21")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.mediarouter:mediarouter:1.0.0")
    implementation("com.google.android.gms:play-services-cast-framework:17.0.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}