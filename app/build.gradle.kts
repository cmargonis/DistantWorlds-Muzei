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
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebaseCrashlytics)
    kotlin("kapt")
    alias(libs.plugins.dagger.hilt)
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
    namespace = "com.ultimus.distantworlds_muzei"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.ultimus.distantworlds_muzei"
        minSdk = 29
        targetSdk = 35
        versionCode = 11
        versionName = "3.5.0"

        buildFeatures {
            buildConfig = true
        }

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

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(19)
        vendor = JvmVendorSpec.AZUL
    }
}

dependencies {
    implementation(libs.dagger.hilt)
    implementation(libs.hilt.navigation)
    kapt(libs.hilt.kapt)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    implementation(libs.androidx.fragment.ktx)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.muzei.api)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.gson)
    implementation(libs.timber)

    implementation(libs.firebase.core)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics.ktx)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
