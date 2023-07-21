plugins {
    java
}

description = "lumine-api-client"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lumine-api"))
}