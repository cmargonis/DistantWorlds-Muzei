/*
 *  Copyright 2021 Chris Margonis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

fun getImgurApiProperties(): Properties {
    val apiProps = Properties()

    val propsFile = File("apikeys.properties")
    if (!propsFile.exists()) {
        propsFile.createNewFile()
        propsFile.writeText("imgur_client_id=placeholder\n" + "imgur_dw_album_id=placeholderId")
    }

    apiProps.load(FileInputStream(propsFile))
    return apiProps
}

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "com.ultimus.distantworlds_muzei"
        minSdk = 29
        targetSdk = 33
        versionCode = 11
        versionName = "3.5.0"

        val distantWorldsAuthorityValue = "com.ultimus.distantworlds"
        buildConfigField("String", "DISTANT_WORLDS_AUTHORITY", "\"${distantWorldsAuthorityValue}\"")

        val distantWorlds2AuthorityValue = "com.ultimus.distantworlds_two"
        buildConfigField("String", "DISTANT_WORLDS_TWO_AUTHORITY", "\"${distantWorlds2AuthorityValue}\"")

        manifestPlaceholders["distantWorldsAuthority"] = distantWorldsAuthorityValue
        manifestPlaceholders["distantWorlds2Authority"] = distantWorlds2AuthorityValue

        val apiProps = getImgurApiProperties()
        val imgurClient = apiProps["imgur_client_id"]
        buildConfigField("String", "IMGUR_CLIENT_ID", "\"${imgurClient}\"")

        val imgurDWAlbum = apiProps["imgur_dw_album_id"]
        buildConfigField("String", "IMGUR_DW_ALBUM", "\"${imgurDWAlbum}\"")

        val imgurDW2Album = apiProps["imgur_dw2_album_id"]
        buildConfigField("String", "IMGUR_DW2_ALBUM", "\"${imgurDW2Album}\"")

    }

    buildFeatures {
        viewBinding = true
        renderScript = false
        shaders = false
    }

    buildTypes {
        getByName("debug") {
            versionNameSuffix = " Debug"
            isDebuggable = true
            setupMinification(this@android, minificationEnabled = false)
        }
        getByName("release") {
            isDebuggable = false
            setupMinification(this@android, minificationEnabled = true)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    lint {
        warningsAsErrors = true
        informational.add("IconDensities")
    }
}

fun ApplicationBuildType.setupMinification(baseAppModuleExtension: BaseAppModuleExtension, minificationEnabled: Boolean) {
    isMinifyEnabled = minificationEnabled
    isShrinkResources = minificationEnabled
    proguardFiles(
        baseAppModuleExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
    (this as ExtensionAware).configure<CrashlyticsExtension> { mappingFileUploadEnabled = minificationEnabled }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:_")

    implementation("com.google.dagger:hilt-android:_")
    implementation("androidx.hilt:hilt-navigation-fragment:_")
    kapt("com.google.dagger:hilt-android-compiler:_")

    implementation("androidx.core:core-ktx:_")
    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:_")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:_")
    implementation("androidx.work:work-runtime-ktx:_")
    implementation("androidx.constraintlayout:constraintlayout:_")
    implementation("com.google.android.material:material:_")
    implementation("androidx.fragment:fragment-ktx:_")

    implementation("androidx.navigation:navigation-fragment-ktx:_")
    implementation("androidx.navigation:navigation-ui-ktx:_")
    implementation("androidx.navigation:navigation-compose:_")

    implementation("com.google.android.apps.muzei:muzei-api:_")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:_")
    implementation("com.squareup.retrofit2:retrofit:_")
    implementation("com.squareup.retrofit2:converter-gson:_")
    implementation("com.squareup.okhttp3:okhttp:_")
    implementation("com.squareup.okhttp3:logging-interceptor:_")

    implementation("com.google.code.gson:gson:_")
    implementation("com.jakewharton.timber:timber:_")

    implementation("com.google.firebase:firebase-core:_")
    implementation("com.google.firebase:firebase-crashlytics:_")
    implementation("com.google.firebase:firebase-analytics-ktx:_")

    testImplementation("org.junit.jupiter:junit-jupiter-api:_")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:_")
    testImplementation("org.junit.jupiter:junit-jupiter-params:_")
    testImplementation("io.mockk:mockk:_")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
    testImplementation("app.cash.turbine:turbine:_")

    androidTestImplementation("androidx.test:runner:_")
    androidTestImplementation("androidx.test:rules:_")
    // Optional -- UI testing with Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:_")
    androidTestImplementation("androidx.navigation:navigation-testing:_")
}
