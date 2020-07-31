package com.example.diceout;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class MainActivity extends AppCompatActivity {
    // Field to hold the roll result text
    TextView rollResult;

    // Field to hold the roll button
    Button rollButton;

    // Field to hold the score
    int score;

    // Field to hold the score
    Random rand;

    // List to hold all die
    static final int DICE_COUNT=3;
    List<Die> dice;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Field initialization here
        score = 0;
        rollResult = (TextView) findViewById(R.id.rollResult);
        rollButton = (Button) findViewById(R.id.rollButton);

       dice= IntStream.range(1, DICE_COUNT+1).mapToObj(diceIndex ->
                {
                    int id = getResources().getIdentifier("die"+diceIndex+"Image","id", getPackageName());
                    return Die.create(diceIndex, (ImageView) findViewById(id));
                }
                ).collect(toList());

        Toast.makeText(getApplicationContext(), "Welcome to DiceOut!", Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void rollDice(View v){
        rollResult.setText("Clicked!");

        // Set dice values into an ArrayList
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("You rolled");
        IntStream.range(0, DICE_COUNT).forEachOrdered(dieIndex -> {
            String formatString;
            if(dieIndex == 0){
                formatString = "a %d";
            } else if(dieIndex == DICE_COUNT-1){
                formatString = ", and a %d.";
            } else {
                formatString = ", a %d";
            }
            messageBuilder.append(String.format(formatString, dice.get(dieIndex).getRoll()));
        });

        // Update the app to display the result value
        rollResult.setText(messageBuilder.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class Die {
        private static final int MAX_DIE_VALUE = 6;
        private static Random rand = new Random(MAX_DIE_VALUE);

        int currentValue;
        String name;
        ImageView imageView;

        public Die(String name, ImageView imageView) {
            this.name = name;
            this.imageView = imageView;
        }

        public static Die create(int diceIndex, ImageView imageView) {
            return new Die("die"+diceIndex, imageView);
        }

        public int getRoll() {
            currentValue = rand.nextInt(MAX_DIE_VALUE+1);
            return currentValue;
        }

        public String getName() {
            return name;
        }
    }
}
