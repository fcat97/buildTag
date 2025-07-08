package com.github.fcat97.buildTag

import org.gradle.api.Action

class BuildTagExtension {
    String tagPrefix = ""
    boolean pushToGit = false
    String runOnTask = "assembleRelease"

    BuildMetadata metadata
}
