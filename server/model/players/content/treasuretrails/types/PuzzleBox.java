package server.model.players.content.treasuretrails.types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import server.util.Misc;
import server.model.players.Player;
import server.model.players.content.treasuretrails.TreasureTrails;

public class PuzzleBox {


    /**
     * Checks if the clicked tile is legally allowed to slide into the blank space.
     * @param clickedIndex The 0-24 grid index the player just clicked.
     * @param blankIndex The 0-24 grid index where the empty hole currently is.
     * @return true if the move is a valid slide.
     */
    public static boolean canMove(int clickedIndex, int blankIndex) {
        // 1. Check Horizontal Move (Left or Right)
        // They must be 1 index apart, AND they must be on the exact same row.
        // We divide by 5 to check the row number (e.g., indexes 0-4 are row 0).
        if (Math.abs(clickedIndex - blankIndex) == 1 && (clickedIndex / 5) == (blankIndex / 5)) {
            return true;
        }

        // 2. Check Vertical Move (Up or Down)
        // Because the grid is 5 tiles wide, the tile exactly above or below you is always 5 indexes away.
        if (Math.abs(clickedIndex - blankIndex) == 5) {
            return true;
        }

        // If it's diagonal or further away, it's an illegal move.
        return false;
    }
    /**
     * Scrambles the puzzle by making 100 random valid slide moves.
     * This guarantees the puzzle is mathematically solvable!
     */
 // 2D Array containing all 7 puzzles in perfect numerical order
    public static final int[][] SOLVED_STATES = {
        // 0: Castle
        { 2749, 2750, 2751, 2752, 2753, 2754, 2755, 2756, 2757, 2758, 2759, 2760, 2761, 2762, 2763, 2764, 2765, 2766, 2767, 2768, 2769, 2770, 2771, 2772, -1 },
        // 1: Tree
        { 3619, 3620, 3621, 3622, 3623, 3624, 3625, 3626, 3627, 3628, 3629, 3630, 3631, 3632, 3633, 3634, 3635, 3636, 3637, 3638, 3639, 3640, 3641, 3642, -1 },
        // 2: Troll
        { 3643, 3644, 3645, 3646, 3647, 3648, 3649, 3650, 3651, 3652, 3653, 3654, 3655, 3656, 3657, 3658, 3659, 3660, 3661, 3662, 3663, 3664, 3665, 3666, -1 },
        // 3: Zulrah
        { 20283, 20284, 20285, 20286, 20287, 20288, 20289, 20290, 20291, 20292, 20293, 20294, 20295, 20296, 20297, 20298, 20299, 20300, 20301, 20302, 20303, 20304, 20305, 20306, -1 },
        // 4: Cerberus
        { 20307, 20308, 20309, 20310, 20311, 20312, 20313, 20314, 20315, 20316, 20317, 20318, 20319, 20320, 20321, 20322, 20323, 20324, 20325, 20326, 20327, 20328, 20329, 20330, -1 },
        // 5: Gnome Child
        { 20331, 20332, 20333, 20334, 20335, 20336, 20337, 20338, 20339, 20340, 20341, 20342, 20343, 20344, 20345, 20346, 20347, 20348, 20349, 20350, 20351, 20352, 20353, 20354, -1 },
        // 6: Theatre of Blood
        { 23418, 23419, 23420, 23421, 23422, 23423, 23424, 23425, 23426, 23427, 23428, 23429, 23430, 23431, 23432, 23433, 23434, 23435, 23436, 23437, 23438, 23439, 23440, 23441, -1 }
    };

    /**
     * Checks if the player's current puzzle perfectly matches their assigned puzzle's solved state.
     */
    public static boolean isSolved(Player c) {
        if (c.currentPuzzleId < 0 || c.currentPuzzleId >= SOLVED_STATES.length) {
            return false; // Failsafe
        }
        
        int[] targetSolution = SOLVED_STATES[c.currentPuzzleId];
        
        for (int i = 0; i < targetSolution.length; i++) {
            if (c.currentPuzzle[i] != targetSolution[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Scrambles a randomly chosen puzzle image.
     */
    public static void scramblePuzzle(Player c) {
        c.currentPuzzleId = server.util.Misc.random(SOLVED_STATES.length - 1);
        c.currentPuzzle = java.util.Arrays.copyOf(SOLVED_STATES[c.currentPuzzleId], 25);
        int blankIndex = 24; 
        
        // Clear the memory bank from any previous puzzles!
        c.puzzleSolutionSteps.clear();

        for (int i = 0; i < 100; i++) {
            java.util.List<Integer> validMoves = new java.util.ArrayList<>();
            
            if (blankIndex % 5 != 0) validMoves.add(blankIndex - 1); // Left
            if (blankIndex % 5 != 4) validMoves.add(blankIndex + 1); // Right
            if (blankIndex >= 5) validMoves.add(blankIndex - 5);     // Up
            if (blankIndex < 20) validMoves.add(blankIndex + 5);     // Down

            int moveToMake = validMoves.get(server.util.Misc.random(validMoves.size() - 1));

            // RECORD THE REVERSE MOVE: 
            // To undo this slide, we will need to click the spot where the blank space used to be!
            c.puzzleSolutionSteps.push(blankIndex);

            c.currentPuzzle[blankIndex] = c.currentPuzzle[moveToMake];
            c.currentPuzzle[moveToMake] = -1;
            blankIndex = moveToMake; 
        }
    }
    /**
     * Handles the player clicking a puzzle tile.
     * @param c The player.
     * @param itemId The item ID of the puzzle piece clicked.
     * @param slot The 0-24 grid index the player clicked.
     */
    public static void clickTile(Player c, int itemId, int slot) {
        // 1. Find the blank space
        int blankIndex = -1;
        for (int i = 0; i < c.currentPuzzle.length; i++) {
            if (c.currentPuzzle[i] == -1) {
                blankIndex = i;
                break;
            }
        }

        // Failsafe
        if (blankIndex == -1 || c.currentPuzzle[slot] != itemId) {
            return; 
        }

        // 2. Check if the move is legal
        if (canMove(slot, blankIndex)) {
            
            // 3. Swap the pieces in the array
            c.currentPuzzle[blankIndex] = c.currentPuzzle[slot];
            c.currentPuzzle[slot] = -1;

            // 4. Redraw the puzzle interface with the new positions
            openPuzzle(c);

            // 5. Check if they just solved it!
         // 5. Check if they just solved it!
            if (isSolved(c)) {
                c.sendMessage("You have solved the puzzle!");
                c.getPA().closeAllWindows();
                
                // Reset the puzzle arrays so it doesn't stay solved forever
                c.currentPuzzle = new int[25]; 
                c.currentPuzzleId = -1;
                
                // Remove the puzzle box from their inventory (Assuming ID 2800 for Hard Puzzle Box)
                if (c.getItems().playerHasItem(2800)) {
                    c.getItems().deleteItem(2800, 1);
                }

                // Advance their Hard Clue progress! (Tier 3)
               // TreasureTrails.advanceClue(c, 3);
            }
        }
    }
    /**
     * Sends the items to the interface and opens the puzzle box for the player.
     */
    /**
     * Sends the items to the interface and opens the puzzle box for the player.
     */
    public static void openPuzzle(Player c) {
        // If they don't have a puzzle started, scramble a new one!
        if (c.currentPuzzle == null || c.currentPuzzle[0] == 0) {
            scramblePuzzle(c);
        }

        // Grab the perfect solution for whatever image they rolled
        int[] targetSolution = SOLVED_STATES[c.currentPuzzleId];

        // Loop through all 25 slots
        for (int i = 0; i < c.currentPuzzle.length; i++) {
            
            // 1. Draw the scrambled pieces on the playable board (6980)
            int scrambledItemId = c.currentPuzzle[i];
            if (scrambledItemId > 0) {
                c.getPA().sendFrame34a(6980, scrambledItemId, i, 1);
            } else {
                c.getPA().sendFrame34a(6980, -1, i, 1);
            }

            // 2. Draw the perfect solution on the hidden Hint board (6985)
            int solvedItemId = targetSolution[i];
            if (solvedItemId > 0) {
                c.getPA().sendFrame34a(6985, solvedItemId, i, 1);
            } else {
                c.getPA().sendFrame34a(6985, -1, i, 1);
            }
        }
        
        // Open the main puzzle interface
        c.getPA().showInterface(6976);
    }
}