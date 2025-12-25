package io.github.honhimw

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishPlugin
import groovy.xml.XmlParser
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPlatformPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningPlugin

import java.nio.charset.StandardCharsets

class PublishCfgPlugin implements Plugin<Project> {

    private Project project

    @Override
    void apply(Project project) {
        this.project = project
        project.plugins.apply SigningPlugin
        project.plugins.apply MavenPublishPlugin

        project.afterEvaluate {

			project.extensions.configure(PublishingExtension) { publishing ->
				def group = project.group.toString()
				def name = project.name
				def ver = project.version.toString()
				def packageKind
				SoftwareComponent target

				project.plugins.withType(JavaLibraryPlugin).configureEach {
					target = project.components.java
					packageKind = 'jar'
				}

				project.plugins.withType(JavaPlatformPlugin).configureEach {
					target = project.components.javaPlatform
					if (name.contains('bom')) {
						packageKind = 'pom'
					} else {
						packageKind = 'jar'
					}
				}

				publishing.repositories {
					it.maven {
						it.name = 'tmp'
						it.url = project.layout.buildDirectory.dir("/publishing-repository").get().asFile.toURI()
					}
				}

				publishing.publications {publication ->
					publication.create('library', MavenPublication) { mavenPublication ->
						mavenPublication.versionMapping {vms ->
							vms.allVariants {
								it.fromResolutionResult()
							}
						}
						mavenPublication.groupId = group
						mavenPublication.artifactId = name
						mavenPublication.version = ver
						mavenPublication.pom {
							it.packaging = packageKind
						}
						mavenPublication.from target
					}
				}
			}

            project.extensions.configure(MavenPublishBaseExtension) {
                it.publishToMavenCentral false
                it.signAllPublications()

                it.coordinates project.group as String, project.name, project.version as String

                it.pom { pom ->
                    pom.name.set project.name
                    pom.description.set project.description
                    pom.url.set 'https://github.com/honhimW/spring-cloud-nacos'
                    pom.licenses { licenses ->
                        licenses.license { license ->
                            license.name.set 'The Apache License, Version 2.0'
                            license.url.set 'https://www.apache.org/licenses/LICENSE-2.0.txt'
                            license.distribution.set 'https://www.apache.org/licenses/LICENSE-2.0.txt'
                        }
                    }
                    pom.developers { developers ->
                        developers.developer { developer ->
                            developer.id.set 'honhimw'
                            developer.name.set 'honhimw'
                            developer.url.set 'https://honhimW.github.io'
                            developer.email.set 'honhimw@outlook.com'
                        }
                    }
                    pom.scm { scm ->
                        scm.url.set 'https://github.com/honhimW/spring-cloud-nacos'
                        scm.connection.set 'scm:git:git://github.com/honhimW/spring-cloud-nacos.git'
                        scm.developerConnection.set 'scm:git:ssh://github.com/honhimW/spring-cloud-nacos.git'
                    }
                }
            }

        }

        project.tasks.register('deployIfAbsent') {
            it.group = 'deploy'
			it.dependsOn ":${project.name}:publishLibraryPublicationToMavenCentralRepository"

			it.onlyIf {
				def latestVersion = getLatestVersion(project.name)
				println "module: ${project.name}, current: ${project.version}, latest: ${latestVersion}"
				return project.version != latestVersion
			}
        }

        project.tasks.register('doGenerate') {
            it.group = 'deploy'
			it.dependsOn ":${project.name}:publishLibraryPublicationToTmpRepository"
        }
    }

    String getLatestVersion(String artifactId) {
        def url = "https://repo1.maven.org/maven2/io/github/honhimw/${artifactId}/maven-metadata.xml"
		def connection = url.toURL().openConnection()
		connection.setConnectTimeout(5000)
		connection.setReadTimeout(5000)
		if (connection instanceof HttpURLConnection) {
			def httpConn = (HttpURLConnection) connection
			if (httpConn.responseCode == 404) {
				return ''
			}
		}
		def response = connection.inputStream.withCloseable { stream -> stream.bytes }
        def xmlContent = new XmlParser().parseText(new String(response, StandardCharsets.UTF_8))
        def latestVersion = xmlContent?.versioning?.latest?.text()
        return latestVersion
    }

}
