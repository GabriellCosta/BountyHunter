package dev.tigrao.bountyhunter.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class TrackerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.tasks
            .create(
                "generateAffectedModulesFile",
                TrackerTask::class.java)
    }
}
