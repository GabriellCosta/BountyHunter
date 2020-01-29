package dev.tigrao.bountyhunter.tracker

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

open class TrackerTask : DefaultTask() {

    @Option(option = "task", description = "Task to run in modules")
    var task: String = ""

    private val gitClient: GitClient by lazy {
        GitClientImpl(project.projectDir)
    }

    private val projectGraph: ProjectGraph by lazy {
        ProjectGraph(project.rootProject)
    }

    @TaskAction
    fun sayHello() {
        val collection = getProjectsToRun()

        writeToFile(collection)
    }

    private fun getProjectsToRun(): Collection<Project> {
        val collection = sortedSetOf<Project>()

        val files = gitClient.findChangesFromPrincipalBranch()
        val projectsToEval = mutableSetOf<Project>()
        files.forEach {
            val currentProject = projectGraph.findContainingProject(it)

            if (currentProject != null)
                projectsToEval.add(currentProject)
            else {
                collection += project.rootProject

                return collection
            }
        }

        collection += projectsToEval

        projectsToEval.forEach { file ->
            project.allprojects.forEach { all ->
                println("Module ${all.name}")
                val items = all
                    .configurations
                    .flatMap { it.dependencies }
                    .filterIsInstance<DefaultProjectDependency>()
                    .filter { it.name == file.name }
                    .toSet()

                println(items.map { it.name })
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
            file.appendText(task)
        else
            collection.forEach {
                file.appendText("${it.path}:$task")
                file.appendText("\n")
            }
    }
}
