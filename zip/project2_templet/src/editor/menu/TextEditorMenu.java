package editor.menu;

import java.awt.*;
import javax.swing.*;

public abstract class TextEditorMenu extends JMenu {
	
	protected TextEditorMenuBar parentBar;
	protected Window window;

	public TextEditorMenu(String title, TextEditorMenuBar parentBar, Window window) {
		super(title);
		this.parentBar = parentBar;
		this.window = window;

		initializeFields();
		build();
	}

	protected abstract void build();

	protected void initializeFields() {}

	public TextEditorMenuBar getParentBar() {
		return parentBar;
	}
}