import java.util.List;
import java.util.List;
import java.util.ArrayList;

// Importing the actual classes from their packages
import Entite.Entite;
import Monde.Monde;
import Monde.Block;
import src.MarkdownCommandParser; // Added import
import src.ParsedCommand;       // Added import
import java.util.Map;           // Added import for Map

// No direct need for Humain or Animaux imports if using Entite type polymorphically

public class ApiHandler {

    // Inner class for Entity Status Response
    public static class EntityStatusResponse {
        String entityId;
        String type;
        int x;
        int y;
        // Add other relevant status fields here

        public EntityStatusResponse(String entityId, String type, int x, int y) {
            this.entityId = entityId;
            this.type = type;
            this.x = x;
            this.y = y;
        }

        // Conceptual JSON conversion
        @Override
        public String toString() {
            return String.format("{\"entityId\": \"%s\", \"type\": \"%s\", \"x\": %d, \"y\": %d}",
                                 entityId, type, x, y);
        }
    }

    // Inner class for Block Info Response
    public static class BlockInfoResponse {
        String blockId; // Assuming blocks will have IDs or coordinates
        String type;    // Type of block (e.g., "grass", "water")
        boolean isSolid;
        // Add other relevant block data fields here

        public BlockInfoResponse(String blockId, String type, boolean isSolid) {
            this.blockId = blockId;
            this.type = type;
            this.isSolid = isSolid;
        }

        // Conceptual JSON conversion
        @Override
        public String toString() {
            return String.format("{\"blockId\": \"%s\", \"type\": \"%s\", \"isSolid\": %b}",
                                 blockId, type, isSolid);
        }
    }

    // Inner class for Notification Payload
    public static class NotificationPayload {
        String entityId;
        List<String> messages;
        boolean success;

        public NotificationPayload(String entityId, List<String> messages, boolean success) {
            this.entityId = entityId;
            this.messages = messages;
            this.success = success;
        }

        // Conceptual JSON conversion
        @Override
        public String toString() {
            StringBuilder messagesJson = new StringBuilder("[");
            for (int i = 0; i < messages.size(); i++) {
                messagesJson.append("\"").append(messages.get(i)).append("\"");
                if (i < messages.size() - 1) {
                    messagesJson.append(",");
                }
            }
            messagesJson.append("]");
            return String.format("{\"entityId\": \"%s\", \"messages\": %s, \"success\": %b}",
                                 entityId, messagesJson.toString(), success);
        }
    }

    /**
     * Returns a JSON string representing entity status.
     * @param entityId The ID of the entity.
     * @param monde The world object.
     * @return JSON string of entity status.
     */
    public String getEntityStatus(String entityId, Monde monde) {
        Entite entity = monde.getEntiteById(entityId);
        if (entity != null) {
            // Using getters from the updated Entite class
            EntityStatusResponse response = new EntityStatusResponse(entity.getId(), entity.getType(), entity.getPositionX(), entity.getPositionY());
            return response.toString();
        }
        return String.format("{\"error\": \"Entity with ID %s not found\"}", entityId);
    }

    /**
     * Returns a JSON string for block info.
     * For now, this is a placeholder as block implementation is not detailed.
     * @param entityId The ID of the entity (to potentially get block info relative to entity).
     * @param monde The world object.
     * @return JSON string of block info.
     */
    public String getBlockInfo(String entityId, Monde monde) {
        // Placeholder: Assuming we want info about the block the entity is on.
        // This requires Monde to have a representation of the terrain/blocks.
        Entite entity = monde.getEntiteById(entityId);
        if (entity != null) {
            // Simulate getting block info at entity's position using getPositionX/Y
            // In a real scenario, Monde would have a method like getBlockAt(x, y)
            // and Block would have more detailed info.
            String blockIdAtEntity = "block_at_" + entity.getPositionX() + "_" + entity.getPositionY();
            Block currentBlock = entity.getBlockActuelle(); // Now uses imported Block
            String blockType = "unknown";
            boolean isSolid = true; // Default to solid if unknown
            if (currentBlock != null) {
                // Assuming Block has methods like getType() and isTraversable()
                // For now, using placeholder "grass" and !isTraversable() for solid
                blockType = currentBlock.estTraverseable() ? "traversable_generic" : "solid_generic";
                // This is a guess. Block.java has: Block(boolean estSolide, boolean estDetruit, boolean estTraversable)
                // Let's use estTraverseable directly.
                isSolid = !currentBlock.estTraverseable();
                // A more descriptive type would be good, e.g. currentBlock.getBlockType()
                // For now, let's use a generic type based on properties.
                // The Block constructor is Block(boolean estTraverseable, boolean estConstructible, boolean estDestructible)
                // It does not have estSolide directly. Let's assume if not traversable, it's solid.
                if (currentBlock.estTraverseable()){ // Simplified logic
                    blockType = "clear_ground";
                } else {
                    blockType = "solid_obstacle";
                }
            }
            BlockInfoResponse response = new BlockInfoResponse(blockIdAtEntity, blockType, isSolid);
            return response.toString();
        }
        return String.format("{\"error\": \"Entity with ID %s not found for block info\"}", entityId);
    }

    /**
     * Takes a Markdown command, processes it (placeholder for now),
     * and returns a JSON string with notifications.
     * @param entityId The ID of the entity to command.
     * @param markdownCommand The command in Markdown format.
     * @param monde The world object.
     * @return JSON string with notifications.
     */
    public String executeEntityCommand(String entityId, String markdownCommand, Monde monde) {
        Entite entity = monde.getEntiteById(entityId);
        List<String> notificationMessages = new ArrayList<>(); // Renamed for clarity
        boolean commandSuccess = false;
        String actionOutcome = "No action performed.";

        if (entity == null) {
            notificationMessages.add("Entity with ID " + entityId + " not found.");
            // Assuming NotificationPayload needs entityId, messages, success
            // The existing NotificationPayload in the provided ApiHandler.java does have this.
            return new NotificationPayload(entityId, notificationMessages, false).toString();
        }

        MarkdownCommandParser parser = new MarkdownCommandParser();
        ParsedCommand parsedCommand = parser.parse(markdownCommand);

        if (parsedCommand == null) {
            notificationMessages.add("Failed to parse command: [" + markdownCommand + "]");
            actionOutcome = "Command parsing failed.";
        } else {
            String action = parsedCommand.getAction();
            Map<String, String> params = parsedCommand.getParameters();
            actionOutcome = "Action: " + action + " processed."; // Default outcome

            notificationMessages.add("Executing action: " + action + " for entity " + entityId);

            switch (action.toLowerCase()) { // Use toLowerCase for case-insensitive action matching
                case "move":
                    String direction = params.get("Direction");
                    if (direction != null) {
                        int dx = 0;
                        int dy = 0;
                        switch (direction.toLowerCase()) {
                            case "north": dy = -1; break;
                            case "south": dy = 1; break;
                            case "east":  dx = 1; break;
                            case "west":  dx = -1; break;
                            default:
                                notificationMessages.add("Invalid direction: " + direction);
                                actionOutcome = "Move failed: Invalid direction " + direction;
                                break;
                        }
                        if (dx != 0 || dy != 0) {
                            // Store position before move to check if it changed
                            int oldX = entity.getPositionX();
                            int oldY = entity.getPositionY();
                            entity.deplacer(dx, dy); // Assumes Entite.deplacer handles invalid moves internally
                            if (entity.getPositionX() != oldX || entity.getPositionY() != oldY) {
                                actionOutcome = "Moved " + direction + " to (" + entity.getPositionX() + "," + entity.getPositionY() + ").";
                                commandSuccess = true;
                            } else {
                                actionOutcome = "Move " + direction + " failed (e.g., obstacle or boundary). Position: (" + entity.getPositionX() + "," + entity.getPositionY() + ").";
                                commandSuccess = false; // Explicitly set based on outcome
                            }
                            notificationMessages.add(actionOutcome);
                        }
                    } else {
                        notificationMessages.add("Move action failed: Missing Direction parameter.");
                        actionOutcome = "Move failed: Missing Direction.";
                    }
                    break;
                case "attack":
                    String targetId = params.get("TargetID");
                    String damageStr = params.get("Damage");
                    if (targetId != null && damageStr != null) {
                        Entite targetEntity = monde.getEntiteById(targetId);
                        if (targetEntity != null) {
                            try {
                                int damage = Integer.parseInt(damageStr);
                                // Store target health before attack for outcome message
                                int targetOldSante = targetEntity.getSante();
                                entity.attaquer(targetEntity, damage); // Assumes Entite.attaquer handles energy, etc.
                                actionOutcome = "Attacked " + targetId + " for " + damage + " damage. Target health now: " + targetEntity.getSante() + ".";
                                notificationMessages.add(actionOutcome);
                                commandSuccess = true;
                            } catch (NumberFormatException e) {
                                notificationMessages.add("Attack action failed: Invalid Damage value: " + damageStr);
                                actionOutcome = "Attack failed: Invalid Damage value.";
                            }
                        } else {
                            notificationMessages.add("Attack action failed: Target entity " + targetId + " not found.");
                            actionOutcome = "Attack failed: Target " + targetId + " not found.";
                        }
                    } else {
                        notificationMessages.add("Attack action failed: Missing TargetID or Damage parameter.");
                        actionOutcome = "Attack failed: Missing parameters.";
                    }
                    break;
                case "interact":
                    String interactionResult = entity.interagir(); // Entite.interagir() returns a String
                    notificationMessages.add("Interaction result: " + interactionResult);
                    actionOutcome = interactionResult;
                    commandSuccess = true;
                    break;
                case "wait":
                    notificationMessages.add(entity.getId() + " waits.");
                    actionOutcome = entity.getId() + " waited.";
                    commandSuccess = true;
                    break;
                // TODO: Add cases for "Eat", "Pickup" which might require Humain-specific logic
                // For now, they will fall into the default "unknown command"
                default:
                    notificationMessages.add("Unknown or unsupported action: " + action);
                    actionOutcome = "Unknown action: " + action;
                    break;
            }
        }

        // The 'actionOutcome' can be part of the messages, or a separate field in NotificationPayload later.
        // For now, it's added to notificationMessages.
        // The existing NotificationPayload(entityId, messages, success) should be used.
        // 'commandSuccess' is set based on the action's perceived success.

        // Construct the payload. The current NotificationPayload in ApiHandler is:
        // public NotificationPayload(String entityId, List<String> messages, boolean success)
        // This is fine for now.
        NotificationPayload response = new NotificationPayload(entityId, notificationMessages, commandSuccess);
        return response.toString();
    }
}
