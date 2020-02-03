package dev.tigrao.bountyhunter.tracker

import org.gradle.api.Project
import java.io.File

internal class WriterToFile(
    private val project: Project,
    private val task: Collection<String>
) {

    fun writeToFile(collection: Collection<Project>) {
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
