package src;

// No changes to ParsedCommand.java expected, assuming it's correct from prior attempts.

public class MarkdownCommandParser {

    public ParsedCommand parse(String markdownString) {
        if (markdownString == null || markdownString.trim().isEmpty()) {
            // System.err.println("DEBUG: Markdown string is null or empty.");
            return null;
        }

        String[] lines = markdownString.trim().split("\r?\n");
        ParsedCommand parsedCommand = null;
        String firstActionEncountered = null;

        for (String line : lines) {
            String trimmedLine = line.trim();
            // System.out.println("DEBUG: Processing line: [" + trimmedLine + "]");

            if (!trimmedLine.startsWith("**")) {
                // System.out.println("DEBUG: Line does not start with '**': " + trimmedLine);
                continue;
            }

            // The key ends just before the colon.
            int colonIndex = trimmedLine.indexOf(':');

            // Validate colonIndex: must exist, and must be after the initial "**"
            // e.g., "**K:**" means key is "K", colonIndex is 3. So colonIndex must be > 2 (i.e. >=3).
            if (colonIndex < 3) {
                // System.out.println("DEBUG: Colon not found, or key is too short (colonIndex=" + colonIndex + "). Line: " + trimmedLine);
                continue;
            }

            String key = trimmedLine.substring(2, colonIndex); // Extract from after "**" up to (but not including) colon
            // System.out.println("DEBUG: Extracted key: [" + key + "]");

            String valuePart = "";
            if (trimmedLine.length() > colonIndex + 1) { // Check if there's anything after the colon
                valuePart = trimmedLine.substring(colonIndex + 1).trim(); // Extract from after colon, then trim
            }
            // System.out.println("DEBUG: Extracted value part (raw): [" + valuePart + "]");

            // Workaround for the strange "** Value" issue previously observed
            if (valuePart.startsWith("** ")) {
               valuePart = valuePart.substring(3).trim();
            //    System.out.println("DEBUG: Applied workaround for value part, now: [" + valuePart + "]");
            } else if (valuePart.startsWith("**")) { // if no space after **
               valuePart = valuePart.substring(2).trim();
            //     System.out.println("DEBUG: Applied workaround (no space) for value part, now: [" + valuePart + "]");
            }


            if (key.equalsIgnoreCase("Action")) {
                if (parsedCommand == null) {
                    parsedCommand = new ParsedCommand(valuePart);
                    firstActionEncountered = valuePart;
                    // System.out.println("DEBUG: Set Action: " + valuePart);
                } else if (!firstActionEncountered.equalsIgnoreCase(valuePart)) {
                    // System.err.println("DEBUG: Multiple different Action keys found. Current: '" + valuePart + "', Using first: '" + firstActionEncountered +"'");
                }
            } else if (parsedCommand != null) {
                if (!key.isEmpty()) {
                    parsedCommand.addParameter(key, valuePart);
                    // System.out.println("DEBUG: Added parameter: [" + key + "] = [" + valuePart + "]");
                } else {
                     // This should ideally not be reached if colonIndex < 3 check is correct and key is non-empty
                    // System.err.println("DEBUG: Empty key found for value: [" + valuePart + "] in line: " + trimmedLine);
                }
            } else {
                // System.err.println("DEBUG: Parameter found before Action key: Key=[" + key + "], Value=[" + valuePart + "]. Ignoring.");
            }
        }

        if (parsedCommand == null) {
            // System.err.println("DEBUG: No valid Action key found or command is malformed.");
            return null;
        }
        // System.out.println("DEBUG: Final parsed command: " + parsedCommand.toString());
        return parsedCommand;
    }

    public static void main(String[] args) {
        MarkdownCommandParser parser = new MarkdownCommandParser();
        System.out.println("Starting parser tests...");

        String testCommand1 = "**Action:** Move\n**Direction:** North";
        ParsedCommand cmd1 = parser.parse(testCommand1);
        System.out.println("Test 1 (Move North): " + cmd1);

        String testCommand2 = "**Action:** Attack\n**TargetID:** entity_123\n**Damage:** 10";
        ParsedCommand cmd2 = parser.parse(testCommand2);
        System.out.println("Test 2 (Attack): " + cmd2);

        String testCommand3 = "**Action:** Eat";
        ParsedCommand cmd3 = parser.parse(testCommand3);
        System.out.println("Test 3 (Eat, no params): " + cmd3);

        String testCommand4 = "**Direction:** North\n**Action:** Move";
        ParsedCommand cmd4 = parser.parse(testCommand4);
        System.out.println("Test 4 (Action not first): " + cmd4);

        String testCommand5 = "**Action:** Move\n**Action:** Attack";
        ParsedCommand cmd5 = parser.parse(testCommand5);
        System.out.println("Test 5 (Multiple Actions): " + cmd5);

        String malformedCommand1 = "Action: Move\nDirection: North";
        ParsedCommand cmdMalformed1 = parser.parse(malformedCommand1);
        System.out.println("Test Malformed 1 (No asterisks): " + cmdMalformed1);

        String malformedCommand2 = "****: Move\n**Direction:** North"; // Malformed key
        ParsedCommand cmdMalformed2 = parser.parse(malformedCommand2);
        System.out.println("Test Malformed 2 (Empty Key): " + cmdMalformed2);

        String testCommandEmptyValue = "**Action:** Test\n**Param:**";
        ParsedCommand cmdEmptyValue = parser.parse(testCommandEmptyValue);
        System.out.println("Test Empty Value: " + cmdEmptyValue);

        String testCommandNoValue = "**Action:** Test\n**Param:**      ";
        ParsedCommand cmdNoValue = parser.parse(testCommandNoValue);
        System.out.println("Test No Value (spaces): " + cmdNoValue);

        // Test case from the previous failure description where value was "** Move"
        String problematicTest = "**Action:** ** Move"; // Simulating the problematic reported input
        System.out.println("Problematic Test Input: " + problematicTest);
        ParsedCommand cmdProblematic = parser.parse(problematicTest);
        System.out.println("Problematic Test Output: " + cmdProblematic);

        String simpleActionValue = "**Action:** Move";
        System.out.println("Simple Action Value Input: " + simpleActionValue);
        ParsedCommand cmdSimpleActionValue = parser.parse(simpleActionValue);
        System.out.println("Simple Action Value Output: " + cmdSimpleActionValue);

        String actionNoSpace = "**Action:**Attack";
         ParsedCommand cmdActionNoSpace = parser.parse(actionNoSpace);
        System.out.println("Test Action No Space After Colon: " + cmdActionNoSpace);


        System.out.println("Parser tests finished.");
    }
}
