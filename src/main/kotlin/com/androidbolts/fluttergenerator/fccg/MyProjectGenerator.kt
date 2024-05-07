package com.androidbolts.fluttergenerator.fccg

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.testFramework.utils.vfs.createFile


class GenerateFolderStructureAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
//        val featureName = Messages.showInputDialog("Enter Feature Name:", "Generate Folder Structure", null)
//        if (featureName.isNullOrBlank()) {
//            Messages.showErrorDialog("Feature name cannot be empty.", "Error")
//            return
//        }
//        generateFolderStructure(event.project, featureName)

        val dialog = MyInputDialog()
        dialog.show()
        ApplicationManager.getApplication().runWriteAction {
            if (dialog.isOK) {
                val featureName = dialog.featureName
                generateFolderStructure(event.project, featureName)
            }
        }

    }

    private fun generateFolderStructure(project: Project?, featureName: String) {
        if (project != null) {
            val baseDir = project.baseDir
            val featureDir = baseDir.createChildDirectory(null, featureName)
            val dataDir = featureDir.createChildDirectory(null, "data")
            dataDir.createChildDirectory(null, "model")

            dataDir.createChildData(null, "${featureName}_repository.dart")
                .setBinaryContent("abstract class ${featureName.toCamelCase()}Repository {}".toByteArray())
            dataDir.createChildData(null, "${featureName}_repository_impl.dart")
                .setBinaryContent("class ${featureName.toCamelCase()}RepositoryImpl implements ${featureName.toCamelCase()}Repository {}".toByteArray())

            val localDir = dataDir.createChildDirectory(null, "local")
            localDir.createChildData(null, "${featureName}_local.dart")
                .setBinaryContent("abstract class ${featureName.toCamelCase()}Local {}".toByteArray())

            localDir.createChildData(null, "${featureName}_local_impl.dart")
                .setBinaryContent("class ${featureName.toCamelCase()}LocalImpl implements ${featureName.toCamelCase()}Local {}".toByteArray())
            val remoteDir = dataDir.createChildDirectory(null, "remote")
            remoteDir.createChildData(null, "${featureName}_remote.dart")
                .setBinaryContent("abstract class ${featureName.toCamelCase()}Remote {}".toByteArray())

            remoteDir.createChildData(null, "${featureName}_remote_impl.dart")
                .setBinaryContent("class ${featureName.toCamelCase()}RemoteImpl implements ${featureName.toCamelCase()}Remote {}".toByteArray())

            val presentationDir = featureDir.createChildDirectory(null, "presentation")
            val providersDir = presentationDir.createChildDirectory(null, "providers")

            val stateDir = providersDir.createChildDirectory(null, "state")
            stateDir.createChildData(null, "${featureName}_notifier.dart")
                .setBinaryContent("class ${featureName.toCamelCase()}Notifier {}".toByteArray())

            stateDir.createChildData(null, "${featureName}_state.dart")
                .setBinaryContent("class ${featureName.toCamelCase()}State {}".toByteArray())

            presentationDir.createChildDirectory(null, "screens")
            presentationDir.createChildDirectory(null, "widgets")
            providersDir.createChildData(null, "${featureName}_state_provider.dart")
                .setBinaryContent("class ${featureName.toCamelCase()}StateProvider {}".toByteArray())


//            Messages.showInfoMessage("Folder structure generated successfully.", "Success")
        }
    }

    private fun String.toCamelCase(): String {
        return this.split("_").joinToString("") { it.capitalize() }
    }
}