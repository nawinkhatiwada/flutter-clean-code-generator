package com.androidbolts.fluttergenerator.fccg

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.projectRoots.impl.jdkDownloader.RuntimeChooserMessages.showErrorMessage
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import org.yaml.snakeyaml.Yaml
import java.io.InputStreamReader

class GenerateFolderStructureAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val dialog = MyInputDialog()
        dialog.show()
        if (dialog.isOK) {
            val featureName = dialog.featureName
            val selectedDir = getSelectedDirectory(event)
            val project = event.project

            if (project != null) {
                ApplicationManager.getApplication().runWriteAction {
                    if (selectedDir != null) {
                        val projectDir = project.guessProjectDir()
                        if (projectDir == null) {
                            showErrorMessage("No directory selected.")
                        } else {
                            val libDir = findOrCreateChildDir(projectDir, "lib")
                            if (libDir != null) {
                                val featuresDir = findDir(libDir, "features")
                                if (featuresDir == null) {
                                    val createdFeatureDir = createDir(libDir, "features")
                                    generateFolderStructure(createdFeatureDir, featureName, project)
                                } else {
                                    if (isSelectedDirInsideFeatureDir(selectedDir)) {
                                        generateFolderStructure(selectedDir, featureName, project)
                                    } else {
                                        showErrorMessage("Selected directory must be inside features directory.")
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                showErrorMessage("Project context is not available.")
            }
        }
    }

    private fun isSelectedDirInsideFeatureDir(dir: VirtualFile?): Boolean {
        var currentDir = dir
        while (currentDir != null) {
            val featuresDir = currentDir.findChild("features")
            if (featuresDir != null) {
                return true
            }
            currentDir = currentDir.parent
        }
        return false
    }

    private fun findDir(parentDir: VirtualFile?, dirName: String): VirtualFile? {
        return parentDir?.findChild(dirName)
    }

    private fun createDir(parentDir: VirtualFile, dirName: String): VirtualFile {
        return parentDir.createChildDirectory(null, dirName)
    }

    private fun findOrCreateChildDir(parentDir: VirtualFile, childName: String): VirtualFile? {
        return parentDir.findChild(childName) ?: parentDir.createChildDirectory(null, childName)
    }

    private fun getSelectedDirectory(event: AnActionEvent): VirtualFile? {
        val selectedFiles = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE_ARRAY)
        return selectedFiles?.firstOrNull()
    }

    // new implementation
    private fun generateFolderStructure(baseDir: VirtualFile, featureName: String, project: Project) {
        val featureDir = baseDir.createChildDirectory(null, featureName)
        val dataDir = featureDir.createChildDirectory(null, "data")
        dataDir.createChildDirectory(null, "model")

        val packageName = getPackageName(project) ?: return

        dataDir.createChildData(null, "${featureName}_repository.dart")
            .setBinaryContent("abstract class ${featureName.toCamelCase()}Repository {}".toByteArray())
        dataDir.createChildData(null, "${featureName}_repository_impl.dart")
            .setBinaryContent(getRepositoryImplContent(packageName, featureName).toByteArray())

        val localDir = dataDir.createChildDirectory(null, "local")
        localDir.createChildData(null, "${featureName}_local.dart")
            .setBinaryContent("abstract class ${featureName.toCamelCase()}Local {}".toByteArray())

        localDir.createChildData(null, "${featureName}_local_impl.dart")
            .setBinaryContent(getLocalContent(packageName, featureName).toByteArray())

        val remoteDir = dataDir.createChildDirectory(null, "remote")
        remoteDir.createChildData(null, "${featureName}_remote.dart")
            .setBinaryContent("abstract class ${featureName.toCamelCase()}Remote {}".toByteArray())

        remoteDir.createChildData(null, "${featureName}_remote_impl.dart")
            .setBinaryContent(getRemoteContent(packageName, featureName).toByteArray())

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

        showToastMessage("Generated Successfully!")
    }

    private fun String.toCamelCase(): String {
        return this.split("_").joinToString("") { it.capitalize() }
    }

    private fun showToastMessage(message: String) {
        ApplicationManager.getApplication().invokeLater {
            Messages.showMessageDialog(message, "Success", Messages.getInformationIcon())
        }
    }

    private fun getPackageName(project: Project?): String? {
        val pubspecFile = project?.guessProjectDir()?.findChild("pubspec.yaml") ?: return null
        val psiFile = PsiManager.getInstance(project).findFile(pubspecFile) ?: return null
        val virtualFile = psiFile.virtualFile ?: return null

        val yaml = Yaml()
        val inputStream = virtualFile.inputStream
        val yamlObject = yaml.load<Map<*, *>>(InputStreamReader(inputStream))

        return yamlObject?.get("name") as? String
    }

    private fun getRepositoryImplContent(packageName: String, featureName: String): String {
        val repositoryContent = """
            |import 'package:$packageName/features/$featureName/data/${featureName}_repository.dart';
            |
            |class ${featureName.toCamelCase()}RepositoryImpl implements ${featureName.toCamelCase()}Repository {}
        """.trimMargin()
        return repositoryContent
    }

    private fun getLocalContent(packageName: String, featureName: String): String {
        val repositoryContent = """
            |import 'package:$packageName/features/$featureName/data/local/${featureName}_local.dart';
            |
            |class ${featureName.toCamelCase()}LocalImpl implements ${featureName.toCamelCase()}Local {}
        """.trimMargin()
        return repositoryContent
    }

    private fun getRemoteContent(packageName: String, featureName: String): String {
        val repositoryContent = """
            |import 'package:$packageName/features/$featureName/data/remote/${featureName}_remote.dart';
            |
            |class ${featureName.toCamelCase()}RemoteImpl implements ${featureName.toCamelCase()}Remote {}
        """.trimMargin()
        return repositoryContent
    }
}