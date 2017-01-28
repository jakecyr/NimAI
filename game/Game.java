/*******************
 * Christian A. Duncan
 * CSC350: Intelligent Systems
 * Spring 2017
 *
 * AI Game Server Project
 * This project is designed to support multiple game platforms to test
 * AI-based solutions.
 * See README file for more details.
 ********************/

package cad.ai.game;

/***********************************************************
 * An interface whose sole purpose is to faciliate the running of
 * a single game (with accomodations for a client/server model approach).
 ***********************************************************/
public interface Game {
    boolean isDone();   // Is the game done?

    /**
     * Current state of game (in some string format - game dependent)
     * If force is false then a null is returned if nothing has changed since last getState --- 
     * so doesn't repeatedly send the same data...
     **/
    String getState(boolean force);

    /**
     * Get State of the game.  For the AI system.
     * This is an Object but will have to be type-cast after
     * to the proper state based on the Game.
     * This can be more code-friendly than a string.
     **/
    Object getStateAsObject();
    
    /**
     * Update the current state of game (in some string format - game dependent)
     **/
    void updateState(String state);

    /**
     * Get the move from the player or AI.
     * If AI system is in place, query AI else ask player
     **/
    String getMove();

    /**
     * Process the move requested by the player.
     * p is an integer for the player number.
     *  For two player games, 0=Home, 1=Away...
     * move is a String format for the move - game dependent.
     * Returns a String message to send back to the player.
     **/
    String processMove(int p, String move);

    /**
     * Get the winner.  Returns player that won.  
     *   0=Home, 1=Away, -1=Tie, -2=Aborted, -3=Not Finished
     **/
    int getWinner();

    /**
     * Is it current user's turn?  Based on state information...
     **/
    boolean isPlayerTurn();

    /**
     * Get whose turn it is (0=Home, 1=Away, -1=Nobody yet...)
     **/
    int getTurn();

    /**
     * Get the player's number (0=Home, 1=Away)
     **/
    int getPlayer();

    /**
     * Post the winner - useful to inform AI if it needs to "learn".
     *   result is either (H)ome win, (A)way win, (T)ie
     **/
    void postWinner(char result);
}
