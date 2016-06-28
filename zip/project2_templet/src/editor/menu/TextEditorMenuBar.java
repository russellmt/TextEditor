package editor.menu;

import editor.*;
import editor.menu.edit.*;
import editor.menu.file.*;
import editor.menu.font.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TextEditorMenuBar extends JMenuBar {
	
	private TextEditorPane editor;
	private boolean stayOpen;

	public TextEditorMenuBar(TextEditorPane editor, Window window, boolean stayOpen) {
		this.editor = editor;
		this.stayOpen = stayOpen;
		build(window);
	}

	private void build(Window window) {
		FileMenu mFile = new FileMenu(this, window);
		EditMenu mEdit = new EditMenu(this, window);
		FontMenu mFont = new FontMenu(this, window);

		add(mFile);
		add(mEdit);
		add(mFont);
	}

	public TextEditorPane getEditor() {
		return editor;
	}

	public boolean isStayOpen() {
		return stayOpen;
	}
}