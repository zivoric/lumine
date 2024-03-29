import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.fabricmc.tinyremapper.IMappingProvider
import net.fabricmc.tinyremapper.OutputConsumerPath
import net.fabricmc.tinyremapper.TinyRemapper
import org.apache.commons.io.FileUtils
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.security.MessageDigest
import java.util.LinkedList


buildscript {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.fabricmc.net/")
        }
    }
    dependencies {
        "classpath"(group = "net.fabricmc", name = "tiny-remapper", version = "0.8.7")
        "classpath"(group = "net.fabricmc", name = "stitch", version = project.properties["stitch_version"].toString())
        "classpath"(group = "com.google.code.gson", name = "gson", version = "2.8.9")
        "classpath"(group = "commons-io", name = "commons-io", version = "2.11.0")
    }
}

val minecraft: Configuration by configurations.creating

plugins {
    java
}

val javaVersion = 17

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.fabricmc.net/")
    }
}

val apiVersion = project.properties["lumine_patch"]!!
val minecraftVersion = "1.19.4"

val yarnVersion = "$minecraftVersion+build.${project.properties["yarn_build"]}"

group = project.properties["group"]!!
version = "${minecraftVersion}_${apiVersion}"
description = "lumine-main"

/* File Values */

val minecraftDir = file(".gradle/minecraft/$minecraftVersion")
val minecraftLibs = File(minecraftDir, "libraries")
val clientJar = File(minecraftDir, "$minecraftVersion-client.jar")
val wrappedServerJar = File(minecraftDir, "$minecraftVersion-server-wrapped.jar")
val serverJar = File(minecraftDir, "$minecraftVersion-server.jar")
val mergedJar = File(minecraftDir, "$minecraftVersion-merged.jar")
var intermediaryJar = File(minecraftDir, "$minecraftVersion-intermediary.jar")
var namedJar = File(minecraftDir, "$minecraftVersion-named.jar")
val intermediaryServer = File(minecraftDir, "$minecraftVersion-server-intermediary.jar")
val namedServer = File(minecraftDir, "$minecraftVersion-server-named.jar")

val mappings = file(".gradle/mappings/yarn-$yarnVersion-mergedv2.tiny")
val versionManifest = file(".gradle/manifest/$minecraftVersion.json")
val libraryTree = fileTree(".gradle/minecraft/$minecraftVersion/libraries")
val yarnJar = File(minecraftDir, "yarn-$yarnVersion-mergedv2.jar")

/* Subproject Configurations */

configure(subprojects.filter{it.name != "prisma"}) {
    apply(plugin = "java")
    dependencies {
        compileOnly(project(":prisma"))
    }
}

configure(subprojects.filter{it.name == "lumine-api" || it.name == "lumine-api-client"}) {
    version = apiVersion
    dependencies {

    }
}

subprojects {
    apply(plugin = "java")
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.fabricmc.net/")
        }
    }
    tasks.compileJava {
        dependsOn(":setupEnvironment")
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        }
    }
    dependencies {
        compileOnly("com.google.code.gson:gson:2.10")
    }
}

configure(subprojects.filter{it.name == "lumine-bridge" || it.name == "lumine-bridge-client"}) {
    version = project(":").version

    val intermediaryJar by configurations.creating {
        isCanBeConsumed = true
        isCanBeResolved = false
    }

    val outputName = "${project.name}-$version.jar"
    val outputFile = File(buildDir, "/intermediary/$outputName")

    val intermediary = tasks.register("intermediary") {
        dependsOn("jar")
        group = "build"
        val inputFile = tasks.named("jar").get().outputs.files.singleFile

        inputs.file(inputFile)
        outputs.file(outputFile)
        doLast {
            val dependencies = objects.fileCollection()
                .from(configurations.compileOnly.get().resolve())
                .from(configurations.implementation.get().resolve())
            mapJar(outputFile, inputFile, mappings, dependencies, "named", "intermediary")
        }
    }

    artifacts.add("intermediaryJar", outputFile) {
        builtBy(intermediary.get())
    }

    configurations.compileOnly.get().isCanBeResolved = true
    configurations.implementation.get().isCanBeResolved = true
}

project(":lumine-bridge") {
    dependencies {
        compileOnly(files(namedServer))
        compileOnly(libraryTree)
    }
}

project(":lumine-bridge-client") {
    dependencies {
        compileOnly(files(namedJar))
        compileOnly(libraryTree)
    }
}

/* Create Minecraft Runtime Dependencies */

dependencies {
    minecraft(project(":prisma"))
    minecraft(project(":lumine-api"))
    minecraft(project(":lumine-api-client"))
    minecraft(project(path = ":lumine-bridge", configuration = "intermediaryJar"))
    minecraft(project(path = ":lumine-bridge-client", configuration = "intermediaryJar"))
    minecraft("net.fabricmc:tiny-remapper:0.8.7")
    minecraft(files(yarnJar, libraryTree))
}

/* Tasks */

tasks.register("downloadManifest") {
    group = "build setup"
    val manifestFile = File(minecraftDir, "version_manifest.json")
    outputs.file(manifestFile)
    doLast {
        download("https://launchermeta.mojang.com/mc/game/version_manifest.json", manifestFile)
    }
}

tasks.register("downloadVersionManifest") {
    group = "build setup"
    dependsOn("downloadManifest")
    doLast {
        val manifestFile = tasks.named("downloadManifest").get().outputs.files.singleFile
        val manifestVersion = getManifestVersion(manifestFile, minecraftVersion)
        download(manifestVersion.get("url").asString, versionManifest)
    }
}

tasks.register("downloadMinecraft") {
    group = "build setup"
    dependsOn("downloadVersionManifest")
    inputs.file(versionManifest)
    outputs.files(clientJar, serverJar)
    doLast {
        val jsonObj = JsonParser.parseString(FileUtils.readFileToString(versionManifest, StandardCharsets.UTF_8)).asJsonObject.getAsJsonObject("downloads")
        val clientURL = jsonObj.getAsJsonObject("client").get("url").asString
        val serverURL = jsonObj.getAsJsonObject("server").get("url").asString
        download(clientURL, clientJar)
        download(serverURL, wrappedServerJar)
    }
}

tasks.register("extractServer") {
    group = "build setup"
    dependsOn("downloadMinecraft")
    inputs.file(wrappedServerJar)
    outputs.file(serverJar)
    doLast {
        val trueServerJar = zipTree(wrappedServerJar).matching {
            include("**/server-$minecraftVersion.jar")
        }.singleFile
        trueServerJar.copyTo(serverJar, true)
    }
}

tasks.register("mergeJars") {
    group = "build setup"
    dependsOn("extractServer")
    inputs.files(tasks.named("extractServer").get().outputs.files)
    outputs.file(mergedJar)
    outputs.upToDateWhen {mergedJar.exists()}
    doLast {
        val jarMerger = net.fabricmc.stitch.merge.JarMerger(clientJar, serverJar, mergedJar)
        jarMerger.merge()
        jarMerger.close()
    }
}

tasks.register("downloadLibraries") {
    group = "build setup"
    dependsOn("downloadVersionManifest")
    inputs.file(versionManifest)
    doLast {
        val libraries = JsonParser.parseString(FileUtils.readFileToString(versionManifest, StandardCharsets.UTF_8)).asJsonObject.getAsJsonArray("libraries")
        libraries.forEach {
            val artifact = it.asJsonObject.getAsJsonObject("downloads").getAsJsonObject("artifact")
            val url = artifact.get("url").asString
            val fileName = url.substring(url.lastIndexOf("/") + 1)
            val localFile = File(minecraftLibs, fileName)
            if (!localFile.exists() || !verifyChecksum(localFile, artifact.get("sha1").asString)) {
                download(url, localFile)
            }
            //project.dependencies.add("implementation", files(File(minecraftLibs, fileName)))
        }
    }
}

tasks.register("downloadMappings") {
    group = "build setup"
    outputs.file(yarnJar)
    doLast {
        val url = "https://maven.fabricmc.net/net/fabricmc/yarn/$yarnVersion/yarn-$yarnVersion-mergedv2.jar"
        download(com.google.common.net.UrlEscapers.urlFragmentEscaper().escape(url), yarnJar)
    }
}

tasks.register<Copy>("extractMappings") {
    group = "build setup"
    dependsOn("downloadMappings")
    val outputDir = File(".gradle/mappings/")
    from(zipTree(tasks.named("downloadMappings").get().outputs.files.singleFile)) {
        include("mappings/mappings.tiny")
        rename("mappings.tiny", "../yarn-$yarnVersion-mergedv2.tiny")
    }
    into(outputDir)
}

tasks.register("mapServer") {
    group = "build setup"
    dependsOn("downloadLibraries", "extractServer", "extractMappings")
    outputs.upToDateWhen {intermediaryServer.exists() && namedServer.exists()}
    doLast {
        if (!intermediaryServer.exists()) {
            mapJar(intermediaryServer, serverJar, mappings, minecraftLibs, "official", "intermediary")
        }
        if (!namedServer.exists()) {
            mapJar(namedServer, intermediaryServer, mappings, minecraftLibs, "intermediary", "named")
        }
    }
}

tasks.register("setupEnvironment") {
    group = "build setup"
    dependsOn("mergeJars", "mapServer")
    outputs.upToDateWhen {intermediaryJar.exists() && namedJar.exists()}
    doLast {
        if (!intermediaryJar.exists()) {
            mapJar(intermediaryJar, mergedJar, mappings, minecraftLibs, "official", "intermediary")
        }
        if (!namedJar.exists()) {
            mapJar(namedJar, intermediaryJar, mappings, minecraftLibs, "intermediary", "named")
        }
        //project.dependencies.add("implementation", files(namedJar))
    }
}

tasks.compileJava {
    dependsOn("setupEnvironment")
}

tasks.register("buildAll") {
    group = "build"
    val dependencyNames = arrayOf(":lumine-api:jar", ":lumine-api-client:jar",
        ":lumine-bridge:intermediary", ":lumine-bridge-client:intermediary")
    dependsOn(dependencyNames)
    outputs.files(dependencyNames.asList().stream().map {
        tasks.getByPath(it).outputs.files.singleFile
    }.toArray())
}

tasks.register<JavaExec>("runTestMinecraft") {
    mainClass.set("lumine.bridge.client.launch.LumineClient")
    dependsOn("buildAll")
    val runtimeDir = file(".gradle/test-runtime/$version")
    workingDir = runtimeDir
    val assetsDir = File(runtimeDir, "assets")
    if (!runtimeDir.exists())
        runtimeDir.mkdir()
    if (!assetsDir.exists())
        assetsDir.mkdir()
    val indexVersion = 3
    val assetIndex = File(assetsDir, "indexes/$indexVersion.json")
    if (!assetIndex.exists()) {
        download("https://piston-meta.mojang.com/v1/packages/01a7b1c7940d61f46a1cfbbca0684a7e86affa58/3.json", assetIndex)
        downloadAssets(assetsDir, indexVersion)
    }
    val jarLoc = File(runtimeDir, "/versions/$minecraftVersion/$minecraftVersion.jar")
    clientJar.copyTo(jarLoc, true)
    classpath = files(minecraft.resolve(), jarLoc)
    classpath.forEach {
        println(it.path)
    }
    args = listOf("--username",
        "SmellaGuy",
        "--version",
        "$minecraftVersion-lumine_$apiVersion",
        "--gameDir",
        runtimeDir.path,
        "--assetsDir",
        assetsDir.path,
        "--assetIndex",
        "$indexVersion",
        "--accessToken",
        "--clientId",
        "$minecraftVersion-lumine_$apiVersion")
    jvmArgs = "-Xmx6G -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M"
        .split(" ")

}

/* Utility/Functions */

fun downloadAssets(assetsDir : File, indexVersion : Int) {
    val index = File(assetsDir, "indexes/$indexVersion.json")
    val jsonObj = JsonParser.parseString(FileUtils.readFileToString(index, StandardCharsets.UTF_8)).asJsonObject.getAsJsonObject("objects")
    jsonObj.entrySet().forEach {
        val hash = it.value.asJsonObject.getAsJsonPrimitive("hash").asString
        val path = "${hash.substring(0,2)}/$hash"
        download("https://resources.download.minecraft.net/$path", File(assetsDir, "objects/$path"))
    }
}

fun getManifestVersion(manifest : File, version : String) : JsonObject {
    val jsonObj = JsonParser.parseString(FileUtils.readFileToString(manifest, StandardCharsets.UTF_8)).asJsonObject
    val manifestVersion = jsonObj.get("versions").asJsonArray.firstOrNull {
        it.asJsonObject.get("id").asString.equals(version)
    } ?: throw IllegalArgumentException("Version name $version is invalid.")
    return manifestVersion.asJsonObject
}

fun mapJar(output : File, input : File, mappings : File, libraries : FileCollection, from : String, to : String) {
    if (output.exists()) {
        output.delete()
    }
    val remapper = TinyRemapper.newRemapper()
        //.withMappings(TinyUtils.createTinyMappingProvider(mappings.toPath(), from, to))
        .withMappings(createMappingProvider(mappings, from, to))
        .renameInvalidLocals(true)
        .rebuildSourceFilenames(true)
        .build()

    try {
        val outputConsumer = OutputConsumerPath.Builder(output.toPath()).build()
        outputConsumer.addNonClassFiles(input.toPath())
        remapper.readInputs(input.toPath())

        libraries.files.parallelStream().forEach { file ->
            remapper.readClassPath(file.toPath())
        }
        remapper.apply(outputConsumer)
        outputConsumer.close()
        remapper.finish()
    } catch (e: Exception) {
        remapper.finish()
        throw RuntimeException("Failed to remap jar", e)
    }
}

fun mapJar(output : File, input : File, mappings : File, libDir : File, from : String, to : String) {
    mapJar(output, input, mappings, fileTree(libDir), from, to)
}

fun createMappingProvider(mappings : File, from : String, to : String): IMappingProvider {
    val reader = BufferedReader(InputStreamReader(FileInputStream(mappings)))
    val mapping = net.fabricmc.mapping.tree.TinyMappingFactory.loadWithDetection(reader)
    return IMappingProvider {
        acceptor ->
        for (def : net.fabricmc.mapping.tree.ClassDef in mapping.classes) {
            val className = def.getName(from)
            acceptor.acceptClass(className, def.getName(to))
            for (field in def.fields) {
                acceptor.acceptField(getMember(className, field.getName(from), field.getDescriptor(from)), field.getName(to))
            }
            for (method in def.methods) {
                val methodIdentifier = getMember(className, method.getName(from), method.getDescriptor(from))
                acceptor.acceptMethod(methodIdentifier, method.getName(to))
            }
        }
    }
}

fun getMember(className : String, memberName : String, descriptor : String) : IMappingProvider.Member {
    return IMappingProvider.Member(className, memberName, descriptor)
}

fun download(url : String, dest : File) {
    if (!dest.parentFile.exists())
        dest.parentFile.mkdirs()
    ant.invokeMethod("get", mapOf("src" to url, "dest" to dest))
}

fun move(src : File, dest : File) : File {
    if (!dest.parentFile.exists())
        dest.parentFile.mkdirs()
    ant.invokeMethod("move", mapOf("file" to src, "todir" to dest))
    return File(dest, src.name)
}

fun verifyChecksum(file: File, testChecksum: String): Boolean {
    val sha1 = MessageDigest.getInstance("SHA1")
    val fis = FileInputStream(file)
    val data = ByteArray(1024)
    var read = 0
    while (fis.read(data).also { read = it } != -1) {
        sha1.update(data, 0, read)
    }
    val hashBytes = sha1.digest()
    val sb = StringBuffer()
    for (i in hashBytes.indices) {
        sb.append(((hashBytes[i].toInt() and 0xff) + 0x100).toString(16).substring(1))
    }
    val fileHash = sb.toString()
    return fileHash == testChecksum
}