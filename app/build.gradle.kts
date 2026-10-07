import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.rentaya.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.rentaya.rentola"
        minSdk = 24
        targetSdk = 36
        versionCode = 6
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Claves Supabase: supabase.properties (en el repo, clave anon pública)
        // y opcionalmente local.properties (override local, en .gitignore).
        // Si ambas quedan vacías, la app funciona 100% offline con SampleData.
        val supabaseProps = Properties()
        val supabaseFile = rootProject.file("supabase.properties")
        if (supabaseFile.exists()) {
            supabaseFile.inputStream().use { supabaseProps.load(it) }
        }
        val localProps = Properties()
        val localFile = rootProject.file("local.properties")
        if (localFile.exists()) {
            localFile.inputStream().use { localProps.load(it) }
        }
        fun propSupabase(clave: String): String {
            val desdeLocal = localProps.getProperty(clave)?.takeIf { it.isNotBlank() }
            val desdeRepo = supabaseProps.getProperty(clave)?.takeIf { it.isNotBlank() }
            return desdeLocal ?: desdeRepo ?: ""
        }
        val supabaseUrl = propSupabase("SUPABASE_URL")
        val supabaseAnonKey = propSupabase("SUPABASE_ANON_KEY")
        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
        // Nunca true en release. En debug solo si local.properties lo pide.
        buildConfigField("boolean", "DEBUG_OMITIR_VERIFICACION_CORREO", "false")
    }

    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val hasKeystore = keystorePropertiesFile.exists()
    
    if (hasKeystore) {
        signingConfigs {
            create("release") {
                val keystoreProperties = Properties()
                keystoreProperties.load(keystorePropertiesFile.inputStream())
                
                storeFile = file(keystoreProperties["storeFile"].toString())
                storePassword = keystoreProperties["storePassword"].toString()
                keyAlias = keystoreProperties["keyAlias"].toString()
                keyPassword = keystoreProperties["keyPassword"].toString()
            }
        }
    }

    buildTypes {
        debug {
            val omitirVerificacion = rootProject.file("local.properties").let { archivo ->
                if (!archivo.exists()) return@let false
                val props = Properties()
                archivo.inputStream().use { props.load(it) }
                props.getProperty("DEBUG_OMITIR_VERIFICACION_CORREO") == "true"
            }
            buildConfigField(
                "boolean",
                "DEBUG_OMITIR_VERIFICACION_CORREO",
                omitirVerificacion.toString()
            )
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "DEBUG_OMITIR_VERIFICACION_CORREO", "false")
            if (hasKeystore) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.5")
    
    // DataStore for preferences
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    
    // Room for local database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // HTTP para Supabase REST + Auth + Storage (sin SDK pesado)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Fotos (assets, Uri de galería y URLs de Storage)
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Mapa OSM (sin clave de facturación de Google)
    implementation("org.osmdroid:osmdroid-android:6.1.20")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.12.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
