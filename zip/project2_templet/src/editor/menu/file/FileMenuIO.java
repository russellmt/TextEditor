package editor.menu.file;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.*;

public class FileMenuIO {
	
	public static String readFile(File file) {
		String content = "Error reading file.";
		try {
			Scanner reader = new Scanner(file);
			reader.useDelimiter("\\Z");
			content = reader.next();
		} catch (FileNotFoundException e) {}
		return content;
	}

	public static void writeFile(String text, File file, Window window, boolean safeSave) {
		if (!safeSave || !isWriteCanceled(file, window)) {
			try {
				System.out.println(file.getAbsolutePath());
				PrintWriter writer = new PrintWriter(file.getAbsolutePath());
				writer.println(text);
				writer.close();
			} catch (IOException e) {
				showSaveErrorDialog(file.getName(), window);
			}
		}
	}

	public static StyledDocument deserializeDocument(File file, Window window) {
		StyledDocument doc = null;
		try {
			FileInputStream fileStream = new FileInputStream(file.getAbsolutePath());
			ObjectInputStream objStream = new ObjectInputStream(fileStream);
			doc = (StyledDocument) objStream.readObject();
			objStream.close();
			fileStream.close();
		} catch (IOException | ClassNotFoundException e) {
			showOpenErrorDialog(file.getAbsolutePath(), window);
		}
		return doc;
	}

	public static void serializeDocument(StyledDocument doc, String savePath, Window window, boolean safeSave) {
		if (!safeSave || !isWriteCanceled(new File(savePath), window)) {

			// the undo manager must be removed from document before it can be serialized
			UndoableEditListener undoManager = removeUndoManager(doc);
			try {
				FileOutputStream fileStream = new FileOutputStream(savePath);
				ObjectOutputStream objStream = new ObjectOutputStream(fileStream);
				objStream.writeObject(doc);
				objStream.close();
				fileStream.close();
			} catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
				showSaveErrorDialog(savePath, window);
			}
			restoreUndoManager(doc, undoManager);
		}
	}

	private static UndoableEditListener removeUndoManager(StyledDocument doc) {
		UndoableEditListener undoManager = null;
		UndoableEditListener[] undoManagerArray = ((DefaultStyledDocument) doc).getUndoableEditListeners();
		if (undoManagerArray.length > 0) {
			undoManager = undoManagerArray[0];
			doc.removeUndoableEditListener(undoManager);
		}
		return undoManager;
	}

	private static void restoreUndoManager(StyledDocument doc, UndoableEditListener undoManager) {
		if (undoManager != null) {
			doc.addUndoableEditListener(undoManager);
		}
	}

	private static boolean isWriteCanceled(File file, Window window) {
		boolean canceled = false;
		if (file.exists()) {
			int option = showOverwriteDialog(file.getName(), window);
			if (option != JOptionPane.YES_OPTION) {
				canceled = true;
			}
		}
		return canceled;
	}

	private static int showOverwriteDialog(String fileName, Window window) {
		return JOptionPane.showConfirmDialog(window, "Are you sure you would like to overwrite " + fileName + "?",
			"Warning", JOptionPane.WARNING_MESSAGE);
	}

	private static void showSaveErrorDialog(String fileName, Window window) {
		showErrorDialog("Error saving " + fileName, window);
	}

	private static void showOpenErrorDialog(String fileName, Window window) {
		showErrorDialog("Error opening " + fileName, window);
	}

	private static void showErrorDialog(String fileName, Window window) {
		JOptionPane.showMessageDialog(window, "Error saving " + fileName, "Error", JOptionPane.ERROR_MESSAGE);
	}
}