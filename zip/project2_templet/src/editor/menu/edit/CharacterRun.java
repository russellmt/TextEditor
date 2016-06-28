package editor.menu.edit;

import javax.swing.text.*;

public class CharacterRun {
	
	private String text;
	private AttributeSet attributes;
	private EditRange range;

	public CharacterRun(String text, AttributeSet attributes, EditRange range) {
		this.text = text;
		this.attributes = attributes;
		this.range = range;
	}

	public String getText() {
		return text;
	}

	public AttributeSet getAttributes() {
		return attributes;
	}

	public EditRange getRange() {
		return range;
	}
}