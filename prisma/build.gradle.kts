plugins {
    java
    `maven-publish`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
    maven {
        url = uri("https://maven.fabricmc.net/")
    }
}

dependencies {
    compileOnly("org.apache.logging.log4j:log4j-api:2.19.0")
    compileOnly("org.apache.logging.log4j:log4j-slf4j2-impl:2.19.0")
    compileOnly("org.slf4j:slf4j-api:2.0.1")
    compileOnly("net.sf.jopt-simple:jopt-simple:5.0.4")
    compileOnly("org.ow2.asm:asm:9.1")
    compileOnly("org.ow2.asm:asm-tree:9.1")
    compileOnly("org.ow2.asm:asm-commons:9.1")
    compileOnly("net.fabricmc:tiny-remapper:0.8.7")
    compileOnly("com.google.guava:guava:28.0-jre")
    compileOnly("com.guardsquare:proguard-base:7.3.2")
    compileOnly("cuchaz:enigma:2.3.1")
}

version = "1.0.0"
description = "prisma"

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
