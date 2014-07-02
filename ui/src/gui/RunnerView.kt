package org.funql.ri.gui

import java.io.File

public trait RunnerView {
    fun askForSave(): UserAnswer;
    fun setQueryText(text: String);
    fun setResultText(text: String);
    fun getQueryText(): String
    fun getResultText(): String
    fun showSaveDialog(title: String): File?
    fun showOpenDialog(title: String): File?
    fun error(text: String);
    fun setTitle(text: String);
}