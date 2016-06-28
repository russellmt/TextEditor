package editor.menu.edit;

public class EditRange {
	
	private int start, end;

	public EditRange(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int getLength() {
		return end - start;
	}
}