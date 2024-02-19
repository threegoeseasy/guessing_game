// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false

}



buildscript {
    val navVersion by extra("2.6.0")
    val composeVersion by extra("1.6.1")
    val roomVersion by extra { "2.6.1" }


    dependencies {

        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    }
}