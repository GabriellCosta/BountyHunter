package dev.tigrao.bountyhunter.tracker

import org.gradle.api.Project
import java.io.File

internal class WriterToFile(
    private val project: Project,
    private val task: Collection<String>,
    private val file: File = File("${project.buildDir.absolutePath}/tasks_to_run")
) {

    fun writeToFile(collection: Collection<Project>) {
        if (file.exists())
            file.delete()

        file.createNewFile()

        if (collection.contains(project.rootProject))
            file.appendText(task.first())
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
