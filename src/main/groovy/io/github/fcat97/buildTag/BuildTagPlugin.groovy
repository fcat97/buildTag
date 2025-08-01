package io.github.fcat97.buildTag


import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildTagPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def ext = project.extensions.create("buildTag", BuildTagExtension)

        def commitHash = "git rev-parse --short HEAD".execute().text.trim()
        def timestamp = new Date().format('yyyyMMddHHmmss')
        def uid = timestamp[-6..-1]

        def buildTag = "${commitHash}-${uid}"
        project.ext.buildTag = buildTag
        // println "✅ Prepared build ID: ${project.ext.buildTag}"

        // Add it to BuildConfig
        project.plugins.withId('com.android.application') {
            def version = project.android.defaultConfig.versionName
            def tag = "${version}-${project.ext.buildTag}"
            println "✅ adding BUILD_ID to BuildConfig: ${tag}"
            project.android.defaultConfig.buildConfigField 'String', 'BUILD_ID', "\"${tag}\""
        }

        project.plugins.withId('com.android.library') {
            def tag = project.ext.buildTag
            println "✅ BUILD_ID --> BuildConfig: ${tag}"
            project.android.defaultConfig.buildConfigField 'String', 'BUILD_ID', "\"${tag}\""
        }

        project.afterEvaluate {
            // Git cleanliness check
            def checkGitClean = project.tasks.register("checkGitClean") {
                doLast {
                    def status = "git status --porcelain".execute().text.trim()
                    if (status) {
                        throw new GradleException("❌ Git workspace is dirty! Please commit or stash changes before release.")
                    }
                }
            }

            // Git tag task
            def tagGitRelease = project.tasks.register("tagGitRelease") {
                doLast {
                    def tag = "${ext.tagPrefix}${buildTag}"
                    println "🏷️ Tagging current commit with $tag"
                    "git tag ${tag}".execute().waitFor()
                    if (ext.pushToGit) "git push origin ${tag}".execute().waitFor()
                }
            }

            // Wire into assembleRelease
            project.tasks.matching { ext.onTasks.contains(it.name) }.configureEach {
                dependsOn(checkGitClean)
                finalizedBy(tagGitRelease)
            }
        }
    }
}
