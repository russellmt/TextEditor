package editor.menu.font;

import editor.*;
import editor.menu.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

public class FontMenu extends TextEditorMenu {

	private Map<String, JRadioButtonMenuItem> familyItemMap;
	private Map<String, JRadioButtonMenuItem> sizeItemMap;
	private Map<String, JRadioButtonMenuItem> colorItemMap;

	private JCheckBoxMenuItem boldItem;
	private JCheckBoxMenuItem italicItem;
	private JCheckBoxMenuItem underlineItem;

	private String selectedFamily;
	private String selectedSize;
	private String selectedColor;
	
	public FontMenu(TextEditorMenuBar parentBar, Window window) {
		super(MenuConstants.FONT, parentBar, window);
	}

	@Override
	protected void initializeFields() {
		familyItemMap = new HashMap<>();
		sizeItemMap = new HashMap<>();
		colorItemMap = new HashMap<>();
	}

	@Override
	protected void build() {
		resetEditorFont();
		parentBar.getEditor().addCaretListener(createCaretListener());

		ActionListener fontStyleListener = createFontStyleListener();

		KeyStroke boldStroke = KeyStroke.getKeyStroke(MenuConstants.KEY_B, MenuConstants.KEY_CTRL);
		KeyStroke italicStroke = KeyStroke.getKeyStroke(MenuConstants.KEY_I, MenuConstants.KEY_CTRL);
		KeyStroke underlineStroke = KeyStroke.getKeyStroke(MenuConstants.KEY_U, MenuConstants.KEY_CTRL);

		JMenu mFamily = new JMenu(MenuConstants.FAMILY);
		JMenu mSize = new JMenu(MenuConstants.SIZE);
		JMenu mStyle = new JMenu(MenuConstants.STYLE);
		JMenu mColor = new JMenu(MenuConstants.COLOR);

		mFamily.setMnemonic(MenuConstants.KEY_F);
		mSize.setMnemonic(MenuConstants.KEY_S);
		mStyle.setMnemonic(MenuConstants.KEY_Y);
		mColor.setMnemonic(MenuConstants.KEY_C);

		constructFamilyItems(mFamily);
		constructSizeItems(mSize);
		constructColorItems(mColor);

		boldItem = getCheckBoxItem(MenuConstants.BOLD);
		italicItem = getCheckBoxItem(MenuConstants.ITALIC);
		underlineItem = getCheckBoxItem(MenuConstants.UNDERLINE);

		boldItem.setAccelerator(boldStroke);
		italicItem.setAccelerator(italicStroke);
		underlineItem.setAccelerator(underlineStroke);

		boldItem.addActionListener(fontStyleListener);
		italicItem.addActionListener(fontStyleListener);
		underlineItem.addActionListener(fontStyleListener);

		mStyle.add(boldItem);
		mStyle.add(italicItem);
		mStyle.add(underlineItem);

		add(mFamily);
		add(mSize);
		add(mStyle);
		add(mColor);

		setMnemonic(MenuConstants.KEY_T);
	}

	private void resetEditorFont() {
		this.selectedFamily = MenuConstants.DEFAULT_FAMILY;
		this.selectedSize = MenuConstants.DEFAULT_SIZE;
		this.selectedColor = MenuConstants.DEFAULT_COLOR;

		handleSetFamily();
		handleSetSize();
		// handleSetColor();
	}

	private void constructFamilyItems(JMenu mFamily) {
		ActionListener fontFamilyListener = createFontFamilyListener();
		ButtonGroup familyGroup = new ButtonGroup();
		JRadioButtonMenuItem miFamily;

		for (String family : MenuConstants.FONT_FAMILIES) {
			miFamily = new JRadioButtonMenuItem(family);
			miFamily.addActionListener(fontFamilyListener);

			mFamily.add(miFamily);
			familyItemMap.put(family, miFamily);
			familyGroup.add(miFamily);

			if (selectedFamily.equals(family)) {
				miFamily.setSelected(true);
			}
		}
	}

	private void constructSizeItems(JMenu mSize) {
		ActionListener fontSizeListener = createFontSizeListener();
		ButtonGroup sizeGroup = new ButtonGroup();
		JRadioButtonMenuItem miSize;

		for (String size : MenuConstants.FONT_SIZES) {
			miSize = new JRadioButtonMenuItem(size + " " + MenuConstants.POINT);
			miSize.setActionCommand(size);
			miSize.addActionListener(fontSizeListener);

			mSize.add(miSize);
			sizeItemMap.put(size, miSize);
			sizeGroup.add(miSize);

			if (selectedSize.equals(size)) {
				miSize.setSelected(true);
			}
		}
	}

	private void constructColorItems(JMenu mColor) {
		ActionListener fontColorListener = createFontColorListener();
		ButtonGroup colorGroup = new ButtonGroup();
		JRadioButtonMenuItem miColor;

		for (String colorText : MenuConstants.FONT_COLORS) {
			miColor = new JRadioButtonMenuItem(colorText);
			miColor.addActionListener(fontColorListener);

			mColor.add(miColor);
			colorItemMap.put(colorText, miColor);
			colorGroup.add(miColor);

			if (selectedColor.equals(colorText)) {
				miColor.setSelected(true);
			}
		}
	}

	private JCheckBoxMenuItem getCheckBoxItem(String command) {
		JCheckBoxMenuItem item;
		if (parentBar.isStayOpen()) {
			item = new StayOpenCheckBoxMenuItem(command);
		} else {
			item = new JCheckBoxMenuItem(command);
		}
		return item;
	}

	// sets state of menu items based on caret position OR end location of selection based on highlight direction (for now)
	private CaretListener createCaretListener() {
		return new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				TextEditorPane editor = parentBar.getEditor();
				StyledDocument doc = editor.getStyledDocument();
				Element element = doc.getCharacterElement(editor.getCaretPosition());
				AttributeSet attributes = element.getAttributes();

				updateRadioItem(familyItemMap, attributes, StyleConstants.FontFamily, MenuConstants.DEFAULT_FAMILY);
				updateRadioItem(sizeItemMap, attributes, StyleConstants.FontSize, MenuConstants.DEFAULT_SIZE);
				updateColorItem(attributes);

				updateCheckItem(boldItem, attributes, StyleConstants.Bold);
				updateCheckItem(italicItem, attributes, StyleConstants.Italic);
				updateCheckItem(underlineItem, attributes, StyleConstants.Underline);
			}
		};
	}

	private void updateRadioItem(Map<String, JRadioButtonMenuItem> itemMap, AttributeSet attributes, Object style, String defaultKey) {
		if (attributes.isDefined(style)) {
			itemMap.get(String.valueOf(attributes.getAttribute(style))).setSelected(true);
		} else {
			itemMap.get(defaultKey).setSelected(true);
		}
	}

	//color is a special case: color is not a string and there is no color.getName() method :(
	private void updateColorItem(AttributeSet attributes) {
		if (attributes.isDefined(StyleConstants.Foreground)) {
			Color currentColor = (Color) attributes.getAttribute(StyleConstants.Foreground);
			Map<String, Color> colorMap = MenuConstants.COLOR_MAP;
			String colorText = "";
			for (String key : colorMap.keySet()) {
				if (colorMap.get(key).equals(currentColor)) {
					colorText = key;
					break;
				}
			}
			colorItemMap.get(colorText).setSelected(true);
		} else {
			colorItemMap.get(MenuConstants.DEFAULT_COLOR).setSelected(true);
		}
	}

	private void updateCheckItem(JCheckBoxMenuItem item, AttributeSet attributes, Object style) {
		if (attributes.isDefined(style)) {
			item.setState((Boolean) attributes.getAttribute(style));	
		} else {
			item.setState(false);
		}
	}

	private ActionListener createFontFamilyListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedFamily = e.getActionCommand();
				handleSetFamily();
			}
		};
	}

	private ActionListener createFontSizeListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedSize = e.getActionCommand();
				handleSetSize();
			}
		};
	}

	private ActionListener createFontColorListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedColor = e.getActionCommand();
				handleSetColor();
			}
		};
	}

	private ActionListener createFontStyleListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				switch (command) {
					case MenuConstants.BOLD:
						handleToggleBold();
						break;
					case MenuConstants.ITALIC:
						handleToggleItalic();
						break;
					case MenuConstants.UNDERLINE:
						handleToggleUnderline();
						break;
				}
			}
		};
	}

	public void handleSetFamily() {
		MutableAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attributes, selectedFamily);
		parentBar.getEditor().setCharacterAttributes(attributes, false);
	}

	public void handleSetSize() {
		MutableAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setFontSize(attributes, Integer.parseInt(selectedSize));
		parentBar.getEditor().setCharacterAttributes(attributes, false);
	}

	public void handleSetColor() {
		MutableAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setForeground(attributes, MenuConstants.COLOR_MAP.get(selectedColor));
		parentBar.getEditor().setCharacterAttributes(attributes, false);
	}

	public void handleToggleBold() {
		MutableAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setBold(attributes, boldItem.getState());
		parentBar.getEditor().setCharacterAttributes(attributes, false);
	}

	public void handleToggleItalic() {
		MutableAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setItalic(attributes, italicItem.getState());
		parentBar.getEditor().setCharacterAttributes(attributes, false);
	}

	public void handleToggleUnderline() {
		MutableAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setUnderline(attributes, underlineItem.getState());
		parentBar.getEditor().setCharacterAttributes(attributes, false);
	}
}
