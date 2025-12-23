package io.github.honhimw

import org.gradle.api.Project

class BuildConfigExtension {

    private final Project project

    BuildConfigExtension(Project project) {
        this.project = project
    }

    boolean enabled = true

    String packageName

    String suffix = '.java.in'

    Map<String, String> tokens = [:]


}
