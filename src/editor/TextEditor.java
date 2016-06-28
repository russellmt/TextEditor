package editor;

import editor.menu.*;

import java.awt.event.*;
import javax.swing.*;

public class TextEditor {
	
	public static void main (String[] args) {
		final boolean isStayOpen = args.length > 0 && args[0].equals("-so");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				buildApp(isStayOpen);
			}
		});
	}

	private static void buildApp(boolean isStayOpen) {
		MenuConstants.initColorMap();

		JFrame frame = new JFrame();
		frame.addWindowListener(createWindowAdapter());
		frame.setSize(800, 800);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		TextEditorPane editor = new TextEditorPane();
		TextEditorMenuBar bar = new TextEditorMenuBar(editor, frame, isStayOpen);

		frame.setContentPane(new JScrollPane(editor));
		frame.setJMenuBar(bar);
		frame.setVisible(true);
	}

	private static WindowAdapter createWindowAdapter() {
		return new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//TODO: put code here to create JDialog and ask to save unsaved changes, if they exist
			}
		};
	}
}