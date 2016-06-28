package editor.menu;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;

public class MenuConstants {
	
	//file menu items
	public static final String FILE				= "File";
	public static final String NEW 				= "New";
	public static final String OPEN 			= "Open";
	public static final String SAVE 			= "Save";
	public static final String SAVE_AS 			= "Save As...";
	public static final String QUIT 			= "Quit";

	//edit menu items
	public static final String EDIT				= "Edit";
	public static final String CUT 				= "Cut"; 
	public static final String COPY				= "Copy";
	public static final String PASTE			= "Paste";
	public static final String UNDO				= "Undo";
	public static final String REDO				= "Redo";
	public static final String SELECT_ALL		= "Select All";
	public static final String FIND_REPLACE		= "Find/Replace";

	//font menu items
	public static final String FONT				= "Font";
	public static final String FAMILY			= "Family";
	public static final String SIZE				= "Size";
	public static final String STYLE			= "Style";
	public static final String POINT			= "Point";
	public static final String BOLD				= "Bold";
	public static final String ITALIC			= "Italic";
	public static final String UNDERLINE		= "Underline";
	public static final String COLOR			= "Color";

	//common keystrokes
	public static final int	KEY_A 				= KeyEvent.VK_A;
	public static final int	KEY_B 				= KeyEvent.VK_B;
	public static final int	KEY_C 				= KeyEvent.VK_C;
	public static final int	KEY_E 				= KeyEvent.VK_E;
	public static final int	KEY_F 				= KeyEvent.VK_F;
	public static final int	KEY_I 				= KeyEvent.VK_I;
	public static final int	KEY_N 				= KeyEvent.VK_N;
	public static final int	KEY_O 				= KeyEvent.VK_O;
	public static final int	KEY_Q 				= KeyEvent.VK_Q;
	public static final int	KEY_S 				= KeyEvent.VK_S;
	public static final int	KEY_T 				= KeyEvent.VK_T;
	public static final int	KEY_U 				= KeyEvent.VK_U;
	public static final int	KEY_V 				= KeyEvent.VK_V;
	public static final int	KEY_X 				= KeyEvent.VK_X;
	public static final int	KEY_Y 				= KeyEvent.VK_Y;
	public static final int KEY_Z				= KeyEvent.VK_Z;
	public static final int KEY_CTRL 			= Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

	//find/replace text
	public static final String FIND				= "Find";
	public static final String FIND_DIRECTION	= "Direction";
	public static final String FIND_NEXT		= "Next";
	public static final String FIND_PREVIOUS	= "Previous";
	public static final String FIND_ALL			= "All";
	public static final String FIND_CONTROL		= "Control";
	public static final String FIND_MATCH_CASE	= "Match Case";
	public static final String FIND_WRAP		= "Wrap Document";
	public static final String FIND_REGEX		= "Use RegExp";
	public static final String FIND_FOUND		= "Found";
	public static final String FIND_MATCHES 	= "Matches";
	public static final String FIND_INVALID		= "Invalid Expression";
	public static final String REPLACE			= "Replace";

	//font families
	public static final String DEFAULT_FAMILY	= "Helvetica";
	public static final String[] FONT_FAMILIES	= new String[] {
		"Ariel",
		"Baskerville",
		"Courier New",
		"Helvetica",
		"Impact",
		"Optima",
		"Tahoma",
		"Times New Roman",
		"Verdana"
	};

	//font sizes
	public static final String DEFAULT_SIZE		= "12";
	public static final String[] FONT_SIZES		= new String[] {
		"9",
		"10",
		"11",
		"12",
		"13",
		"14",
		"18",
		"24",
		"36",
		"48"
	};

	//font colors
	public static final String DEFAULT_COLOR	= "black";
	public static final String[] FONT_COLORS	= new String[] {
		"black",
		"blue",
		"cyan",
		"gray",
		"green",
		"orange",
		"magenta",
		"red",
		"yellow"
	};

	//color map
	public static final Map<String, Color> COLOR_MAP	= new HashMap<>();
	public static final void initColorMap() {
		COLOR_MAP.put("black", Color.BLACK);
		COLOR_MAP.put("blue", Color.BLUE);
		COLOR_MAP.put("cyan", Color.CYAN);
		COLOR_MAP.put("gray", Color.GRAY);
		COLOR_MAP.put("green", Color.GREEN);
		COLOR_MAP.put("orange", Color.ORANGE);
		COLOR_MAP.put("magenta", Color.MAGENTA);
		COLOR_MAP.put("red", Color.RED);
		COLOR_MAP.put("yellow", Color.YELLOW);
	}
}