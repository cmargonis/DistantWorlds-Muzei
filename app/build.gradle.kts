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
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
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
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "com.ultimus.distantworlds_muzei"
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 6
        versionName = "3.2"

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

    buildTypes {
        getByName("debug") {
            versionNameSuffix = " Debug"

        }
        getByName("release") {
            minifyEnabled(false)
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.10")
    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.appcompat:appcompat:1.3.0-alpha02")
    implementation("androidx.work:work-runtime-ktx:2.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.1")
    implementation("com.google.android.material:material:1.3.0-alpha02")

    implementation("com.google.android.apps.muzei:muzei-api:3.4.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.8.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.8.1")

    implementation("com.google.code.gson:gson:2.8.6")

    implementation("com.google.firebase:firebase-core:17.5.0")
    implementation("com.google.firebase:firebase-crashlytics:17.2.1")

    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test:rules:1.3.0")
    // Optional -- UI testing with Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}
