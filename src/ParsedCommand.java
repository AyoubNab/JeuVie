package src;

import java.util.Map;
import java.util.HashMap;

public class ParsedCommand {
    private String action;
    private Map<String, String> parameters;

    public ParsedCommand(String action) {
        this.action = action;
        this.parameters = new HashMap<>();
    }

    public String getAction() {
        return action;
    }

    public void addParameter(String key, String value) {
        this.parameters.put(key, value);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "ParsedCommand{" +
               "action='" + action + '\'' +
               ", parameters=" + parameters +
               '}';
    }
}
