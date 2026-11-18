plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.recetario"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.recetario"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Pruebas unitarias de la lógica (src/test)
    testImplementation("junit:junit:4.13.2")
}
