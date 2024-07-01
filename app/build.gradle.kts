plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    implementation("com.prolificinteractive:material-calendarview:1.4.3")
//    implementation("com.michalsvec:single-row-calednar:1.0.0")

    // 캘린더 라이브러리
    implementation ("com.kizitonwose.calendar:view:2.5.2")
    implementation ("com.kizitonwose.calendar:compose:2.5.2")

    // api 통신
    implementation ("com.google.code.gson:gson:2.8.5") // json을 자동적으로 자바 클래스로 변형해주는 라이브러리
    implementation ("com.squareup.retrofit2:retrofit:2.6.0") // 레트로핏
    implementation ("com.squareup.retrofit2:converter-gson:2.6.0") // 레트로핏 - gson 컨버터

}
