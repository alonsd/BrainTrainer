package com.example.alon.a2018_17_12_userloginexhomework;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    public static final String LEVEL = "level";
    //public static final int GAME_TIME = 10100;
    public static int GAME_TIME = 0;
    public static final int COUNT_DOWN_INTERVAL = 1000;
    public static final String MAX_RESULT = "max result";
    public static final String PREFS = "prefs";
    private TextView tvSum, tvResult, tvScore, tvTimer, tvGoBack;
    private ArrayList<Integer> answersList = new ArrayList<>();
    private Button button1, button2, button3, button4, button5, button6, btnPlayAgain;
    private int locationOfCorrectAnswer;
    private int score = 0;
    private int numberOfQuestions = 0;
    private int maxResult;
    public static Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initGame();


    }


    private void initGame() {
        tvSum = findViewById(R.id.tvSum);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        tvResult = findViewById(R.id.tvResult);
        tvScore = findViewById(R.id.tvScore);
        tvTimer = findViewById(R.id.tvTimer);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        tvGoBack = findViewById(R.id.tvGoBack);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                generateQuestion();
                startTimer();
                return false;
            }
        });
        int level = getIntent().getIntExtra(LEVEL, -1);
        if (level < 1 || level > 4 ){
            Log.d("alon","invalid level");
            return;
        }
        GameTask gameTask = new GameTask();
        gameTask.execute(new GameParams(level));


    }

    private void startTimer() {
        new CountDownTimer(GAME_TIME, COUNT_DOWN_INTERVAL) {


            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(String.format("%ss", String.valueOf(millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                tvResult.setText("Done! your result is " + score);
                btnPlayAgain.setVisibility(View.VISIBLE);
                tvGoBack.setVisibility(View.VISIBLE);
                setButtonsClickable(false);
                if (score > maxResult) {
                    maxResult = score;
                    getSharedPreferences(PREFS, MODE_PRIVATE).edit().putInt(MAX_RESULT, maxResult).apply();
                }

            }
        }.start();
    }

    public void generateQuestion() {
        Random random = new Random();
        int firstNumber = 1 + random.nextInt(31);
        int secondNumber = 1 + random.nextInt(31);
        tvSum.setText(String.format("%s + %s", Integer.toString(firstNumber), Integer.toString(secondNumber)));

        locationOfCorrectAnswer = 1 + random.nextInt(4);

        answersList.clear();

        for (int i = 1; i <= 6; i++) {
            if (i == locationOfCorrectAnswer) {
                answersList.add(firstNumber + secondNumber);
            }
            int wrongAnswer = 1 + random.nextInt(41);
            while (wrongAnswer == firstNumber + secondNumber) {
                wrongAnswer = 1 + random.nextInt(41);
            }
            answersList.add(wrongAnswer);

        }
        button1.setText(String.format(Locale.getDefault(), "%s", Integer.toString(answersList.get(0))));
        button2.setText(String.format(Locale.getDefault(), "%s", Integer.toString(answersList.get(1))));
        button3.setText(String.format(Locale.getDefault(), "%s", Integer.toString(answersList.get(2))));
        button4.setText(String.format(Locale.getDefault(), "%s", Integer.toString(answersList.get(3))));
        button5.setText(String.format(Locale.getDefault(), "%s", Integer.toString(answersList.get(4))));
        button6.setText(String.format(Locale.getDefault(), "%s", Integer.toString(answersList.get(5))));
    }


    public void chooseAnswer(View view) {
        String clickedAnswerTag = view.getTag().toString();
        if (Integer.toString(locationOfCorrectAnswer).equals(clickedAnswerTag)) {
            tvResult.setText("Correct!");
            score++;
        } else {
            tvResult.setText("Wrong :(");
        }
        numberOfQuestions++;
        tvScore.setText(String.format(Locale.getDefault(), "%s", Integer.toString(score) + "/" + Integer.toString(numberOfQuestions)));
        generateQuestion();
    }

    public void playAgain(View view) {
        score = 0;
        numberOfQuestions = 0;
        setButtonsClickable(true);
        tvGoBack.setVisibility(View.INVISIBLE);
        int timeRested = GAME_TIME / 1000;
        tvTimer.setText(String.valueOf(timeRested));
        tvScore.setText(String.format(Locale.getDefault(), "%s", Integer.toString(score) + "/" + Integer.toString(numberOfQuestions)));
        startTimer();
        generateQuestion();
        btnPlayAgain.setVisibility(View.INVISIBLE);
    }

    private void setButtonsClickable(boolean clickable) {
        button1.setClickable(clickable);
        button2.setClickable(clickable);
        button3.setClickable(clickable);
        button4.setClickable(clickable);
        button5.setClickable(clickable);
        button6.setClickable(clickable);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(MAX_RESULT, maxResult);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    private static class GameTask extends AsyncTask<GameParams, Void, Integer> {


        //public static final String BASE_URL = "http://10.0.2.2:8080/braintest_server_war_exploded/MainServlet";
        public static final String BASE_URL = "http://localhost:8080/braintest_server_war_exploded/MainServlet";
        //public static final String BASE_URL = "http://192.168.1.16:8080/braintest_server_war_exploded/MainServlet";

        @Override
        protected Integer doInBackground(GameParams... gameParams) {
            if (gameParams == null || gameParams.length != 1) {
                return null;
            }
            int level = gameParams[0].getGameDifficultyLevel();
            String response = GameGetRequest.getRequest(BASE_URL + "?level=" + level);
            if (response == null)
                return null;
            try {
                int result = Integer.valueOf(response);
                GAME_TIME = result;
                return result;
            } catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            handler.sendMessage(new Message());
        }
    }
}