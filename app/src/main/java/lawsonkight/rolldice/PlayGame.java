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

    private boolean containsDraggable = false;

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
    private static final List<Integer> HOME_CHECKER_ID = Collections.unmodifiableList(Arrays.asList(
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
    ));

    private ArrayList<ArrayList<Boolean>> gameBoardArrayList;
    //private int[] gameBoardArray = {0,2,0,0,0,0,-5,0,-3,0,0,0,5,-5,0,0,0,3,0,5,0,0,0,0,-2,0}; todo reverse gameboard representation & math
    private int[] gameBoardArray = {0,-2,0,0,0,0,5,0,3,0,0,0,-5,5,0,0,0,-3,0,-5,0,0,0,0,2,0};

    private List<Integer> freeMoves = new ArrayList<Integer>();
    private List<Integer> myLegalStartPoints = new ArrayList<Integer>();
    private List<Integer> myLegalEndPoints = new ArrayList<Integer>();

    private TextView playerNameTextView;
    private String[] playerNameString = new String[NUMBER_OF_PLAYERS];

    private Button rollDiceButton;
    private Button movePieceButton;

    private TextView[] dieTextView = new TextView[NUMBER_OF_DICE];

    TextView gameBoardTextView;

    private boolean isHome = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        rollDiceButton = (Button) findViewById(R.id.button_roll_dice);
        movePieceButton = (Button) findViewById(R.id.button_move_piece);
        movePieceButton.setEnabled(false);

        gameBoardTextView = (TextView) findViewById(R.id.game_board_text_view);

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

                View parentView = (View) view.getParent();
                int startPoint = POINT_ID.indexOf(parentView.getId());

                // Color legal end points
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

    private void setLegalStartPoints() {

        if (isHome && gameBoardArray[25] > 0 || !isHome && gameBoardArray[0] > 0) {

            View v = findViewById(POINT_ID.get(isHome ? 25 : 0));
            v.getBackground().setColorFilter(0xFF00FFFF, PorterDuff.Mode.SRC);

            setCheckerTouchListener(v, new myTouchListener());

            /*for (int j = 0; j < ((ViewGroup) v).getChildCount(); j++) {
                View nextChild = ((ViewGroup)v).getChildAt(j);
                nextChild.setOnTouchListener(new myTouchListener());
            }*/

        } else {

            for (int i = 1; i <= 24; ++i) {

                View v = findViewById(POINT_ID.get(i));

                if (isMyPoint(i) && !getLegalEndPoints(i).isEmpty()) {

                    v.getBackground().setColorFilter(0xFF00FFFF, PorterDuff.Mode.SRC);

                    setCheckerTouchListener(v, new myTouchListener());

                    /*for (int j = 0; j < ((ViewGroup) v).getChildCount(); j++) {
                        View nextChild = ((ViewGroup) v).getChildAt(j);
                        nextChild.setOnTouchListener(new myTouchListener());
                    }*/

                    myTouchListener x = new myTouchListener();

                } else {

                    v.setOnDragListener(null);
                    v.getBackground().clearColorFilter();

                    setCheckerTouchListener(v, null);

                    /*for (int j = 0; j < ((ViewGroup) v).getChildCount(); j++) {
                        View nextChild = ((ViewGroup) v).getChildAt(j);
                        nextChild.setOnTouchListener(null);
                    }*/

                }

            }

        }

    }

    private void setCheckerTouchListener(View v, myTouchListener touchListener) {

        ViewGroup vg = (ViewGroup) v;

        for (int j = 0; j < vg.getChildCount(); j++) {
            View nextChild = vg.getChildAt(j);
            nextChild.setOnTouchListener(touchListener);
        }

    }

    private void clearColorFilters() {

        for (int i = 1; i <= 24; ++i) {
            View v = findViewById(POINT_ID.get(i));
            v.setOnDragListener(null);
            v.getBackground().clearColorFilter();

            for (int j = 0; j < ((ViewGroup) v).getChildCount(); j++) {
                View nextChild = ((ViewGroup)v).getChildAt(j);
                nextChild.getBackground().clearColorFilter();
                nextChild.setOnTouchListener(null);
            }

        }

    }

    private void setLegalMoves() {

        for (int i = 0; i <= 25; ++i) {

            int absMultiplier = isHome ? 1: -1;

            if (gameBoardArray[i] == 0) continue;

            if (isMyPoint(i) && !getLegalEndPoints(i).isEmpty()) {
                // I own, and have legal options

                myLegalStartPoints.add(i);

                for (int j = 0; j < gameBoardArray[i] * absMultiplier; j++) {
                    // do something for each of my checkers
                }

            } else if (gameBoardArray[i] * absMultiplier < 2) {

                //myOpponentPoints.add(i);

            } else {

                //myOpponentBlots.add(i);

            }

        }

    }

    private List<Integer> getLegalEndPoints(int pointId) {

        List<Integer> myLegalEndPoints = new ArrayList<Integer>();

        if (freeMoves.size() == 0) return myLegalEndPoints;

        if (freeMoves.size() == 2) {

            int combinedRoll = freeMoves.get(0) + freeMoves.get(1);

            for (int i = 0; i < 2; ++i) {

                // todo break if (capture for move1 || move 2) && move1 != move2

                if (isLegalMove(pointId, freeMoves.get(i))) {

                    myLegalEndPoints.add(pointId - freeMoves.get(i));

                    if (isLegalMove(pointId, combinedRoll) && !myLegalEndPoints.contains(pointId - combinedRoll))
                        myLegalEndPoints.add(pointId - combinedRoll);

                }

                // todo if only two endPoints exist with only a single start checker for both, eliminate smaller endPoint

            }

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
        if (i < 0 || i > 24) return false;
        i = gameBoardArray[i];
        return (isHome && i > 0 || !isHome && i < 0);
    }

    private boolean isLegalMove(int startPoint, int currentMove) {

        int endPoint = startPoint - currentMove;

        if(endPoint > 0 && endPoint < 25) {

            int temp = gameBoardArray[endPoint];
            return isMyPoint(endPoint) || temp > -2 && temp < 2;

        } else {

            int currentBar = isHome ? 25 : 0;
            int playDirection = isHome ? 1 : -1;

            for (int i = startPoint + playDirection; i <= currentBar; i += playDirection)
                if (isMyPoint(i)) return false;

            return hasAllCheckersHome();

        }

    }

    private boolean hasAllCheckersHome() {

        final int[] nonHomeRange = isHome ? new int[]{7, 25} : new int[]{0, 18};

        for (int i = nonHomeRange[0]; i < nonHomeRange[1]; ++i)
            if (isMyPoint(i)) return false;

        return true;

    }

    private void requestMove(View checkerView, View endView) {

        View startView = (View) checkerView.getParent();
        int startPoint = POINT_ID.indexOf(startView.getId());
        int endPoint = POINT_ID.indexOf(endView.getId());

        requestMove(checkerView, startView, endView, startPoint, endPoint);

    }

    private void requestMove(int startPoint, int endPoint) {

        View startView = findViewById(POINT_ID.get(startPoint));
        View endView = findViewById(POINT_ID.get(endPoint));
        View checkerView = ((ViewGroup) startView).getChildAt(0);

        requestMove(checkerView, startView, endView, startPoint, endPoint);

    }

    private void requestMove(View checkerView, View startView, View endView, int startPoint, int endPoint) {

        if (!isLegalMove(startPoint, startPoint - endPoint)) return;

        if (gameBoardArray[endPoint] != 0 && !isMyPoint(endPoint)) {

            int opponentBar = isHome ? 25 : 0;

            View capturedChecker = ((ViewGroup) endView).getChildAt(0);
            View barView = findViewById(POINT_ID.get(opponentBar));

            makeMove(capturedChecker, endView, barView, endPoint, opponentBar);

        }

        makeMove(checkerView, startView, endView, startPoint, endPoint);

        // todo allow undo
        Integer moveDistance = startPoint - endPoint;
        if (!freeMoves.remove(moveDistance)) {
            int i = freeMoves.remove(0) + freeMoves.remove(0);
            while (i != moveDistance) i += freeMoves.remove(0);
        }

        if (freeMoves.isEmpty()) nextTurn();

        setLegalStartPoints();

    }

    private void makeMove(View checkerView, View startView, View endView, int startPoint, int endPoint) {

        // move data
        int currentDirection = isHome ? 1 : -1;
        gameBoardArray[endPoint] += currentDirection;
        gameBoardArray[startPoint] -= currentDirection;
        setGameBoardText(); // todo remove?

        // move graphics
        ViewGroup owner = (ViewGroup) startView;
        owner.removeView(checkerView);
        LinearLayout container = (LinearLayout) endView;
        container.addView(checkerView);
        checkerView.setVisibility(View.VISIBLE);

    }

    private void setGameBoardText() {

        String temp = "{";
        for (int i : gameBoardArray) temp += i + ",";
        temp += "}";

        gameBoardTextView.setText(temp);

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

        if (freeMoves.get(0).equals(freeMoves.get(1))) freeMoves.addAll(freeMoves);

        setLegalStartPoints();

        rollDiceButton.setEnabled(false);
        movePieceButton.setEnabled(true);

    }

    public void movePieceClick(View view) {

        // todo eliminate?

        TextView checkerStartLocationTextView = (TextView) findViewById(R.id.checker_start_location);
        TextView checkerEndLocationTextView = (TextView) findViewById(R.id.checker_end_location);

        String checkerStartLocationString = checkerStartLocationTextView.getText().toString();
        String checkerEndLocationString = checkerEndLocationTextView.getText().toString();

        int checkerStart = 0;
        int checkerEnd = 0;

        // checkerEndLocationTextView.addTextChangedListener(watch);

        if(checkerStartLocationString.matches("\\d+")) {
            checkerStart = Integer.parseInt(checkerStartLocationString);
        }
        if(checkerEndLocationString.matches("\\d+")) {
            checkerEnd = Integer.parseInt(checkerEndLocationString);
        }

        if (checkerStart > 0 && checkerStart < 25 && checkerEnd >= 0 && checkerEnd <= 25) {
            //moveChecker(checkerStart, checkerEnd);
            requestMove(checkerStart, checkerEnd);
        }

    }

    private void nextTurn() {

        isHome = !isHome;
        int currentPlayer = isHome ? 0 : 1;

        rollDiceButton.setEnabled(true);
        movePieceButton.setEnabled(false);

        playerNameTextView.setText(playerNameString[currentPlayer]);
        for(TextView tv : dieTextView) tv.setText("");

        Context context = getApplicationContext();

        CharSequence text = playerNameString[currentPlayer] + "'s Turn";

        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }

}