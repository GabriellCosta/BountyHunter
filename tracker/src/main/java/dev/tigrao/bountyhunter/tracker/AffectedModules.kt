package dev.tigrao.bountyhunter.tracker

import org.gradle.api.Project

internal class AffectedModules(
    private val project: Project
) {

    private val projectGraph: ProjectGraph by lazy {
        ProjectGraph(project.rootProject)
    }

    fun getAffectedModules(changedFiles: List<String>): Pair<Set<Project>, Set<String>> {
        val nonModuleFiles = hashSetOf<String>()

        val modules = changedFiles.mapNotNull { file ->
            projectGraph.findContainingProject(file)
                .also { project ->
                    if (project == null)
                        nonModuleFiles += file
                }
        }.toSet()

        return modules to nonModuleFiles
    }
}
