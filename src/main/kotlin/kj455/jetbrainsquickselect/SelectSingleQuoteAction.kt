package kj455.jetbrainsquickselect

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor

/**
 * Action to select text inside single quotes ''
 */
class SelectSingleQuoteAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        selectQuote(editor, '\'')
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabled = editor != null
    }

    private fun selectQuote(editor: Editor, char: Char) {
        val selector = BracketSelector(editor)
        selector.selectBetweenQuotes(char, false, false)
    }
}
