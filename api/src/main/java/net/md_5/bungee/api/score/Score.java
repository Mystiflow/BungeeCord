package net.md_5.bungee.api.score;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a scoreboard score entry.
 */
@Data
@AllArgsConstructor
public class Score
{

    /**
     * Name to be displayed in the list.
     */
    private final String itemName; // Player
    /**
     * Unique name of the score.
     */
    private String scoreName; // Score
    /**
     * Value of the score.
     */
    private int value;
}
