package dev.tigrao.bountyhunter.tracker

import org.gradle.api.Plugin
import org.gradle.api.Project

class TrackerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create<TrackerExtension>(
            "tracker",
            TrackerExtension::class.java
        )

        project.tasks
            .create(
                "generateAffectedModulesFile",
                TrackerTask::class.java, extension
            )
    }
}
