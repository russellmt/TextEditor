package editor.menu.file;

import editor.*;
import editor.menu.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.WindowEvent;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

public class FileMenu extends TextEditorMenu {

	public static final String SERIALIZED_EXTENSION = ".sst";

	private String destinationName;
	private boolean documentEdited;
	private JFileChooser fileChooser;
	private JMenuItem saveItem;
	private JMenuItem saveAsItem;
	private KeyStroke saveStroke;

	public FileMenu(TextEditorMenuBar parentBar, Window window) {
		super(MenuConstants.FILE, parentBar, window);
	}

	@Override
	protected void initializeFields() {
		this.destinationName = "";
		this.documentEdited = false;
		fileChooser = new JFileChooser();
	}

	@Override
	protected void build() {
		ActionListener fileListener = createFileListener();
		
		JMenuItem miNew = new JMenuItem(MenuConstants.NEW);
		JMenuItem miOpen = new JMenuItem(MenuConstants.OPEN);
		saveItem = new JMenuItem(MenuConstants.SAVE);	//this is an instance variable
		saveAsItem = new JMenuItem(MenuConstants.SAVE_AS);	//this is an instance variable
		JMenuItem miQuit = new JMenuItem(MenuConstants.QUIT);

		miNew.addActionListener(fileListener);
		miOpen.addActionListener(fileListener);
		saveItem.addActionListener(fileListener);
		saveAsItem.addActionListener(fileListener);
		miQuit.addActionListener(fileListener);

		KeyStroke newStroke = KeyStroke.getKeyStroke(MenuConstants.KEY_N, MenuConstants.KEY_CTRL);
		KeyStroke openStroke = KeyStroke.getKeyStroke(MenuConstants.KEY_O, MenuConstants.KEY_CTRL);
		saveStroke = KeyStroke.getKeyStroke(MenuConstants.KEY_S, MenuConstants.KEY_CTRL);
		KeyStroke quitStroke = KeyStroke.getKeyStroke(MenuConstants.KEY_Q, MenuConstants.KEY_CTRL);

		miNew.setAccelerator(newStroke);
		miOpen.setAccelerator(openStroke);
		saveAsItem.setAccelerator(saveStroke);
		miQuit.setAccelerator(quitStroke);

		saveItem.setVisible(false);

		add(miNew);
		add(miOpen);
		add(saveItem);
		add(saveAsItem);
		add(miQuit);

		setMnemonic(MenuConstants.KEY_F);

		parentBar.getEditor().getStyledDocument().addDocumentListener(createDocumentListener());
	}

	private ActionListener createFileListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				switch (command) {
					case MenuConstants.NEW:
						handleNewFile();
						break;
					case MenuConstants.OPEN:
						handleOpenFile();
						break;
					case MenuConstants.SAVE:
						handleSaveFile();
						break;
					case MenuConstants.SAVE_AS:
						handleSaveFileAs();
						break;
					case MenuConstants.QUIT:
						handleQuit();
						break;
				}
			}
		};
	}

	private DocumentListener createDocumentListener() {
		return new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				FileMenu.this.setDocumentEdited(true);
			}
			public void removeUpdate(DocumentEvent e) {
				FileMenu.this.setDocumentEdited(true);
			}
			public void changedUpdate(DocumentEvent e) {
				if (destinationName.endsWith(SERIALIZED_EXTENSION)) {
					FileMenu.this.setDocumentEdited(true);
				}
			}
		};
	}

	private void initializeDocument(StyledDocument doc, TextEditorPane editor) {
		doc.addDocumentListener(createDocumentListener());
		editor.setStyledDocument(doc);
		editor.resetUndoManager();
	}

	public void performDefaultSave() {
		if (destinationName.isEmpty()) {
			handleSaveFileAs();
		} else {
			handleSaveFile();
		}
	}

	public void handleNewFile() {
		checkToSaveChanges();
		documentEdited = false;
		destinationName = "";

		TextEditorPane editor = parentBar.getEditor();
		StyledDocument doc = new DefaultStyledDocument();
		initializeDocument(doc, editor);
		editor.setCharacterAttributes(new SimpleAttributeSet(), true);
		deactivateSaveItem();
	}

	public void handleOpenFile() {
		checkToSaveChanges();
		int selection = fileChooser.showOpenDialog(window);
		if (selection == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			TextEditorPane editor = parentBar.getEditor();
			StyledDocument doc;

			if (selectedFile.getName().endsWith(SERIALIZED_EXTENSION)) {
				doc = FileMenuIO.deserializeDocument(selectedFile, window);
				initializeDocument(doc, editor);
			} else {
				//prevents unstyled text from being read with the current editor styling
				editor.setCharacterAttributes(new SimpleAttributeSet(), true);
				doc = new DefaultStyledDocument();
				initializeDocument(doc, editor);
				editor.setText(FileMenuIO.readFile(selectedFile));
			}
			activateSaveItem(selectedFile.getAbsolutePath());
			documentEdited = false;
		}
	}

	private void handleSaveFile() {
		doSave(destinationName, false);
	}

	private void handleSaveFileAs() {
		int selection = fileChooser.showSaveDialog(window);
		if (selection == JFileChooser.APPROVE_OPTION) {
			String filePath = fileChooser.getSelectedFile().getAbsolutePath();
			doSave(filePath, true);
			activateSaveItem(filePath);
		}
	}

	private void doSave(String filePath, boolean safeSave) {
		if (filePath.endsWith(SERIALIZED_EXTENSION)) {
			FileMenuIO.serializeDocument(parentBar.getEditor().getStyledDocument(), filePath, window, safeSave);
		} else {
			FileMenuIO.writeFile(parentBar.getEditor().getText(), new File(filePath), window, safeSave);
		}
		documentEdited = false;
	}

	private void handleQuit() {
		checkToSaveChanges();
		window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
	}

	private void checkToSaveChanges() {
		if (documentEdited) {
			int selection = JOptionPane.showConfirmDialog(window, "Would you like to save changes?", "Warning", JOptionPane.WARNING_MESSAGE);
			if (selection == JOptionPane.YES_OPTION) {
				performDefaultSave();
			}
		}
	} 

	private void activateSaveItem(String destinationName) {
		if (!destinationName.isEmpty()) {
			this.destinationName = destinationName;
			swapSaveAccelerator(saveItem, saveAsItem);
			saveItem.setVisible(true);
		}
	}

	private void deactivateSaveItem() {
		swapSaveAccelerator(saveAsItem, saveItem);
		saveItem.setVisible(false);
	}

	private void swapSaveAccelerator (JMenuItem itemReceivesSave, JMenuItem itemReceivesNull) {
		itemReceivesSave.setAccelerator(saveStroke);
		itemReceivesNull.setAccelerator(null);
	}

	public String getDestinationName() {
		return destinationName;
	}

	public boolean isDocumentEdited() {
		return documentEdited;
	}

	public void setDocumentEdited(boolean documentEdited) {
		this.documentEdited = documentEdited;
	}
}