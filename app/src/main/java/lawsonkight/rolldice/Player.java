package lawsonkight.rolldice;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player {
    //private Checker[] myCheckers;
    private String playerName;
    private Die[] myDie;
    private int[] myDice;
    private List<Integer> myMoves;
    private boolean isHome;
    private int isHomeMultiplier;
    private static final int NUMBER_OF_DICE = 2;
    private static final int NUMBER_OF_SIDES = 6;

    /*
    public Player(String startPlayerName, Die[] startDice, Checker[] startCheckers, boolean startIsHome) {
        playerName = startPlayerName;
        myDie = startDice;
        myCheckers = startCheckers;
        myMoves = new ArrayList<Integer>();
        isHome = startIsHome;
        isHomeMultiplier = (isHome ? 1 : -1);
    }
    */

    public Player(String startPlayerName, boolean startIsHome) {
        playerName = startPlayerName;
        //myDie[0] = new Die(NUMBER_OF_SIDES);
        //myDie[1] = new Die(NUMBER_OF_SIDES);
        //myCheckers = startCheckers;
        myMoves = new ArrayList<Integer>();
        isHome = startIsHome;
        isHomeMultiplier = (isHome ? 1 : -1);
        myDice = new int[NUMBER_OF_DICE];
    }

    public String getPlayerName() {
        return playerName;
    }

    /*public void setMyCheckers(Checker[] startMyCheckers) {
        myCheckers = startMyCheckers;
    }

    public Checker[] getMyCheckers() {
        return myCheckers;
    }

    public void setDice(Die[] newDice) {
        myDie = newDice;
    }*/

    public List<Integer> getDice() {

        List<Integer> myDice = new ArrayList<Integer>(2);

        for (Die currentDie : myDie) {
            myDice.add(currentDie.getValue());
        }

        return myDice;
    }

    /* public void rollDice() {

        for (Die currentDie : myDie) currentDie.rollDie();

        myMoves.clear();

        for (Die currentDie : myDie) {
            myMoves.add(currentDie.getValue() * isHomeMultiplier);
        }

        if (myMoves.get(0).equals(myMoves.get(1))) {
            myMoves.addAll(myMoves);
        }

    }*/

    public int[] rollDice() {

        //final int NUMBER_OF_SIDES = 6;

        //List<Integer> myRoll = new ArrayList<Integer>();
        //int[] myRoll = new int[NUMBER_OF_DICE];

        Random r = new Random();

        myMoves.clear();

        for(int i = 0; i < NUMBER_OF_DICE; i++) {

            myDice[i] = r.nextInt(NUMBER_OF_SIDES) + 1;
            myMoves.add(myDice[i] * isHomeMultiplier);

        }

        if (myDice[0] == myDice[1]) myMoves.addAll(myMoves);

        return myDice;

    }

    public List<Integer> getMoves() {
        return myMoves;
    }

}