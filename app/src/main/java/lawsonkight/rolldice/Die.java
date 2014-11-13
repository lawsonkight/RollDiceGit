package lawsonkight.rolldice;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Die {
    private int numberOfSides;
    private int value;
    private TextView myTextView;
    private static final int NUMBER_OF_SIDES = 6;

    /*
    public int Die(int startNumberOfSides) {
        numberOfSides = startNumberOfSides;
    }
    */

    public List<Integer> Die(int numberOfDice) {

        Random r = new Random();
        List<Integer> myRolls = new ArrayList<Integer>();

        for(int i = 0; i < numberOfDice; i++) {

            myRolls.add(r.nextInt(NUMBER_OF_SIDES) + 1);

        }

        return myRolls;

    }

    public void setTextView(TextView newTextView) {
        myTextView = newTextView;
    }

    public int getValue() {
        return value;
    }

    public void rollDie() {

        Random r = new Random();
        value = r.nextInt(numberOfSides)+1;

        // myTextView.setText(Integer.toString(value));

    }

}