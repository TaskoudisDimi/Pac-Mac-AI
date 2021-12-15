package examples.StarterPacMan;

import pacman.controllers.PacmanController;
import static pacman.game.Constants.*;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import java.awt.event.KeyEvent;
import pacman.controllers.KeyBoardInput;
import java.util.ArrayList;
import java.util.Random;
import pacman.controllers.Controller;
/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.username).
 */
public class MyPacMan extends Controller<MOVE> {
    private static final int MIN_DISTANCE = 20;
    private Random random = new Random();
    private int previous = 0;
    @Override
    public MOVE getMove(Game game, long timeDue) {
        //Get Current State
        int current = game.getPacmanCurrentNodeIndex();
        // WORK AROUND WITH SWITCH CASE
        //Run away state 1
        for(GHOST ghost : GHOST.values())
            if(game.getGhostEdibleTime(ghost)==0)
                if(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
                    return game.getNextMoveAwayFromTarget(current,game.getGhostCurrentNodeIndex(ghost),DM.PATH);
        //Chase state 2
        for(GHOST ghost : GHOST.values())
            if(game.getGhostEdibleTime(ghost) > 0)
                return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.PATH);

        //get all active pills in range
        int[] activePills=game.getActivePillsIndices();
        //get all active power pills in range
        int[] activePowerPills=game.getActivePowerPillsIndices();
        //create a target array that includes all  pills and power pills
        int[] targetNodeIndices=new int[activePills.length+activePowerPills.length];
        for(int i=0;i<activePills.length;i++)
            targetNodeIndices[i]=activePills[i];
        for(int i=0;i<activePowerPills.length;i++)
            targetNodeIndices[activePills.length+i]=activePowerPills[i];
        //Eat pills state 3
        if(targetNodeIndices.length != 0)
            return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getClosestNodeIndexFromNodeIndex(current,targetNodeIndices, DM.PATH), DM.PATH);
        //Need to create a clever way to search around
        //Search state 4
        MOVE last = game.getPacmanLastMoveMade();
        MOVE[] allMoves=MOVE.values();
        //RANDOM MOVE **FIX IF STUCK
        if(targetNodeIndices.length == 0) {
            Random rnd = new Random();
            MOVE x =allMoves[rnd.nextInt(allMoves.length)];
            System.out.println(x);
            return x;
        }
        else
            return MOVE.NEUTRAL;
    }

    private boolean isghostnear(Game game){
        int current = game.getPacmanCurrentNodeIndex();
        for(GHOST ghost : GHOST.values())
            if(game.getGhostEdibleTime(ghost)==0)
                if(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
                    return true;
            return false;
    }
    private boolean state2(Game game){
        return false;
    }
    private boolean state3(Game game){
        return false;
    }
    private boolean state4(Game game){
        return false;
    }
}