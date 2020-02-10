package dev.tigrao.bountyhunter.tracker

import org.gradle.api.Project

internal class AffectedModules(
    private val project: Project,
    private val gitClient: GitClient
) {

    private val projectGraph: ProjectGraph by lazy {
        ProjectGraph(project.rootProject)
    }

    fun getAffectedModules(): Set<Project> {

        val files = gitClient.findChangesFromPrincipalBranch()
        val projectsToEval = mutableSetOf<Project>()
        files.forEach {
            val currentProject = projectGraph.findContainingProject(it)

            if (currentProject != null)
                projectsToEval.add(currentProject)
            else {
                return emptySet()
            }
        }

        return projectsToEval
    }
}
