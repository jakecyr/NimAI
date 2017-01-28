/*******************
 * Christian A. Duncan
 * CSC350: Intelligent Systems
 * Spring 2017
 *
 * AI Game Client
 * This project is designed to link to a basic Game Server to test
 * AI-based solutions.
 * See README file for more details.
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
        int take = 0;
        int r = 0;

        //Calculate nimSum
        for (int i = 1; i < rows.length; i++) {
            nimSum = nimSum ^ rows[i];
        }

        //Choose random move
        if (nimSum == 0) {
            r = ran.nextInt(rows.length);
            while (rows[r] == 0) r = (r + 1) % rows.length;
            take = ran.nextInt(rows[r]) + 1;

            return r + "," + take;
        }
        //Use nimSum to find optimal move
        else {
            //Loop through all rows
            for (int i = 0; i < rows.length; i++) {
                //Find first row where nimSum XOR rows[i]) < rows[i]
                if ((nimSum ^ rows[i]) < rows[i]) {
                    take = rows[i] - (nimSum ^ rows[i]);
                    return i + "," + take;
                }
            }

            System.out.println("Using NimSum");
        }

        return "0,0";
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
