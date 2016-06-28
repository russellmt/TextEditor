package editor;

import editor.menu.MenuConstants;
import editor.menu.edit.CompoundUndoManager;

import javax.swing.*;

public class TextEditorPane extends JTextPane {

	private CompoundUndoManager undoManager;

	// these are references to EditMenu items in order to facilitate resetting undo manager upon changing documents
	private JMenuItem undoItem;
	private JMenuItem redoItem;

	public boolean hasSelection() {
		return getSelectionEnd() - getSelectionStart() > 0;
	}

	public boolean isEmpty() {
		return getDocument().getLength() <= 0;
	}

	public CompoundUndoManager getUndoManager() {
		return undoManager;
	}

	public void resetUndoManager() {
		undoManager = new CompoundUndoManager(this);
		if (undoItem != null) {
			undoItem.setAction(undoManager.getUndoAction());
			undoItem.setText(MenuConstants.UNDO);
		}
		if (undoItem != null) {
			redoItem.setAction(undoManager.getRedoAction());
			redoItem.setText(MenuConstants.REDO);
		}
	}

	public void setUndoItemReferences(JMenuItem undoItem, JMenuItem redoItem) {
		this.undoItem = undoItem;
		this.redoItem = redoItem;
	}
}