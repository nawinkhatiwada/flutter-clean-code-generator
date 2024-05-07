package com.androidbolts.fluttergenerator.fccg

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import java.awt.*
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

//class MyInputDialog : DialogWrapper(true) {
//    private val textField = JTextField(20)
//
//    init {
//        init()
//        title = "Enter Feature Name"
//    }
//
//    override fun createCenterPanel(): JComponent? {
//        val panel = JPanel(GridBagLayout())
//        val gbc = GridBagConstraints()
//        gbc.gridx = 0
//        gbc.gridy = 0
//        gbc.anchor = GridBagConstraints.WEST
//        gbc.insets = Insets(5, 5, 5, 5)
//        panel.add(JLabel("Feature Name:"), gbc)
//        gbc.gridx = 1
//        gbc.fill = GridBagConstraints.HORIZONTAL
//        panel.add(textField, gbc)
//        return panel
//    }
//
//    override fun getPreferredSize(): Dimension {
//        // Set a custom preferred size for the dialog
//        return Dimension(300, 100)
//    }
//
//    override fun doOKAction() {
//        if (textField.text.isBlank()) {
//            Messages.showErrorDialog("Feature name cannot be empty.", "Error")
//            return
//        }
//        super.doOKAction()
//    }
//}

class MyInputDialog : DialogWrapper(null) {
    private val textField = JTextField(20)
    var featureName = ""

    init {
        init()
        title = "Enter Feature Name"
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout())
        panel.add(JLabel("Feature Name:"), BorderLayout.WEST)
        panel.add(textField, BorderLayout.CENTER)
        return panel
    }

    override fun getPreferredSize(): Dimension {
        return Dimension(300, 100)
    }

    override fun doOKAction() {
        featureName = textField.text.trim()
        if (featureName.isEmpty()) {
            Messages.showErrorDialog("Feature name cannot be empty.", "Error")
            return
        }
        super.doOKAction()
    }
}