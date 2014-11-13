package lawsonkight.rolldice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends Activity implements View.OnClickListener {

    EditText homePlayerNameEditText;
    EditText awayPlayerNameEditText;

    Button startGameButton;

    public final static String EXTRA_PLAYER_NAME = "com.rolldice.PLAYER_NAMES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startGameButton = (Button) findViewById(R.id.button_start_game);
        startGameButton.setOnClickListener(this);

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

    @Override
    public void onClick(View view) {

        Intent intent = new Intent(this, PlayGame.class);

        homePlayerNameEditText = (EditText) findViewById(R.id.home_player_name);
        awayPlayerNameEditText = (EditText) findViewById(R.id.away_player_name);

        String playerName[] = {
            homePlayerNameEditText.getText().toString(),
            awayPlayerNameEditText.getText().toString()
        };

        for (int i = 0; i < playerName.length; i++) {
            if (playerName[i].isEmpty()) playerName[i] = "PLAYER " + (i + 1);
        }

        intent.putExtra(EXTRA_PLAYER_NAME, playerName);

        startActivity(intent);

    }

}