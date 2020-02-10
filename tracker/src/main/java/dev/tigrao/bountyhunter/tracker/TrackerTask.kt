package dev.tigrao.bountyhunter.tracker

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.language.base.plugins.LifecycleBasePlugin
import javax.inject.Inject

open class TrackerTask @Inject constructor(private val trackerExtension: TrackerExtension) :
    DefaultTask() {

    @Option(option = "task", description = "Task to run in modules")
    var task: List<String> = mutableListOf()

    private val gitClient: GitClient by lazy {
        GitClientImpl(project.projectDir, defaultBranch = trackerExtension.defaultBranch)
    }

    private val affectedModules by lazy {
        AffectedModules(project, gitClient)
    }

    private val writerToFile by lazy {
        WriterToFile(project, task)
    }

    init {
        description = "Create file with modules to run your tasks"
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    @TaskAction
    fun sayHello() {
        val collection = getProjectsToRun()

        writerToFile.writeToFile(collection)
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
}
