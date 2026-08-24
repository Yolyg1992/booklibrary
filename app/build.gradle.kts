import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("keystore.properties")

if (keystorePropertiesFile.exists()) {
    keystorePropertiesFile.inputStream().use(keystoreProperties::load)
}

android {
    namespace = "com.tustockpro.booklibrary"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.tustockpro.booklibrary"

        minSdk = 24
        targetSdk = 36

        versionCode = 2
        versionName = "1.1"

        multiDexEnabled = false

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (keystorePropertiesFile.exists()) {
            create("release") {
                storeFile = rootProject.file(
                    keystoreProperties.getProperty("storeFile")
                )
                storePassword =
                    keystoreProperties.getProperty("storePassword")
                keyAlias =
                    keystoreProperties.getProperty("keyAlias")
                keyPassword =
                    keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }

        release {
            isMinifyEnabled = false
            signingConfig =
                signingConfigs.findByName("release")

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // ANDROID


    implementation("androidx.core:core-ktx:1.18.0")

    implementation(
        "androidx.activity:activity-compose:1.13.0"
    )


    // LIFECYCLE / VIEWMODEL


    implementation(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.10.0"
    )

    implementation(
        "androidx.lifecycle:lifecycle-runtime-compose:2.10.0"
    )

    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0"
    )


    // COMPOSE


    implementation(
        platform(
            "androidx.compose:compose-bom:2026.06.01"
        )
    )

    implementation("androidx.compose.ui:ui")

    implementation(
        "androidx.compose.ui:ui-tooling-preview"
    )

    implementation(
        "androidx.compose.foundation:foundation"
    )

    implementation(
        "androidx.compose.material3:material3"
    )

implementation(
    "androidx.compose.material:material-icons-extended"
    )

debugImplementation(
    "androidx.compose.ui:ui-tooling"
)


    // NAVIGATION


    implementation(
        "androidx.navigation:navigation-compose:2.9.3"
    )

    // COROUTINES


    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2"
    )


    // ROOM


    implementation(
        "androidx.room:room-runtime:2.8.4"
    )

    implementation(
        "androidx.room:room-ktx:2.8.4"
    )

    ksp(
        "androidx.room:room-compiler:2.8.4"
    )


    // DATASTORE


    implementation(
        "androidx.datastore:datastore-preferences:1.2.1"
    )


    // RETROFIT

    implementation(
        "com.squareup.retrofit2:retrofit:3.0.0"
    )

    implementation(
        "com.squareup.retrofit2:converter-gson:3.0.0"
    )


    // OKHTTP


    implementation(
        "com.squareup.okhttp3:okhttp:5.1.0"
    )

    implementation(
        "com.squareup.okhttp3:logging-interceptor:5.1.0"
    )


    // COIL

    implementation(
        "io.coil-kt.coil3:coil-compose:3.3.0"
    )

    implementation(
        "io.coil-kt.coil3:coil-network-okhttp:3.3.0"
    )


    // TEST


    testImplementation(
        "junit:junit:4.13.2"
    )

    testImplementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2"
    )

    androidTestImplementation(
        "androidx.test.ext:junit:1.3.0"
    )

    androidTestImplementation(
        "androidx.test.espresso:espresso-core:3.7.0"
    )
}
