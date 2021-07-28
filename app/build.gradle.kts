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
            isMinifyEnabled = false
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib:_")

    implementation("androidx.core:core-ktx:_")
    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.work:work-runtime-ktx:_")
    implementation("androidx.constraintlayout:constraintlayout:_")
    implementation("com.google.android.material:material:_")

    implementation("androidx.navigation:navigation-fragment-ktx:_")
    implementation("androidx.navigation:navigation-ui-ktx:_")
    implementation("androidx.navigation:navigation-compose:_")

    implementation("com.google.android.apps.muzei:muzei-api:_")
    implementation("com.squareup.retrofit2:retrofit:_")
    implementation("com.squareup.retrofit2:converter-gson:_")
    implementation("com.squareup.okhttp3:okhttp:_")
    implementation("com.squareup.okhttp3:logging-interceptor:_")

    implementation("com.google.code.gson:gson:_")

    implementation("com.google.firebase:firebase-core:_")
    implementation("com.google.firebase:firebase-crashlytics:_")

    androidTestImplementation("androidx.test:runner:_")
    androidTestImplementation("androidx.test:rules:_")
    // Optional -- UI testing with Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:_")
    androidTestImplementation("androidx.navigation:navigation-testing:_")
}
