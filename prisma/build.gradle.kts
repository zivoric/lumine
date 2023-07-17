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
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-api:2.19.0")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.19.0")
    implementation("org.slf4j:slf4j-api:2.0.1")
    implementation("net.sf.jopt-simple:jopt-simple:5.0.4")
    implementation("org.ow2.asm:asm:9.1")
    implementation("org.ow2.asm:asm-tree:9.1")
    implementation("org.ow2.asm:asm-commons:9.1")
    implementation("com.google.guava:guava:28.0-jre")
}

group = "conduit"
version = "1.0.0"
description = "prisma"

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
