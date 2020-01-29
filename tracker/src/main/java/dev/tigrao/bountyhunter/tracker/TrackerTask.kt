package dev.tigrao.bountyhunter.tracker

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

open class TrackerTask : DefaultTask() {

    private val gitClient : GitClient by lazy {
        GitClientImpl(project.projectDir)
    }

    @TaskAction
    fun sayHello() {
        val collection = sortedSetOf<Project>()

        project.allprojects.forEach { all ->
            println("Module ${all.name}")
            val items = all
                .configurations
                .flatMap { it.dependencies }
                .filterIsInstance<DefaultProjectDependency>()
                //.filter { it.name == targetModule }
                .toSet()

            println(items.map { it.name })
            if (items.isNotEmpty()) {
                collection.add(all)
            }
        }

        val file = File("${project.buildDir.absolutePath}/tasks_to_run")

        if (file.exists())
            file.delete()

        collection.forEach {
            file.appendText(it.path)
            file.appendText("\n")
        }

        file.createNewFile()
    }
}
