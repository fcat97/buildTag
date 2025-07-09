# BuildTag

Tag your android builds automatically on git

### Install
**Build the plugin**

```bash
git clone https://github.com/fcat97/buildTag.git
cd buildTag/
./gradlew publishToMavenLocal
```


**Add to project**

in your project's `root/setting.gradle` file

```groovy
pluginManagement {
    repositories {
        ....
        mavenLocal()
    }
}
```
in projects `root/build.gradle` file apply the plugin

```groovy
plugins {
    # ...
    id "com.github.fcat97.buildTag" version "0.1-SNAPSHOT" apply false

    # or ... 
    alias(libs.plugins.buildTag) apply false
}
```

### Usage

apply the plugin to any android project

```groovy
plugins {
    id 'com.android.application' // or 'com.android.library'
    id 'kotlin-android'
    alias(libs.plugins.buildTag)
}
```

1. This will automatically **add git tag on each release build** by running `assembleRelease` or `buildRelease` task.
2. This plugin will also create `BuildConfig.BUILD_ID` on the applied module.

### Other Usage
You can also use it to automate the build output file name

```groovy
// app/build.gradle
android {
    applicationVariants.configureEach { variant ->
        // automate release naming
        variant.outputs.configureEach {
            def date = new Date().format('yyyyMMdd')

            def build = variant.buildType.name // debug or release
            def buildId = "${defaultConfig.versionName}-${project.buildTag}"
            variant.buildConfigField("String", "BUILD_ID", "\"${buildId}\"")

            outputFileName = "myapp_\"${date}_${buildId}_${build}\".apk"
        }
    }
}
```

### Configure

to customize the behaviour of the plugin you can change the default parameters.

```groovy
plugins {
  ...
  alias(libs.plugins.buildTag)
}

buildTag {
  tagPrefix = "" // add a prefix to git tag
  pushToGit = false // automatically push the commit after adding tag
  onTasks = ['assembleRelease', 'bundleRelease'] // when to add tag, default on assembleRelease and buildRelease
}
```



