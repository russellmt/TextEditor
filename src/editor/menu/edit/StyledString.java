package editor.menu.edit;

import java.util.*;

public class StyledString {

	private List<CharacterRun> runs;
	
	public StyledString() {
		this.runs = new ArrayList<>();
	}

	public StyledString(List<CharacterRun> runs) {
		this.runs = runs;
	}

	public void addRun(CharacterRun run) {
		runs.add(run);
	}

	public List<CharacterRun> getRuns() {
		return runs;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(CharacterRun run : runs) {
			builder.append(run.getText());
		}
		return builder.toString();
	}
}