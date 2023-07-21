plugins {
    java
}

description = "lumine-bridge-client"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":lumine-api"))
    compileOnly(project(":lumine-api-client"))
    compileOnly(project(":lumine-bridge"))
    compileOnly("net.fabricmc:fabric-loader:0.14.9") // stops EnvType warnings from occurring
}