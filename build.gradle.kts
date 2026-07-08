plugins {
	id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT"
	id("me.modmuss50.mod-publish-plugin") version "2.1.1"
}

version = project.property("mod_version")!!
group = project.property("maven_group")!!

base {
	archivesName = project.property("archives_base_name") as String
}

repositories {
}

dependencies {
	minecraft("com.mojang:minecraft:${property("minecraft_version")}")
	implementation("net.fabricmc:fabric-loader:${property("loader_version")}")

	implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
}

tasks.processResources {
	val props = mapOf("version" to project.version)
	inputs.properties(props)

	filesMatching("fabric.mod.json") { expand(props) }
}

java {
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}

publishMods {
	file.set(tasks.jar.flatMap { it.archiveFile })
	displayName = property("mod_version") as String
	version = property("mod_version") as String

	var change: String? = null
	file("CHANGELOG.md")?.let {
		if (it.exists()) {
			change = it.readText()
		}
	}
	changelog = change ?: "* nothing"

	type = STABLE
	modLoaders.add("fabric")

	dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null

	modrinth {
		projectId = "P381sJTu"
		accessToken = providers.environmentVariable("MODRINTH_TOKEN")
		minecraftVersions.addAll(property("minecraft_targets_publishing").toString().split(' '))
		requires {
			slug = "fabric-api"
		}
	}
}

