package examples.StarterPacMan;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Maze;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static pacman.game.Constants.DM;
import static pacman.game.Constants.GHOST;
/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.username).
 */
public class MyPacMan extends Controller<MOVE> {
    private int current;
    private static final int MIN_DISTANCE = 20;
    private int[] targetNodeIndices;
    private GHOST ghost;
    private int [] open = new int [0];
    private int [] closed = new int [0];
    private int state0 = 0;
    @Override
    public MOVE getMove(Game game, long timeDue) {
        //Get Current Position
        current = game.getPacmanCurrentNodeIndex();

        if(state0 == 0) {
            open = game.getActivePillsIndices();
            state0 = 1;
        }

        open = removeTheElement(open, current);
        closed = addElement(closed, current);
        ghost = is_ghost_near(game);
        if(ghost != null) {
            System.out.println("Running away from Ghost");
            return game.getNextMoveAwayFromTarget(current, game.getGhostCurrentNodeIndex(ghost), DM.PATH);
        }
        //Chase state 2 - Check if a ghost is near while a power pill is active and chase it
        ghost = edible_ghost_near(game);
        if(ghost != null) {
            System.out.println("Chasing ghost");
            return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost), DM.PATH);
        }
        //Chase pills state 3 - If active pills are near, go after them, remove the pill to be eaten from the open list.
        boolean find = findpills(game);
        if(find == true) {
            System.out.println("Eating nearby pills");
            return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getClosestNodeIndexFromNodeIndex(current, targetNodeIndices, DM.PATH), DM.PATH);
        }
        //Search state 4 - If no active pills are near, check the open list from previously seen pills that weren't eaten yet
        if(targetNodeIndices.length == 0 && open.length != 0) {
            System.out.println("Recovering from memory");
            return game.getApproximateNextMoveTowardsTarget(current,game.getClosestNodeIndexFromNodeIndex(current, open, DM.PATH), game.getPacmanLastMoveMade(), DM.PATH);
        }
        //Fail-safe move
        else {
            System.out.println("Recovering from 'memory'");
            open = game.getPillIndices();
            return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getClosestNodeIndexFromNodeIndex(current, closed, DM.PATH), DM.PATH);
        }
    }
    //Check condition to transition to state 1
    private GHOST is_ghost_near(Game game){
        for(GHOST ghost : GHOST.values())
            if(game.getGhostEdibleTime(ghost)==0)
                if(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
                    return ghost;
        return null;
    }
    //Check condition to transition to state 2
    private GHOST edible_ghost_near(Game game){
        for(GHOST ghost : GHOST.values())
            if(game.getGhostEdibleTime(ghost) > 0)
                return ghost;
        return null;
    }
    //Check condition to transition to state 3
    private boolean findpills(Game game){
        int[] activePills=game.getActivePillsIndices();
        //get all active power pills in range
        int[] activePowerPills=game.getActivePowerPillsIndices();
        //create a target array that includes all  pills and power pills
        targetNodeIndices=new int[activePills.length+activePowerPills.length];
        for(int i=0;i<activePills.length;i++)
            targetNodeIndices[i]=activePills[i];
        for(int i=0;i<activePowerPills.length;i++)
            targetNodeIndices[activePills.length+i]=activePowerPills[i];
        if(targetNodeIndices.length != 0)
            return true;
        return false;
    }

    //Helper functions to create the open list for state 4
    private int[] addElement(int[] arr, int toCheckValue) {
        if (arr.length == 0){
            arr  = Arrays.copyOf(arr,arr.length + 1);
            arr[arr.length - 1] = toCheckValue;
        }
        boolean test = false;
        for(int i=0;i<arr.length;i++) {
            if (arr[i] == toCheckValue) {
                test = true;
                break;
            }
        }

        if (test == true){
            arr  = Arrays.copyOf(arr,arr.length + 1);
            arr[arr.length - 1] = toCheckValue;
        }
        return arr;
    }

    private int[] removeTheElement(int[] arr,
                                   int index)
    {
        if (arr == null
                || index < 0
                || index >= arr.length) {
            return arr;
        }
        // Create another array of size one less
        int[] anotherArray = new int[arr.length - 1];
        System.arraycopy(arr, 0, anotherArray, 0, index);
        System.arraycopy(arr, index + 1,
                anotherArray, index,
                arr.length - index - 1);
        return anotherArray;
    }

}