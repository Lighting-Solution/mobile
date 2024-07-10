plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "com.ls.m.ls_m_v1"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ls.m.ls_m_v1"
        minSdk = 26
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.filament.android)
    implementation(libs.sceneform.base)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.tiles.tooling.preview)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.androidx.lifecycle.viewmodel.android)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.ui.test.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    implementation("com.prolificinteractive:material-calendarview:1.4.3")
//    implementation("com.michalsvec:single-row-calednar:1.0.0")

    // 캘린더 라이브러리
    implementation ("com.kizitonwose.calendar:view:2.5.2")
    implementation ("com.kizitonwose.calendar:compose:2.5.2")

    // viewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")

    // api 통신
    implementation ("com.google.code.gson:gson:2.8.5") // json을 자동적으로 자바 클래스로 변형해주는 라이브러리
    implementation ("com.squareup.retrofit2:retrofit:2.6.0") // 레트로핏
    implementation ("com.squareup.retrofit2:converter-gson:2.6.0") // 레트로핏 - gson 컨버터

    // OkHttp
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")

    // 프래그먼트
    implementation ("androidx.fragment:fragment-ktx:1.5.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

    // PDFviewer
    implementation ("com.github.DImuthuUpe:AndroidPdfViewer:2.8.1")
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")

    // ChipGroup
    implementation ("com.google.android.material:material:1.4.0")

}
