package dev.tigrao.bountyhunter.tracker

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.language.base.plugins.LifecycleBasePlugin
import java.io.File

open class TrackerTask : DefaultTask() {

    @Option(option = "task", description = "Task to run in modules")
    var task: List<String> = mutableListOf()

    private val affectedModules = AffectedModules(project)

    init {
        description = "Create file with modules to run your tasks"
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    @TaskAction
    fun sayHello() {
        val collection = getProjectsToRun()

        writeToFile(collection)
    }

    private fun getProjectsToRun(): Collection<Project> {
        val collection = sortedSetOf<Project>()

        val projectsToEval = affectedModules.getAffectedModules()

        if (projectsToEval.isEmpty())
            return setOf(project.rootProject)

        collection += projectsToEval

        projectsToEval.forEach { file ->
            project.allprojects.forEach { all ->
                val items = all
                    .configurations
                    .flatMap { it.dependencies }
                    .filterIsInstance<DefaultProjectDependency>()
                    .filter { it.name == file.name }
                    .toSet()

                if (items.isNotEmpty()) {
                    collection.add(all)
                }
            }
        }

        return collection
    }

    private fun writeToFile(collection: Collection<Project>) {
        val file = File("${project.buildDir.absolutePath}/tasks_to_run")

        if (file.exists())
            file.delete()

        file.createNewFile()

        if (collection.contains(project.rootProject))
            file.appendText(task.toString())
        else
            collection.forEach { collectionItem ->
                task.forEach { currentTask ->
                    collectionItem.getTasksByName(currentTask, false).firstOrNull()
                        ?.let { itemTasks ->
                            file.appendText(itemTasks.path)
                            file.appendText("\n")
                        }
                }
            }
    }
}
