package com.example.diceout;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class MainActivity extends AppCompatActivity {
    static final int DICE_COUNT = 3;
    static final int MAX_FACE_VALUE = 6;

    // View Objects
    TextView viewRollResult;
    TextView viewScoreText;
    Button rollButton;

    // Model Objects
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

        viewRollResult = (TextView) findViewById(R.id.rollResult);
        viewScoreText = (TextView) findViewById(R.id.scoreText);
        rollButton = (Button) findViewById(R.id.rollButton);

        // Load all the images at once.
        List<Drawable> imageCache = IntStream.range(1, MAX_FACE_VALUE+1).mapToObj(faceValue -> loadImageFile(faceValue)).collect(toList());

        // Create the die and pass in a reference to the view they will update and the images
        // that can be updated to. Die know how to roll and update their corresponding view.
        dice = IntStream.range(1, DICE_COUNT + 1).mapToObj(
                diceIndex ->
                {
                    String resourceName = String.format("die%dImage", diceIndex);
                    return Die.create(findViewByName(resourceName), imageCache);
                }
        ).collect(toList());

        Toast.makeText(getApplicationContext(), "Welcome to DiceOut!", Toast.LENGTH_LONG).show();
    }

    private ImageView findViewByName(String resourceName) {
        int id = getResources().getIdentifier(resourceName, "id", getPackageName());
        return (ImageView) findViewById(id);
    }

    private Drawable loadImageFile(int faceValue) {
        String fileName = String.format("die_%d.png", faceValue);
        try {
            InputStream stream = getAssets().open(fileName);
            return Drawable.createFromStream(stream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Should never reach here.
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void rollDice(View v) {
        viewRollResult.setText("Clicked!");

        ArrayList<Integer> allRollValues = new ArrayList<>();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("You rolled");
        IntStream.range(0, DICE_COUNT).forEachOrdered(dieIndex -> {
            String formatString;
            if (dieIndex == 0) {
                formatString = " a %d";
            } else if (dieIndex == DICE_COUNT - 1) {
                formatString = ", and a %d.";
            } else {
                formatString = ", a %d";
            }
            int rollValue = dice.get(dieIndex).getRoll();
            allRollValues.add(rollValue);
            messageBuilder.append(String.format(formatString, rollValue));
        });

        // Update Score

        // Update the app to display the result value
        viewRollResult.setText(messageBuilder.toString());
        int scoreDelta = computeScore(allRollValues);
        String scoreMessage;
        if(scoreDelta > 0 ){
            scoreMessage = String.format("Score: %d", scoreDelta);
        } else {
            scoreMessage = "You didn't score this roll. Try again!";
        }
        Toast.makeText(getApplicationContext(), scoreMessage, Toast.LENGTH_SHORT).show();
        viewScoreText.setText(String.format("Score: %d", scoreDelta));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private int computeScore(ArrayList<Integer> allRollValues) {

        // Should probably bounds check the input array.
        Map<Integer, Long> faceValueFrequencies = allRollValues
                .stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        int sum = 0;
        for(Integer faceValue : faceValueFrequencies.keySet()){
            int frequency = faceValueFrequencies.get(faceValue).intValue();
            switch(frequency) {
                case 2:
                    sum +=  50;
                    break;
                case 3:
                    sum += faceValue *100;
                    break;
                default:
                    Log.i("scoring", "Ignoring face value because frequency is out of bounds");
                    break;
            }
        }
        return sum;
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
        private static final Random rand = new Random(MainActivity.MAX_FACE_VALUE);
        private final List<Drawable> imageCache;
        int faceValue;
        ImageView imageView;

        private Die(ImageView imageView, List<Drawable> imageCache) {
            faceValue = 1;
            this.imageView = imageView;
            this.imageCache = imageCache;
        }

        public static Die create(ImageView imageView, List<Drawable> imageCache) {
            return new Die(imageView, imageCache);
        }

        /**
         * Roll dice and update image.
         */
        public int getRoll() {
            int randInt = rand.nextInt(MainActivity.MAX_FACE_VALUE + 1);
            int imageIndex = randInt == 0 ? 1 : randInt - 1;
            imageView.setImageDrawable(imageCache.get(imageIndex));
            faceValue = imageIndex + 1;
            return faceValue;
        }
    }
}
