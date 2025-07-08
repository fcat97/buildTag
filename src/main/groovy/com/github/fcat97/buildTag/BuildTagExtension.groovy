package com.github.fcat97.buildTag

class BuildTagExtension {
    String tagPrefix = ""
    boolean pushToGit = false
    List<String> onTasks = ['assembleRelease', 'bundleRelease']
}
