plugins { id("com.android.application") }
android {
    namespace = "edu.hitsz.aircraftwar2026"
    compileSdk = 37
    buildToolsVersion = "36.0.0"
    defaultConfig {
        applicationId = "edu.hitsz.aircraftwar2026"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "edu.hitsz.application.Lab2Instrumentation"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
