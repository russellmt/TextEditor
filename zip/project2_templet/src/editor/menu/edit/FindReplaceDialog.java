package editor.menu.edit;

import editor.TextEditorPane;
import editor.menu.MenuConstants;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FindReplaceDialog extends JDialog {

    private TextEditorPane editor;

    private JTextField findField;
    private JTextField replaceField;

    private JRadioButton previousButton;
    private JRadioButton nextButton;
    private JRadioButton allButton;

    private JCheckBox caseButton;
    private JCheckBox wrapButton;
    private JCheckBox regexButton;

    private JPanel foundPane;
    private JLabel matchedText;
    private JLabel numFound;

    private Highlighter highlighter;
    private DefaultHighlighter.DefaultHighlightPainter painter;
    private Highlighter.Highlight[] currentHighlights;

    public FindReplaceDialog (TextEditorPane editor) {
        this.editor = editor;
        this.highlighter = editor.getHighlighter();
        this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.getColor("yellow"));
        build();
    }

    private void build() {
        setTitle(MenuConstants.FIND_REPLACE);
        setModal(false);
        setSize(400, 250);
        setResizable(false);
        setLocationRelativeTo(editor);
        setAlwaysOnTop(true);
        addWindowFocusListener(createWindowAdapter());

        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        buildFindPane(pane);
        buildReplacePane(pane);
        buildSettingsPane(pane);

        setContentPane(pane);
    }

    private void buildFindPane(JPanel parentPane) {
        JPanel findPane = new JPanel();
        findPane.setBorder(BorderFactory.createTitledBorder(MenuConstants.FIND));

        ActionListener findListener = createFindListener();

        findField = new JTextField(20);
        findField.addActionListener(findListener);

        JButton findButton = new JButton(MenuConstants.FIND);
        findButton.addActionListener(findListener);

        findPane.add(findField);
        findPane.add(findButton);

        parentPane.add(findPane);
    }

    private void buildReplacePane(JPanel parentPane) {
        JPanel replacePane = new JPanel();
        replacePane.setBorder(BorderFactory.createTitledBorder(MenuConstants.REPLACE));

        ActionListener replaceListener = createReplaceListener();

        replaceField = new JTextField(20);
        replaceField.addActionListener(replaceListener);

        JButton replaceButton = new JButton(MenuConstants.REPLACE);
        replaceButton.addActionListener(replaceListener);

        replacePane.add(replaceField);
        replacePane.add(replaceButton);

        parentPane.add(replacePane);
    }

    private void buildSettingsPane(JPanel parentPane) {
        JPanel settingsPane = new JPanel();
        settingsPane.setLayout(new BoxLayout(settingsPane, BoxLayout.X_AXIS));

        buildDirectionPane(settingsPane);
        buildControlPane(settingsPane);
        buildFoundPane(settingsPane);

        parentPane.add(settingsPane);
    }

    private void buildDirectionPane(JPanel parentPane) {
        JPanel directionPane = new JPanel();
        directionPane.setBorder(BorderFactory.createTitledBorder(MenuConstants.FIND_DIRECTION));
        directionPane.setLayout(new BoxLayout(directionPane, BoxLayout.Y_AXIS));

        ButtonGroup directionGroup = new ButtonGroup();
        previousButton = new JRadioButton(MenuConstants.FIND_PREVIOUS);
        nextButton = new JRadioButton(MenuConstants.FIND_NEXT);
        allButton = new JRadioButton(MenuConstants.FIND_ALL);
        nextButton.setSelected(true);

        directionGroup.add(previousButton);
        directionGroup.add(nextButton);
        directionGroup.add(allButton);

        directionPane.add(previousButton);
        directionPane.add(nextButton);
        directionPane.add(allButton);

        parentPane.add(directionPane);
    }

    private void buildControlPane(JPanel parentPane) {
        JPanel controlPane = new JPanel();
        controlPane.setBorder(BorderFactory.createTitledBorder(MenuConstants.FIND_CONTROL));
        controlPane.setLayout(new BoxLayout(controlPane, BoxLayout.Y_AXIS));

        caseButton = new JCheckBox(MenuConstants.FIND_MATCH_CASE);
        wrapButton = new JCheckBox(MenuConstants.FIND_WRAP);
        regexButton = new JCheckBox(MenuConstants.FIND_REGEX);

        controlPane.add(caseButton);
        controlPane.add(wrapButton);
        controlPane.add(regexButton);

        parentPane.add(controlPane);
    }

    private void buildFoundPane(JPanel parentPane) {
        foundPane = new JPanel();
        foundPane.setBorder(BorderFactory.createTitledBorder(MenuConstants.FIND_FOUND));
        foundPane.setLayout(new BoxLayout(foundPane, BoxLayout.Y_AXIS));

        numFound = new JLabel();
        matchedText = new JLabel();
        matchedText.setFont(new Font(MenuConstants.DEFAULT_FAMILY, Font.BOLD, 14));

        foundPane.add(matchedText);
        foundPane.add(numFound);
        foundPane.setVisible(false);

        parentPane.add(foundPane);
    }

    private WindowAdapter createWindowAdapter() {
        return new WindowAdapter() {
            public void windowLostFocus(WindowEvent e) {
                super.windowLostFocus(e);
                editor.getHighlighter().removeAllHighlights();
                currentHighlights = null;
            }
        };
    }

    private ActionListener createFindListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!editor.isEmpty()) {
                    handleFind();
                }
            }
        };
    }

    private ActionListener createReplaceListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!editor.isEmpty()) {
                    handleReplace();
                }
            }
        };
    }

    private void handleFind() {
        Pattern pattern;
        try {
            pattern = compileFindPattern();
        } catch (PatternSyntaxException e) {
            updateFoundError();
            return;
        }

        String content = getEditorContent();
        Matcher matcher = pattern.matcher(content);
        highlightNextMatch(matcher);
    }

    private String getEditorContent() {
        if (allButton.isSelected()) {
            editor.setCaretPosition(0);
        }
        int caretPos = editor.getCaretPosition();
        Document doc = editor.getDocument();

        StringBuilder contentBuilder = new StringBuilder();
        try {
            String textBeforeCaret = editor.getText(0, caretPos);
            String textAfterCaret = editor.getText(caretPos, doc.getLength() - caretPos);

            if (nextButton.isSelected() || allButton.isSelected() || wrapButton.isSelected()) {
                contentBuilder.append(textAfterCaret);
            }
            if (previousButton.isSelected() || allButton.isSelected() || wrapButton.isSelected()) {
                contentBuilder.append(textBeforeCaret);
            }
        } catch (BadLocationException e) {
            System.out.println(e.getMessage());
        }
        return contentBuilder.toString();
    }

    private Pattern compileFindPattern() throws PatternSyntaxException {
        String text = regexButton.isSelected() ?
                findField.getText() :
                Pattern.quote(findField.getText());
        Pattern pattern = caseButton.isSelected() ?
                Pattern.compile(text) :
                Pattern.compile(text, Pattern.CASE_INSENSITIVE);
        return pattern;
    }

    private void highlightNextMatch(Matcher matcher) {
        highlighter.removeAllHighlights();

        if (findNextPattern(matcher)) {
            int startPos = getHighlightStartPos(matcher);
            int endPos = getHighlightEndPos(matcher);
            if (nextButton.isSelected()) {
                editor.setCaretPosition(endPos);
            } else if (previousButton.isSelected()) {
                editor.setCaretPosition(startPos);
            }
            doHighlight(startPos, endPos);
            currentHighlights = highlighter.getHighlights();
        }
    }

    private void doHighlight(int startPos, int endPos) {
        if (endPos > startPos) {
            try {
                highlighter.addHighlight(startPos, endPos, painter);
            } catch (BadLocationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private int countMatchAll(Matcher matcher) {
        int count = 0;
        while (matcher.find()) {
            if (allButton.isSelected()) {
                doHighlight(getHighlightStartPos(matcher), getHighlightEndPos(matcher));
            }
            count++;
        }
        currentHighlights = highlighter.getHighlights();    //this may unnecessarily reset current highlights if all is not selected
        matcher.reset();
        return count;
    }

    private boolean findNextPattern(Matcher matcher) {
        int matchCount = countMatchAll(matcher);

        boolean isFound = false;
        if (nextButton.isSelected()) {
            isFound = matcher.find();
        } else if (previousButton.isSelected()) {
            for (int i = 0; i < matchCount; i++) {
                isFound = matcher.find();
            }
        }
        updateFoundInfo(matcher, matchCount);
        return isFound;
    }

    private void updateFoundInfo(Matcher matcher, int matchCount) {
        String match = "";
        if (!allButton.isSelected()) {
            try {
                match = matcher.group();
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }

        matchedText.setText(match);
        numFound.setText(matchCount + " " + MenuConstants.FIND_MATCHES);
        foundPane.setVisible(true);
        revalidate();
    }

    private void updateFoundError() {
        numFound.setText("");
        matchedText.setText(MenuConstants.FIND_INVALID);
        foundPane.setVisible(true);
        revalidate();
    }

    private void handleReplace() {
        if (currentHighlights != null) {
            int index = 0;
            for (Highlighter.Highlight highlight : currentHighlights) {
                if (canReplaceHighlight(index)) {
                    editor.setSelectionStart(highlight.getStartOffset());
                    editor.setSelectionEnd(highlight.getEndOffset());
                    editor.replaceSelection(replaceField.getText());
                }
                index++;
            }
        }
        handleFind();
    }

    private boolean canReplaceHighlight(int index) {
        return allButton.isSelected() ||
                (nextButton.isSelected() && index == 0) ||
                (previousButton.isSelected() && index == currentHighlights.length - 1);
    }

    private int getHighlightStartPos(Matcher matcher) {
        return getHighlightPos(matcher.start());
    }

    private int getHighlightEndPos(Matcher matcher) {
        return getHighlightPos(matcher.end());
    }

    private int getHighlightPos(int matcherPos) {
        return (editor.getCaretPosition() + matcherPos) % editor.getDocument().getLength();
    }
}
