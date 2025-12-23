package io.github.honhimw

import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.Copy

class BuildConfigPlugin implements Plugin<Project> {

    static final String BUILD_CONFIG_REPLACEMENT_TASK_NAME = 'buildConfigReplacement'

    @Override
    void apply(Project project) {
        def buildConfigExtension = project.extensions.create('buildConfig', BuildConfigExtension, project)

        project.afterEvaluate {
            if (!buildConfigExtension.tokens.isEmpty()) {
				String packageName = buildConfigExtension.packageName ?: project.group.toString()
				String path = packageName.replace('.', '/')
				project.tasks.register(BUILD_CONFIG_REPLACEMENT_TASK_NAME, Copy) { copy ->
					copy.from "src/main/java/${path}"
					copy.include '**/*' + buildConfigExtension.suffix
					copy.includeEmptyDirs = false
					copy.rename buildConfigExtension.suffix, '.java'
					copy.filter ReplaceTokens, tokens: buildConfigExtension.tokens
					copy.into project.layout.buildDirectory.dir("generated/sources/buildConfig/java/main/${path}")
				}
				project.tasks.maybeCreate(JavaPlugin.COMPILE_JAVA_TASK_NAME).dependsOn(BUILD_CONFIG_REPLACEMENT_TASK_NAME)
				project.tasks.maybeCreate('sourcesJar').dependsOn(BUILD_CONFIG_REPLACEMENT_TASK_NAME)
				project.extensions.configure(JavaPluginExtension) {
					it.sourceSets.named('main') {
						it.java.srcDirs(project.layout.buildDirectory.dir("generated/sources/buildConfig/java/main"))
					}
				}
            }
        }
    }
}
