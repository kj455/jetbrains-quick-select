package kj455.jetbrainsquickselect

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor

/**
 * Action to select text inside parentheses ()
 */
class SelectParenthesisAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        selectParenthesis(editor, false)
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabled = editor != null
    }

    private fun selectParenthesis(editor: Editor, outer: Boolean) {
        val selector = BracketSelector(editor)
        selector.selectBetweenBrackets('(', ')', outer)
    }
}
