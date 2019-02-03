package com.example.alon.a2018_17_12_userloginexhomework;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UserDetailsActivity extends AppCompatActivity {

    //public static final String USERNAME = "username";
    public static final String DESTINATION = "destination";
    public static final String SP_LOGGED_USER = "spLoggedUser";
    public static final String MAX_RESULT = "max result";
    public static final int REQUEST_CODE = 123;
    public static final String PREFS = "prefs";
    public static final String PROFILE_PIC = "profile pic";
    private TextView etWelcome, tvMaxScore;
    private ImageView profilePicture;
    private String loggedInUsername;
    private int maxScore = 0;
    private String photoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_logged_in);
        init();

        loggedInUsername = getSharedPreferences(PREFS, MODE_PRIVATE).getString(SP_LOGGED_USER, null);
        maxScore = getSharedPreferences(PREFS, MODE_PRIVATE).getInt(MAX_RESULT, -1);
        photoPath = getSharedPreferences(PREFS, MODE_PRIVATE).getString(PROFILE_PIC, null);
        if (loggedInUsername != null)
            etWelcome.setText("welcome " + loggedInUsername);
        if (maxScore > 0) {
            tvMaxScore.setText("max score for session is " + maxScore);
        }
        if (photoPath != null) {
            File file = new File(photoPath);
            if (file.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                junk();
                profilePicture.setImageBitmap(myBitmap);
                profilePicture.setRotation(-90);

            }
        }
    }

    private void junk() {
        try {
            int inWidth = 0;
            int inHeight = 0;

            InputStream in = new FileInputStream(photoPath);

            // decode image size (decode metadata only, not the whole image)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            in = null;

            // save width and height
            inWidth = options.outWidth;
            inHeight = options.outHeight;

            // decode full image pre-resized
            in = new FileInputStream(photoPath);
            options = new BitmapFactory.Options();
            // calc rought re-size (this is no exact resize)
            options.inSampleSize = Math.max(inWidth / 100, inHeight / 100);
            // decode full image
            Bitmap roughBitmap = BitmapFactory.decodeStream(in, null, options);

            // calc exact destination size
            Matrix m = new Matrix();
            RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
            RectF outRect = new RectF(0, 0, 100, 100);
            m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
            float[] values = new float[9];
            m.getValues(values);

            // resize bitmap
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(roughBitmap, (int) (roughBitmap.getWidth() * values[0]), (int) (roughBitmap.getHeight() * values[4]), true);

            // save image
            try {
                FileOutputStream out = new FileOutputStream(photoPath);
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            } catch (Exception e) {
                Log.e("Image", e.getMessage(), e);
            }
        } catch (IOException e) {
            Log.e("Image", e.getMessage(), e);
        }
    }

    private void init() {
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnStartgame = findViewById(R.id.btnStartgame);
        profilePicture = findViewById(R.id.ivProfilePicture);
        etWelcome = findViewById(R.id.etWelcome);
        tvMaxScore = findViewById(R.id.tvMaxScore);
    }

    public void logout(View view) {

        new AlertDialog.Builder(this)
                .setTitle("are you sure?")
                .setMessage("logging out will reset your high score")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(SP_LOGGED_USER, null).commit();
                        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putInt(MAX_RESULT, 0).commit();
                        setResult(RESULT_OK, new Intent(UserDetailsActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(DESTINATION, 0);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    public void btnStartgameClicked(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                maxScore = data.getIntExtra(MAX_RESULT, -1);
                if (loggedInUsername != null && maxScore != -1) {
                    tvMaxScore.setText("max score for session is " + maxScore);
                }
            }
        }
    }
}
