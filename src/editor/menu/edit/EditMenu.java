package editor.menu.edit;

import editor.*;
import editor.menu.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.text.*;

public class EditMenu extends TextEditorMenu {

	private StyledString clipboardContents;
	private FindReplaceDialog findReplaceDialog;

	private JMenuItem undoItem;
	private JMenuItem redoItem;

	public EditMenu(TextEditorMenuBar parentBar, Window window) {
		super(MenuConstants.EDIT, parentBar, window);
	}

	@Override
	protected void initializeFields() {
		findReplaceDialog = new FindReplaceDialog(parentBar.getEditor());
	}

	@Override
	protected void build() {
		TextEditorPane editor = parentBar.getEditor();
		InputMap inputMap = editor.getInputMap();

		KeyStroke cutStroke = KeyStroke.getKeyStroke(MenuConstants.KEY_X, MenuConstants.KEY_CTRL);
		KeyStroke copyStroke = KeyStroke.getKeyStroke(MenuConstants.KEY_C, MenuConstants.KEY_CTRL);
		KeyStroke pasteStroke = KeyStroke.getKeyStroke(MenuConstants.KEY_V, MenuConstants.KEY_CTRL);
		KeyStroke undoStroke = KeyStroke.getKeyStroke(MenuConstants.KEY_Z, MenuConstants.KEY_CTRL);
		KeyStroke redoStroke = KeyStroke.getKeyStroke(MenuConstants.KEY_Y, MenuConstants.KEY_CTRL);
		KeyStroke selectAllStroke = KeyStroke.getKeyStroke(MenuConstants.KEY_A, MenuConstants.KEY_CTRL);
		KeyStroke findReplaceStroke = KeyStroke.getKeyStroke(MenuConstants.KEY_F, MenuConstants.KEY_CTRL);

		ActionListener editListener = getUnimplementedActionsAsListener();

		JMenuItem miCut = new JMenuItem();
		JMenuItem miCopy = new JMenuItem();
		JMenuItem miPaste = new JMenuItem();
		undoItem = new JMenuItem();
		redoItem = new JMenuItem();
		JMenuItem miSelectAll = new JMenuItem(MenuConstants.SELECT_ALL);
		JMenuItem miFindReplace = new JMenuItem(MenuConstants.FIND_REPLACE);

		CustomCutAction cutAction = new CustomCutAction();
		CustomCopyAction copyAction = new CustomCopyAction();
		CustomPasteAction pasteAction = new CustomPasteAction();

		miCut.setAction(cutAction);
		miCopy.setAction(copyAction);
		miPaste.setAction(pasteAction);

		miSelectAll.addActionListener(editListener);
		miFindReplace.addActionListener(editListener);

		//because setting an action overwrites the text
		miCut.setText(MenuConstants.CUT);
		miCopy.setText(MenuConstants.COPY);
		miPaste.setText(MenuConstants.PASTE);

		miCut.setAccelerator(cutStroke);
		miCopy.setAccelerator(copyStroke);
		miPaste.setAccelerator(pasteStroke);
		undoItem.setAccelerator(undoStroke);
		redoItem.setAccelerator(redoStroke);
		miSelectAll.setAccelerator(selectAllStroke);
		miFindReplace.setAccelerator(findReplaceStroke);

		inputMap.put(cutStroke, cutAction);
		inputMap.put(copyStroke, copyAction);
		inputMap.put(pasteStroke, pasteAction);
		inputMap.put(selectAllStroke, editListener);

		editor.setUndoItemReferences(undoItem, redoItem);
		editor.resetUndoManager();

		add(miCut);
		add(miCopy);
		add(miPaste);
		add(undoItem);
		add(redoItem);
		add(miSelectAll);
		add(miFindReplace);

		setMnemonic(MenuConstants.KEY_E);
	}

	class CustomCutAction extends DefaultEditorKit.CutAction {
		public void actionPerformed(ActionEvent e) {
			handleCut();
		}
	}

	class CustomCopyAction extends DefaultEditorKit.CopyAction {
		public void actionPerformed(ActionEvent e) {
			handleCopy();
		}
	}

	class CustomPasteAction extends DefaultEditorKit.PasteAction {
		public void actionPerformed(ActionEvent e) {
			handlePaste();
		}
	}

    private ActionListener getUnimplementedActionsAsListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				switch (command) {
					case MenuConstants.SELECT_ALL:
						handleSelectAll();
						break;
					case MenuConstants.FIND_REPLACE:
						handleFindReplace();
						break;
				}
			}
		};
    }

	public void handleCut() {
		TextEditorPane editor = parentBar.getEditor();
		if (editor.hasSelection()) {
			copySelection();
			editor.replaceSelection("");
		}
	}

	public void handleCopy() {
		if (parentBar.getEditor().hasSelection()) {
			copySelection();
		}
	}

	public void handlePaste() {
		if (clipboardContents != null) {
			TextEditorPane editor = parentBar.getEditor();
			StyledDocument doc = editor.getStyledDocument();
			int pasteStart;

			if (editor.hasSelection()) {
				pasteStart = editor.getSelectionStart();
				editor.replaceSelection("");
			} else {
				pasteStart = editor.getCaretPosition();
			}

			List<CharacterRun> runs = clipboardContents.getRuns();
			int lengthSum = 0;
			try {
				for (CharacterRun run : runs) {
					doc.insertString(pasteStart + lengthSum, run.getText(), run.getAttributes());
					lengthSum += run.getRange().getLength();
				}
			} catch (BadLocationException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public void handleSelectAll() {
		TextEditorPane editor = parentBar.getEditor();
		editor.selectAll();
	}

	public void handleFindReplace() {
		findReplaceDialog.setVisible(true);
	}

	private void copySelection() {
		TextEditorPane editor = parentBar.getEditor();
		StyledDocument doc = editor.getStyledDocument();
		StyledString styledCopy = new StyledString();

		int selectionIndex = editor.getSelectionStart();
		int selectionEnd = editor.getSelectionEnd();

		try {
			while (selectionIndex < selectionEnd) {
				Element element = doc.getCharacterElement(selectionIndex);
				int endOffset = element.getEndOffset();
				int end = endOffset < selectionEnd ? endOffset : selectionEnd;
				int runLength = end - selectionIndex;

				String runText = editor.getText(selectionIndex, runLength);
				styledCopy.addRun(new CharacterRun(runText, element.getAttributes(), new EditRange(selectionIndex, end)));
				selectionIndex += runLength;
			}
		} catch (BadLocationException e) {
			System.out.println(e.getMessage());
		} 
		this.clipboardContents = styledCopy;
	}

	public StyledString getClipboardContents() {
		return clipboardContents;
	}

	public void setClipboardContents(StyledString clipboardContents) {
		this.clipboardContents = clipboardContents;
	}

}