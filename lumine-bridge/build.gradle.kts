plugins {
    java
}

description = "lumine-bridge"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":lumine-api"))
}