/*******************
 * Christian A. Duncan
 * CSC350: Intelligent Systems
 * Spring 2017
 *
 * AI Game Interface
 * This project is designed to support a simple direct interaction of a 2-player turn-based game.
 * See README file for more details.
 ********************/

package cad.ai.game;

import java.net.*;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;
import cad.ai.game.*;

/***********************************************************
 * The PlayGame class is designed to allow two players to 
 * player against each other off-line.  At start up, players can
 * decide if home is AI or human and the same for the away.
 *
 * Currently, only NIM is supported but options to choose a game
 * will be added in a future release.
 ***********************************************************/
public class PlayGame {
    private BufferedReader userIn = null;  // Access to user input
    private Game game[]; 
    private Game serverGame = null;
    private AI ai[];
    
    /**
     * Constructor
     * @param homeAI true if home is AI, false if home is human.
     * @param awayAI true if away is AI, false if away is human.
     **/
    public PlayGame(boolean homeAI, boolean awayAI) {
        userIn = new BufferedReader(new InputStreamReader(System.in));  // To access user input stream
        game = new Game[2];  // A copy of the game for each player.
        ai = new AI[2];      // A spot for ai for each player.
        if (homeAI) ai[0] = new NimAI(); else ai[0] = null;
        if (awayAI) ai[1] = new NimAI(); else ai[1] = null;

        for (int p = 0; p < 2; p++)
            game[p] = new NimGame(p, userIn, ai[p], false);
        serverGame = new NimGame(-1, userIn, null, true);
    }

    /**
     * Start playing the game
     **/
    public void run() {
        while (!serverGame.isDone()) {
            String state = serverGame.getState(true);  // Get the state
            int turn = serverGame.getTurn();  // Whose turn is it
            if (turn >= 0 && turn <= 1) {
        	// Get the move based on current game state
        	game[turn].updateState(state);
        	String move = game[turn].getMove();
        	processInput(move, turn);
            }
        }

        // Let the games both know the winner...
        int winner = serverGame.getWinner();
        if (winner == 0) {
            System.out.println("Home won.");
        } else if (winner == 1) {
            System.out.println("Away won.");
        } else {
            System.out.println("It was a tie.");  // Not possible in NIM.
        }
        
        /** Skipping - not needed for Nim Game 
            char r = winner == 0 ? 'H' : winner == 1 ? 'A' : 'T';
            game[0].postWinner(r);
            game[1].postWinner(r);
        **/
        System.out.println("Good-bye!");
    }

    /**
     * Process the message provided.  Uses protocol described in ServerProtocol.txt
     * @param message  The message to process
     * @param p The player that sent it
     **/
    synchronized private void processInput(String message, int p) {
        try {
            String[] pieces = message.split(":", 5);
            String command = pieces[0].toUpperCase();
            switch (command) {
            case "@ERROR": processErrorMessage(pieces, p); break;
            case "@MESSAGE": processMessage(pieces, p); break;
            case "@GAME": processGameCommands(pieces, p); break;
            default: error("Unrecognized command from server. " + message);
            }
        } catch (Exception e) {
            error("Error processing command (" + message + "). " + e.getMessage());
        }
    }

    synchronized private void processErrorMessage(String[] pieces, int p) {
        if (pieces.length < 2) {
            debug("Error Message was incorrectly transmitted.");
        } else {
            display("ERROR: " + pieces[1]);
        }
    }

    synchronized private void processMessage(String[] pieces, int p) {
        if (pieces.length < 2) {
            debug("Message was incorrectly transmitted.");
        } else {
            display(pieces[1]);
        }
    }
        
    synchronized private void processGameCommands(String[] pieces, int p) {
        if (pieces.length < 2) {
            debug("Error.  No game subcommand submitted...");
            return;
        }
        String command = pieces[1];
        switch(command) {
        case "START": processGameStart(pieces, p); break;
        case "STATE": processGameState(pieces, p); break;
        case "MOVE": processGameMove(pieces, p); break;
        case "ERROR": processGameErrorMessage(pieces, p); break;
        case "MESSAGE": processGameMessage(pieces, p); break;
        case "RESULT": processGameResult(pieces, p); break;
        default: debug("Unrecognized game command transmitted: " + command);
        }
    }

    synchronized private void processGameStart(String[] pieces, int p) {
        debug("Error.  This should not need to be sent in PlayGame matches.");
    }
    
    synchronized private void processGameState(String[] pieces, int p) {
        debug("Hmm, this should not be transmitted as input to process.  Ignoring...");
    }

    synchronized private void processGameMove(String[] pieces, int p) {
        if (pieces.length < 3)
            debug("No game move information was transmitted!");
        else {
            String res = serverGame.processMove(p, pieces[2]);
            String message = res + (p == 0 ? "[Home]" : "[Away]");
            System.out.println(message);
        }
    }

    synchronized private void processGameErrorMessage(String[] pieces, int p) {
        if (pieces.length < 3) {
            debug("Game Error Message was incorrectly transmitted.");
        } else {
            display("GAME ERROR: " + pieces[2]);
        }
    }
    
    synchronized private void processGameMessage(String[] pieces, int p) {
        if (pieces.length < 3) {
            debug("Game Message was incorrectly transmitted.");
        } else {
            display(pieces[2]);
        }
    }
    
    synchronized private void processGameResult(String[] pieces, int p) {
        debug("Hmm, this should not be transmitted either.");
    }
    
    // For displaying debug, error, and regular messages
    private void error(String message) { System.err.println("ERROR: " + message); }
    private void debug(String message) { System.err.println("DEBUG: " + message); }
    private void display(String message) { System.out.println(message); }

    /**
     * The main entry point.
     **/
    public static void main(String[] args) {
        // Defaults to use
        boolean homeAI = true;
        boolean awayAI = true;
        
        // Parse the arguments
        for (String arg: args) {
            try {
        	String[] params = arg.split("=",2);
        	switch (params[0]) {
        	case "--help":
        	    printUsage(null);  // just print the Help message and exit
        	    break;
        	case "--home": 
        	    switch (params[1]) {
        	    case "ai": homeAI = true; break;
        	    case "human": homeAI = false; break;
        	    default: printUsage("Unrecognized option to --home");
        	    }
        	    break;
        	case "--away": 
        	    switch (params[1]) {
        	    case "ai": awayAI = true; break;
        	    case "human": awayAI = false; break;
        	    default: printUsage("Unrecognized option to --away");
        	    }
        	    break;
        	default:
        	    printUsage("Unrecognized parameter: " + arg);
        	}
            } catch (Exception e) {
        	printUsage("Error processing parameter: " + arg);
            }
        }	    

        PlayGame c = new PlayGame(homeAI, awayAI);
        c.run();
    }

    /**
     * Print Usage message and exit
     **/
    public static void printUsage(String message) {
        System.err.println("Usage: java cad.ai.game.PlayGame [params]");
        System.err.println("       Where params are:");
        System.err.println("         --help            -- Print this usage message");
        System.err.println("         --home=ai/human   -- Home is ai or human (default is ai).");
        System.err.println("         --away=ai/human   -- Away is ai or human (default is ai).");
        if (message != null) 
            System.err.println("       " + message);
        System.exit(1);
    }       
}
