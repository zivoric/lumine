import com.google.gson.*
import net.fabricmc.tinyremapper.*
import org.apache.commons.io.FileUtils
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

buildscript {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.fabricmc.net/")
        }
    }
    dependencies {
        "classpath"(group = "net.fabricmc", name = "tiny-remapper", version = "0.8.4")
        "classpath"(group = "net.fabricmc", name = "stitch", version = project.properties["stitch_version"].toString())
        "classpath"(group = "com.google.code.gson", name = "gson", version = "2.8.8")
        "classpath"(group = "commons-io", name = "commons-io", version = "2.11.0")
    }
}

plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

val conduitVersion = project.properties["conduit_patch"]!!
val minecraftVersion = "1.19"

group = project.properties["group"]!!
version = "${minecraftVersion}_${conduitVersion}"

val yarnVersion = "$minecraftVersion+build.${project.properties["yarn_build"]}"
val patchName = "$minecraftVersion-conduit_$conduitVersion"
val patchTitle = "$minecraftVersion-Conduit_$conduitVersion"
val libraryPatch = "conduit-main-$version"

repositories {
    mavenCentral()
    maven {
        url = uri("https://libraries.minecraft.net/")
    }
    maven {
        url = uri("https://maven.fabricmc.net/")
    }
}

val conduitBuild = file("build/conduit")
val minecraftDir = file(".gradle/minecraft/$minecraftVersion")
val minecraftLibs = File(minecraftDir, "libraries")
val clientJar = File(minecraftDir, "$minecraftVersion-client.jar")
val serverJar = File(minecraftDir, "$minecraftVersion-server.jar")
val mergedJar = File(minecraftDir, "$minecraftVersion-merged.jar")
var intermediaryJar = File(minecraftDir, "$minecraftVersion-intermediary.jar")
var namedJar = File(minecraftDir, "$minecraftVersion-named.jar")
val conduitNamed = file("build/libs/conduit-$version.jar")
val conduitIntermediary = File(conduitBuild, "intermediary/intermediary-$libraryPatch.jar")
val conduitObf = File(conduitBuild, "$libraryPatch.jar")
val mappings = file(".gradle/mappings/yarn-$yarnVersion-mergedv2.tiny")
val versionManifest = file(".gradle/manifest/$minecraftVersion.json")

dependencies {
    compileOnly(fileTree(".gradle/minecraft/$minecraftVersion/libraries"))
    compileOnly(files(namedJar))
    implementation("net.minecraft:launchwrapper:1.12")
    implementation("org.ow2.asm:asm:9.1")
    implementation("org.ow2.asm:asm-tree:9.1")
    implementation("org.ow2.asm:asm-commons:9.1")
    implementation("com.google.code.gson:gson:2.8.8")
}

tasks.register("downloadManifest") {
    val manifestFile = File(minecraftDir, "version_manifest.json")
    outputs.file(manifestFile)
    doLast {
        download("https://launchermeta.mojang.com/mc/game/version_manifest.json", manifestFile)
    }
}

fun getManifestVersion(manifest : File, version : String) : JsonObject {
    val jsonObj = JsonParser.parseString(FileUtils.readFileToString(manifest, StandardCharsets.UTF_8)).asJsonObject
    val manifestVersion = jsonObj.get("versions").asJsonArray.firstOrNull {
        it.asJsonObject.get("id").asString.equals(version)
    } ?: throw IllegalArgumentException("Version name $version is invalid.")
    return manifestVersion.asJsonObject
}

tasks.register("downloadVersionManifest") {
    dependsOn("downloadManifest")
    doLast {
        val manifestFile = tasks.named("downloadManifest").get().outputs.files.singleFile
        val manifestVersion = getManifestVersion(manifestFile, minecraftVersion)
        download(manifestVersion.get("url").asString, versionManifest)
    }
}

tasks.register("downloadMinecraft") {
    dependsOn("downloadVersionManifest")
    inputs.file(versionManifest)
    outputs.files(clientJar, serverJar)
    doLast {
        val jsonObj = JsonParser.parseString(FileUtils.readFileToString(versionManifest, StandardCharsets.UTF_8)).asJsonObject.getAsJsonObject("downloads")
        val clientURL = jsonObj.getAsJsonObject("client").get("url").asString
        val serverURL = jsonObj.getAsJsonObject("server").get("url").asString
        download(clientURL, clientJar)
        download(serverURL, serverJar)
    }
}

tasks.register("mergeJars") {
    dependsOn("downloadMinecraft")
    inputs.files(tasks.named("downloadMinecraft").get().outputs.files)
    outputs.file(mergedJar)
    outputs.upToDateWhen {mergedJar.exists()}
    doLast {
        val jarMerger = net.fabricmc.stitch.merge.JarMerger(clientJar, serverJar, mergedJar)
        jarMerger.merge()
        jarMerger.close()
    }
}

tasks.register("downloadLibraries") {
    dependsOn("downloadVersionManifest")
    inputs.file(versionManifest)
    doLast {
        val libraries = JsonParser.parseString(FileUtils.readFileToString(versionManifest, StandardCharsets.UTF_8)).asJsonObject.getAsJsonArray("libraries")
        libraries.forEach {
            val url = it.asJsonObject.getAsJsonObject("downloads").getAsJsonObject("artifact").get("url").asString
            val fileName = url.substring(url.lastIndexOf("/") + 1)
            download(url, File(minecraftLibs, fileName))
            //project.dependencies.add("implementation", files(File(minecraftLibs, fileName)))
        }
    }
}

tasks.register("downloadMappings") {
    val outputFile = File(minecraftDir, "yarn-$yarnVersion-mergedv2.jar")
    outputs.file(outputFile)
    doLast {
        val url = "https://maven.fabricmc.net/net/fabricmc/yarn/$yarnVersion/yarn-$yarnVersion-mergedv2.jar"
        download(com.google.common.net.UrlEscapers.urlFragmentEscaper().escape(url), outputFile)
    }
}

tasks.register<Copy>("extractMappings") {
    dependsOn("downloadMappings")
    val outputDir = File(".gradle/mappings/")
    from(zipTree(tasks.named("downloadMappings").get().outputs.files.singleFile)) {
        include("mappings/mappings.tiny")
        rename("mappings.tiny", "../yarn-$yarnVersion-mergedv2.tiny")
    }
    into(outputDir)
}

tasks.register("setupEnvironment") {
    dependsOn("downloadLibraries", "mergeJars", "extractMappings")
    outputs.upToDateWhen {intermediaryJar.exists() && namedJar.exists()}
    doLast {
        mapJar(intermediaryJar, mergedJar, mappings, minecraftLibs, "official", "intermediary")
        mapJar(namedJar, intermediaryJar, mappings, minecraftLibs, "intermediary", "named")
        //project.dependencies.add("implementation", files(namedJar))
    }
}

tasks.compileJava {
    dependsOn("setupEnvironment")
}

tasks.register("conduitIntermediary") {
    dependsOn("build")
    doLast {
        namedJar = move(namedJar, minecraftLibs)
        mapJar(conduitIntermediary, conduitNamed, mappings, minecraftLibs, "named", "intermediary")
        namedJar = move(namedJar, minecraftDir)
    }
}

tasks.register("conduit") {
    dependsOn("conduitIntermediary")
    doLast {
        intermediaryJar = move(intermediaryJar, minecraftLibs)
        mapJar(conduitObf, conduitIntermediary, mappings, minecraftLibs, "intermediary", "official")
        println("Conduit jar successfully saved to ${conduitObf.absolutePath}")
        intermediaryJar = move(intermediaryJar, minecraftDir)
    }
}

fun mapJar(output : File, input : File, mappings : File, libraries : File, from : String, to : String) {
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

        libraries.walk().forEach { file ->
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