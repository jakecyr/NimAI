/*******************
 * Christian A. Duncan
 * CSC350: Intelligent Systems
 * Spring 2017
 *
 * AI Game Client
 * This project is designed to link to a basic Game Server to test
 * AI-based solutions.
 * See README file for more details.
 *
 * Edited By: Jake Cyr, Ryan Ek, and Fanonx Rogers
 * January 30, 2017
 ********************/

package cad.ai.game;

import java.util.Random;

/***********************************************************
 * The AI system for a NimGame.
 *   Most of the game control is handled by the Server but
 *   the move selection is made here - either via user or an attached
 *   AI system.
 ***********************************************************/
public class NimAI implements AI {
    public NimGame game;  // The game that this AI system is playing
    Random ran;

    public NimAI() {
        game = null;
        ran = new Random();
    }

    public void attachGame(Game g) {
        game = (NimGame) g;
    }

    /**
     * Returns the Move as a String "R,S"
     * R=Row
     * S=Sticks to take from that row
     **/
    public synchronized String computeMove() {

        if (game == null) {
            System.err.println("CODE ERROR: AI is not attached to a game.");
            return "0,0";
        }

        int[] rows = (int[]) game.getStateAsObject();
        int nimSum = rows[0];
        int numRows = rows.length;
        int r;

        for (int i = 1; i < numRows; i++) nimSum = nimSum ^ rows[i]; //Calculate nimSum

        //Use the nimSum to return the most optimal quantity of sticks to take
        if (nimSum != 0) {
            //Loop through all rows
            for (r = 0; r < numRows; r++) {
                //Find first row where nimSum XOR rows[i]) < rows[i] and return the optimal number to take from that row
                if ((nimSum ^ rows[r]) < rows[r]) return r + "," + (rows[r] - (nimSum ^ rows[r]));
            }

            //If no optimal quantity to take found
            return "0,0";
        }
        //Return a random row and number of sticks to take
        else {
            r = ran.nextInt(numRows); //Get the number of rows
            while (rows[r] == 0) r = (r + 1) % numRows; //Find a non-empty row
            return r + "," + ran.nextInt(rows[r]) + 1; //Return the number of sticks to take and the row to take them from
        }
    }

    /**
     * Inform AI who the winner is
     * result is either (H)ome win, (A)way win, (T)ie
     **/
    public synchronized void postWinner(char result) {
        // This AI doesn't care.  No learning being done...
        game = null;  // No longer playing a game though.
    }
}