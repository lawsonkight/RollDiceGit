package lawsonkight.rolldice;

import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameBoard {
    private ArrayList<ArrayList<Boolean>> gameBoardArrayList;
    private TextView myTextView;

    private static final int SIZE_OF_BOARD = 26;
    private static final int NUMBER_OF_CHECKERS = 15;
    private static final int[] HOME_START_POSITION = {6,6,6,6,6, 8,8,8, 13,13,13,13,13, 24,24};
    private static final int[] AWAY_START_POSITION = {19,19,19,19,19, 17,17,17, 12,12,12,12,12, 1,1};

    public GameBoard(TextView startTextView) {

        myTextView = startTextView;

        gameBoardArrayList = new ArrayList<ArrayList<Boolean>>();
        for(int i = 0; i < SIZE_OF_BOARD; i++) {
            gameBoardArrayList.add(new ArrayList<Boolean>());
        }

        for (int i = 0; i < NUMBER_OF_CHECKERS; i++) {

            gameBoardArrayList.get(HOME_START_POSITION[i])
                    .add(true);

            gameBoardArrayList.get(AWAY_START_POSITION[i])
                    .add(false);

        }

        setGameBoardText();

    }

    public boolean getPointOwner(final int point) {
        Log.d("Roll Dice", "point: " + point);
        //assert !gameBoardArrayList.get(point).isEmpty() : "TEST";
        if (BuildConfig.DEBUG && gameBoardArrayList.get(point).isEmpty()) throw new AssertionError();
        return gameBoardArrayList.get(point).get(0);
    }

    public void moveChecker(final int startPosition, final int endPosition) {

        if (!gameBoardArrayList.get(startPosition).isEmpty()) {

            boolean myChecker = gameBoardArrayList.get(startPosition).get(0);

            gameBoardArrayList.get(startPosition).remove(0);
            gameBoardArrayList.get(endPosition).add(myChecker);

            setGameBoardText();

        }

    }

    public ArrayList<ArrayList<Boolean>> getGameBoardArrayList() {
        return gameBoardArrayList;
    }

    public void setGameBoardArrayList(ArrayList<ArrayList<Boolean>> gameBoardArrayList) {
        this.gameBoardArrayList = gameBoardArrayList;
    }

    public void setGameBoardText() {
        myTextView.setText(gameBoardArrayList.toString());
    }

    public List<Integer> getMyPoints(boolean isHome) {

        List<Integer> myPoints = new ArrayList<Integer>();

        for (int i = 1; i <= 24; i++) {
            if (gameBoardArrayList.get(i).contains(isHome)) myPoints.add(i);
        }

        return myPoints;
    }

    public List<Integer> getLegalStartPoints(boolean currentPlayer, List<Integer> myMoves) {

        List<Integer> myLegalStartPoints = new ArrayList<Integer>();
        List<Integer> myPoints = getMyPoints(currentPlayer);

        for (int currentPoint : myPoints) {
            for (int currentMove : myMoves) {
                if (!opponentControlsPoint(currentPlayer, currentPoint - currentMove)) {
                    myLegalStartPoints.add(currentPoint);
                    break;
                }
            }
        }

        return myLegalStartPoints;

    }

    public List<ArrayList<Integer>> getLegalMoves(boolean currentPlayer, List<Integer> myMoves) {
        // Returns legal moves for a player any combination of dice

        List<Integer> currentLegalStartPoints = getLegalStartPoints(currentPlayer, myMoves);
        List<ArrayList<Integer>> myLegalMoves = new ArrayList<ArrayList<Integer>>();

        for (int currentPoint : currentLegalStartPoints) {
            myLegalMoves.addAll(getLegalMoves(currentPlayer, myMoves, currentPoint));
        }

        Log.d("Roll Dice", "LEGAL MOVES: " + myLegalMoves.toString());

        return myLegalMoves;
    }

    public List<ArrayList<Integer>> getLegalMoves(boolean currentPlayer, List<Integer> myMoves, int currentPoint) {
        // Returns legal moves for a specific point from any combination of dice

        /*
        todo current player can be determined inside method
        boolean currentPlayer = gameBoardArrayList.get(currentPoint).isEmpty() ? gameBoardArrayList.get(currentPoint).get(0) : false;
        */

        List<ArrayList<Integer>> myLegalMoves = new ArrayList<ArrayList<Integer>>();
        List<Integer> iterateMoves = myMoves;

        do {
            // determine legal moves for combination of dice
            // reverse combination on loop (if necessary)

            ArrayList<Integer> moveBuilder = new ArrayList<Integer>(Arrays.asList(currentPoint));
            int iteratePoint = currentPoint;

            for (int currentMove : iterateMoves) {
                // determine if checker can perform multiple moves at once

                if (isLegalMove(currentPlayer, iteratePoint, currentMove)) {
                    // add legal move to list and check next move (if necessary)

                    iteratePoint -= currentMove;
                    moveBuilder.add(iteratePoint);

                } else break;

            }

            // if a new move combination was created, add to master list
            if (moveBuilder.size() > 1) myLegalMoves.add(moveBuilder);

            // reverse moves
            List<Integer> tempMoves = new ArrayList<Integer>();
            for (int i = iterateMoves.size() - 1; i >= 0; i--) {
                tempMoves.add(iterateMoves.get(i));
            }
            iterateMoves = tempMoves;

        } while (!iterateMoves.equals(myMoves)); // run with reversed moves if combination hasn't already run

        //Log.d("Roll Dice", "INSIDE: " + currentPoint + ", " + myLegalMoves.toString());

        return myLegalMoves;

    }

    private boolean isLegalMove(boolean isHome, int currentPoint, int currentMove) {

        boolean currentPlayer = getPointOwner(currentPoint);

        int endPoint = currentPoint - currentMove;

        if(endPoint > 0 && endPoint <= 24) {

            // Return true if endPoint contains less than 2 opponent checkers

            return !opponentControlsPoint(currentPlayer, endPoint);

        } else if (endPoint <= 0) {

            // return true if all player 1 checkers are home and no checkers exist in a further point

            for (int i = currentPoint + 1; i <= 6; i++) {
                if (!gameBoardArrayList.get(i).isEmpty()) return false;
            }

            return hasAllCheckersHome(currentPlayer);

        } else { // if (endPoint >= 24)

            // return true if all player 2 checkers are home and no checkers exist in a further point

            for (int i = currentPoint - 1; i <= 19; i--) {
                if (!gameBoardArrayList.get(i).isEmpty()) return false;
            }

            return hasAllCheckersHome(currentPlayer);

        }

    }

    public boolean opponentControlsPoint(boolean isHome, int point) {

        List<Boolean> currentPoint = gameBoardArrayList.get(point);

        return currentPoint.contains(!isHome) && currentPoint.size() > 1;

    }

    public boolean iControlPoint(boolean isHome, int point) {
        return gameBoardArrayList.get(point).contains(isHome);
    }

    public boolean hasAllCheckersHome(boolean isHome) {

        final int[] HOME_NON_HOME_RANGE = new int[]{7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};
        final int[] AWAY_NON_HOME_RANGE = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18};

        final int[] nonHomeRange = isHome ? HOME_NON_HOME_RANGE : AWAY_NON_HOME_RANGE;

        for (int i : nonHomeRange) {
            if (gameBoardArrayList.get(i).contains(isHome)) return false;
        }

        return true;
    }

    public boolean hasCheckersInOpponentHome(boolean isHome) {

        // For determining backgammons

        final int[] HOME_HOME_TABLE = new int[]{0, 1, 2, 3, 4, 5, 6};
        final int[] AWAY_HOME_TABLE = new int[]{19, 20, 21, 22, 23, 24, 25};

        final int[] opponentHomeRange = isHome ? AWAY_HOME_TABLE : HOME_HOME_TABLE;

        for (int i : opponentHomeRange) {
            if (gameBoardArrayList.get(i).contains(isHome)) return true;
        }

        return false;

    }

}
