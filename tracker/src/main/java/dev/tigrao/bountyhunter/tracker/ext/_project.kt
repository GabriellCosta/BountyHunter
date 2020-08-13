package dev.tigrao.bountyhunter.tracker.ext

import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency

internal val Project.dependentModules: List<Project>
    get() = project.allprojects.filter { modules ->
        modules.hasDependencyModule(this)
    }

internal fun Project.hasDependencyModule(module: Project) =
    configurations
        .flatMap { it.dependencies }
        .filterIsInstance<DefaultProjectDependency>()
        .any { it.name == module.name }

internal fun Project.getTasksFromProjects(
    projects: Collection<Project>,
    tasks: Collection<String>
): Collection<String> {
    val tasksResolved = if (projects.contains(rootProject))
        tasks.flatMap { currentTask ->
            rootProject.getTasksByName(currentTask, true)
                .map { it.path }
        }
    else projects.flatMap { collectionItem ->
        tasks.mapNotNull { currentTask ->
            collectionItem.getTasksByName(currentTask, false)
                .firstOrNull()?.path
        }
    }
    return tasksResolved.toSet()
}