package lawsonkight.rolldice;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class PlayGame extends Activity {

    private static final int NUMBER_OF_PLAYERS = 2;
    private static final int NUMBER_OF_DICE = 2;
    private static final int NUMBER_OF_SIDES = 6;

    private static final List<Integer> POINT_ID = Collections.unmodifiableList(Arrays.asList(
            R.id.bar,
            R.id.point_1,
            R.id.point_2,
            R.id.point_3,
            R.id.point_4,
            R.id.point_5,
            R.id.point_6,
            R.id.point_7,
            R.id.point_8,
            R.id.point_9,
            R.id.point_10,
            R.id.point_11,
            R.id.point_12,
            R.id.point_13,
            R.id.point_14,
            R.id.point_15,
            R.id.point_16,
            R.id.point_17,
            R.id.point_18,
            R.id.point_19,
            R.id.point_20,
            R.id.point_21,
            R.id.point_22,
            R.id.point_23,
            R.id.point_24,
            R.id.bar
    ));
    /*private static final List<Integer> HOME_CHECKER_ID = Collections.unmodifiableList(Arrays.asList(
            R.id.home_1,
            R.id.home_2,
            R.id.home_3,
            R.id.home_4,
            R.id.home_5,
            R.id.home_6,
            R.id.home_7,
            R.id.home_8,
            R.id.home_9,
            R.id.home_10,
            R.id.home_11,
            R.id.home_12,
            R.id.home_13,
            R.id.home_14,
            R.id.home_15
    ));
    private static final List<Integer> AWAY_CHECKER_ID = Collections.unmodifiableList(Arrays.asList(
            R.id.away_1,
            R.id.away_2,
            R.id.away_3,
            R.id.away_4,
            R.id.away_5,
            R.id.away_6,
            R.id.away_7,
            R.id.away_8,
            R.id.away_9,
            R.id.away_10,
            R.id.away_11,
            R.id.away_12,
            R.id.away_13,
            R.id.away_14,
            R.id.away_15
    ));*/

    //private int[] gameBoardArray = {0,2,0,0,0,0,-5,0,-3,0,0,0,5,-5,0,0,0,3,0,5,0,0,0,0,-2,0}; todo reverse gameboard representation & math
    private int[] gameBoardArray = {0,-2,0,0,0,0,5,0,3,0,0,0,-5,5,0,0,0,-3,0,-5,0,0,0,0,2,0};

    private List<Integer> freeMoves = new ArrayList<Integer>();
    //private List<Integer> myLegalStartPoints = new ArrayList<Integer>();
    //private List<Integer> myLegalEndPoints = new ArrayList<Integer>();

    private TextView playerNameTextView;
    private String[] playerNameString = new String[NUMBER_OF_PLAYERS];

    private Button rollDiceButton;

    private TextView[] dieTextView = new TextView[NUMBER_OF_DICE];

    private boolean isHome = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        rollDiceButton = (Button) findViewById(R.id.button_roll_dice);

        dieTextView[0] = (TextView) findViewById(R.id.die_text_view1);
        dieTextView[1] = (TextView) findViewById(R.id.die_text_view2);

        createPlayers();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final class myTouchListener implements View.OnTouchListener {

        public boolean onTouch(View view, MotionEvent motionEvent) {

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                clearColorFilters();

                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);

                // Color legal end points
                View startView = (View) view.getParent();
                int startPoint = getStartPoint(startView.getId());
                List<Integer> myLegalEndPoints = getLegalEndPoints(startPoint);
                for (int aLegalEndPoint : myLegalEndPoints) {
                    int pointId = POINT_ID.get(aLegalEndPoint);
                    View v = findViewById(pointId);
                    v.setOnDragListener(new myDragListener());
                    v.getBackground().setColorFilter(0xFF0000FF, PorterDuff.Mode.SRC);
                }

                return true;

            } else return false;

        }

    }

    private int getStartPoint(int i) {
        int startPoint = POINT_ID.indexOf(i);
        if (startPoint == 0 && isHome) startPoint = 25;
        return startPoint;
    }

    private void setLegalStartPoints() {

        clearColorFilters();

        List<Integer> legalStartPoints = getLegalStartPoints();

        if (legalStartPoints.size() == 0) {
            nextTurn(); // todo this should actually just wait for player to pick up dice
            return;
        }

        int bar = isHome ? 25 : 0;

        if (legalStartPoints.get(0) == bar) {

            View v = findViewById(POINT_ID.get(bar));
            // todo bar does not have background drawable
            v.getBackground().setColorFilter(0xFF00FFFF, PorterDuff.Mode.SRC);
            setCheckerTouchListeners(v, new myTouchListener());

            return;

        }

        for (int i : legalStartPoints) {

            View v = findViewById(POINT_ID.get(i));

            if (isMyPoint(i) && hasLegalEndPoints(i)) {
                v.getBackground().setColorFilter(0xFF00FFFF, PorterDuff.Mode.SRC);
                setCheckerTouchListeners(v, new myTouchListener());
            }

        }

    }

    private void setCheckerTouchListeners(View v, myTouchListener touchListener) {

        ViewGroup vg = (ViewGroup) v;

        for (int j = 0; j < vg.getChildCount(); j++) {
            View nextChild = vg.getChildAt(j);
            nextChild.setOnTouchListener(touchListener);
        }

    }

    private void clearColorFilters() {
        for (int i = 0; i <= 25; ++i) clearColorFilters(findViewById(POINT_ID.get(i)));
    }

    private void clearColorFilters(View v) {
        v.setOnDragListener(null);
        v.getBackground().clearColorFilter();
        setCheckerTouchListeners(v, null);
    }

    private boolean hasLegalEndPoints(int pointId) {
        for(int i : freeMoves) {
            if (isLegalMove(pointId, i)) return true;
        }
        return false;
    }

    private List<Integer> getLegalEndPoints(int pointId) {

        List<Integer> myLegalEndPoints = new ArrayList<Integer>();

        if (freeMoves.size() == 0) {

            return myLegalEndPoints;

        } else if (freeMoves.size() == 2) {

            int[] move = new int[2];
            int[] endPoint = new int[2];

            for (int i = 0; i < 2; i++) {
                move[i] = freeMoves.get(i);
                endPoint[i] = pointId - move[i];
                if (isLegalMove(pointId, move[i]))
                    myLegalEndPoints.add(pointId - move[i]);
            }

            int combinedMove = pointId - (move[0] + move[1]);
            if (!isOpponentBlot(endPoint[0]) && !isOpponentBlot(endPoint[1]) && !isOpponentPoint(combinedMove))
                myLegalEndPoints.add(combinedMove);

            // todo if only two endPoints exist with only a single start checker for both, eliminate smaller endPoint

        } else {

            // todo break if not enough pieces exist to use all doubles (especially when bearing off)

            for (int i = 1; i <= freeMoves.size(); ++i) {
                int combinedRoll = freeMoves.get(0) * i;

                if (isLegalMove(pointId, combinedRoll)) {

                    myLegalEndPoints.add(pointId - combinedRoll);

                } else break;

            }

        }

        return myLegalEndPoints;

    }

    private boolean isMyPoint(int i) {
        return (!(i < 1 || i > 24)) && (isHome && gameBoardArray[i] > 0 || !isHome && gameBoardArray[i] < 0);
    }

    private boolean isOpponentPoint(int i) {
        return (!(i < 1 || i > 24)) && (isHome && gameBoardArray[i] < 0 || !isHome && gameBoardArray[i] > 0);
    }

    private boolean isLegalMove(int startPoint, int currentMove) {

        int endPoint = startPoint - currentMove;

        if(endPoint > 0 && endPoint < 25) {

            int temp = gameBoardArray[endPoint];
            return isMyPoint(endPoint) || temp > -2 && temp < 2;

        } else {

            int currentBar = isHome ? 25 : 0;
            int playDirection = isHome ? 1 : -1;

            for (int i = startPoint - playDirection; i <= currentBar; i += playDirection)
                if (isMyPoint(i)) return false;

            return hasAllCheckersHome();

        }

    }

    private boolean hasAllCheckersHome() {

        int[] nonHomeRange = isHome ? new int[]{7, 25} : new int[]{0, 18};

        for (int i = nonHomeRange[0]; i < nonHomeRange[1]; ++i)
            if (isMyPoint(i)) return false;

        return true;
    }

    private void requestMove(View checkerView, View endView) {

        View startView = (View) checkerView.getParent();
        int startPoint = getStartPoint(startView.getId());
        int endPoint = POINT_ID.indexOf(endView.getId());

        requestMove(checkerView, startView, endView, startPoint, endPoint);

    }

    private void requestMove(int startPoint, int endPoint) {

        View startView = findViewById(POINT_ID.get(startPoint));
        View endView = findViewById(POINT_ID.get(endPoint));
        View checkerView = ((ViewGroup) startView).getChildAt(0);

        requestMove(checkerView, startView, endView, startPoint, endPoint);

    }

    private boolean isOpponentBlot(int point) {
        return (!(point < 1 || point > 24)) && (isHome ? gameBoardArray[point] == -1 : gameBoardArray[point] == 1);
    }

    private void requestMove(View checkerView, View startView, View endView, int startPoint, int endPoint) {

        if (!isLegalMove(startPoint, startPoint - endPoint)) return;

        // check for capture
        if (isOpponentBlot(endPoint)) {

            int opponentBar = isHome ? 0 : 25;

            View capturedChecker = ((ViewGroup) endView).getChildAt(0);
            View barView = findViewById(POINT_ID.get(opponentBar));

            makeMove(capturedChecker, endView, barView, endPoint, opponentBar);

        }

        makeMove(checkerView, startView, endView, startPoint, endPoint);

        // update remaining moves
        Integer moveDistance = startPoint - endPoint;
        if (!freeMoves.remove(moveDistance)) {
            int i = freeMoves.remove(0) + freeMoves.remove(0);
            while (i != moveDistance) i += freeMoves.remove(0);
        }

        // update board
        if (freeMoves.isEmpty()) {
            nextTurn();
        } else {
            setLegalStartPoints();
        }

    }

    private void makeMove(View checkerView, View startView, View endView, int startPoint, int endPoint) {

        // move data
        int currentPlayer = isHome ? 1 : -1;
        if (endPoint == 0 || endPoint == 25) currentPlayer *= -1;
        gameBoardArray[endPoint] += currentPlayer;
        gameBoardArray[startPoint] -= currentPlayer;

        // move graphics
        ViewGroup owner = (ViewGroup) startView;
        owner.removeView(checkerView);
        LinearLayout container = (LinearLayout) endView;
        container.addView(checkerView);
        checkerView.setVisibility(View.VISIBLE);

    }

    class myDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();

            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.SRC);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.getBackground().setColorFilter(0xFF0000FF, PorterDuff.Mode.SRC);
                    break;
                case DragEvent.ACTION_DROP:
                    View dropView = (View) event.getLocalState();
                    //moveChecker(dropView, v);
                    requestMove(dropView, v);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    View endView = (View) event.getLocalState();
                    endView.setVisibility(View.VISIBLE);
                    setLegalStartPoints();
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    private void createPlayers() {

        Intent intent = getIntent();
        playerNameString = intent.getStringArrayExtra(MainActivity.EXTRA_PLAYER_NAME);

        playerNameTextView = (TextView) findViewById(R.id.player_home_header);
        playerNameTextView.setText(playerNameString[0]);

    }

    public void rollDiceClick(View view) {

        // todo invoke on swipe

        freeMoves.clear();
        Random r = new Random();

        for(int i = 0; i < NUMBER_OF_DICE; ++i) {

            int myRoll = r.nextInt(NUMBER_OF_SIDES) + 1;
            freeMoves.add(myRoll * (isHome ? 1 : -1));

            dieTextView[i].setText(String.valueOf(myRoll));

        }

        //List<Integer> myLegalStartPoints = getLegalStartPoints();

        if (freeMoves.get(0).equals(freeMoves.get(1))) freeMoves.addAll(freeMoves);

        setLegalStartPoints();

        rollDiceButton.setEnabled(false);

    }

    public int[] rollDice() {
        Random r = new Random();
        return new int[]{r.nextInt(6) + 1, r.nextInt(6) + 1};
    }

    public List<Integer> buildFreeMoves(int[] myRoll) {
        List<Integer> myFreeMoves = new ArrayList<Integer>();

        //freeMoves.add(myRoll * (isHome ? 1 : -1));
        if (freeMoves.get(0).equals(freeMoves.get(1))) freeMoves.addAll(freeMoves);

        List<Integer> myLegalStartPoints = getLegalStartPoints();
        List<Integer> myLegalEndPoints = new ArrayList<Integer>();



        return myFreeMoves;
    }

    public List<Integer> getLegalStartPoints() {

        List<Integer> myLegalStartPoints = new ArrayList<Integer>();

        if (isCaptured()) {
            int bar = isHome ? 25 : 0;
            if (hasLegalEndPoints(bar))
                myLegalStartPoints.add(bar);
            return myLegalStartPoints;
        }

        for (int i = 1; i < 25; i++)
            if (hasLegalEndPoints(i))
                myLegalStartPoints.add(i);

        return myLegalStartPoints;

    }

    public boolean isCaptured() {
        return isHome && gameBoardArray[25] > 0 || !isHome && gameBoardArray[0] < 0;
    }

    private void nextTurn() {

        clearColorFilters();
        isHome = !isHome;

        if (isCaptured()) {

            boolean canEnterBoard = false;
            int opponentHomeStart = isHome ? 19 : 1;
            int opponentHomeEnd = isHome ? 24 : 6;

            for (int i = opponentHomeStart; i <= opponentHomeEnd; i++) {
                if (!isOpponentPoint(i)) {
                    canEnterBoard = true;
                    break;
                }
            }

            if (!canEnterBoard) isHome = !isHome;

        }

        for(TextView tv : dieTextView) tv.setText("");
        rollDiceButton.setEnabled(true);

        playerNameTextView.setText(playerNameString[isHome ? 0 : 1]);
        CharSequence cs = playerNameTextView.getText() + "'s Turn";
        showToast(cs);

    }

    private void showToast(CharSequence cs) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, cs, duration);
        toast.show();
    }

}