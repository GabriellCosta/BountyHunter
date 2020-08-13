package dev.tigrao.bountyhunter.tracker

import org.gradle.api.Plugin
import org.gradle.api.Project

class TrackerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.tasks
            .register(
                "generateAffectedModulesFile",
                TrackerTask::class.java
            )
    }
}
