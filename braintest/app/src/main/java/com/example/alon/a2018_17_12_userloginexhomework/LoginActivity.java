package com.example.alon.a2018_17_12_userloginexhomework;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String USERNAME = "username";
    public static final String SP_LOGGED_USER = "spLoggedUser";
    public static final String PREFS = "prefs";
    public static final int REQUEST_CODE = 123;
    public static final String DESTINATION = "destination";
    public static final String REGISTERED_USERS_SET = "registeredUsersSet";
    EditText etUsername, etPassword;
    Button btnLogin;
    public static HashMap<String, User> hashMap;
    Set<String> registeredUsersSet;
    TextView tvSignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkForLoggedOrRegisteredUsers();
        init();
        checkDestination();
    }


    private void checkDestination() {
        int destination = getIntent().getIntExtra(DESTINATION, -1);
        if (destination == 0)
            finish();
    }

    private void checkForLoggedOrRegisteredUsers() {
        String loggedInUser = getSharedPreferences(PREFS, MODE_PRIVATE)
                .getString(SP_LOGGED_USER, null);
        if (loggedInUser != null) {
            User user = new User(loggedInUser);
            Intent intent = new Intent(this, UserDetailsActivity.class);
            intent.putExtra(USERNAME, user.getUsername());
            startActivityForResult(intent, REQUEST_CODE);
        }
        try {
            registeredUsersSet = getSharedPreferences(PREFS, MODE_PRIVATE)
                    .getStringSet(REGISTERED_USERS_SET, null);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (registeredUsersSet != null) {
            hashMap = new HashMap<>();
            for (String s : registeredUsersSet) {
                User u = new User(s);
                hashMap.put(u.getUsername(), u);
                Log.d("log123", u.toString());
            }
        }
    }

    private void init() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        if (registeredUsersSet == null) {
            hashMap = new HashMap<>();
            registeredUsersSet = new HashSet<>();
        }
        btnLogin.setOnClickListener(this);
        tvSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSignup:
                SignupFragment fragment = new SignupFragment();
                fragment.setUsername(etUsername.getText().toString());
                fragment.setPassword(etPassword.getText().toString());
                fragment.setListener(new SignupFragment.OnSignupFragmentListener() {
                    @Override
                    public void onSignup(String username, String password, String profilePicturePath) {
                        signUp(username, password, profilePicturePath);
                        login(username, password);
                        TastyToast.makeText
                                (LoginActivity.this, "successfully signed in", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                    }
                });
                fragment.show(getSupportFragmentManager(), "");
                break;
            case R.id.btnLogin:
                String user = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                login(user, password);
                break;
        }
    }

    private void login(String user, String password) {
        //logic check:
        if (user.length() < 1 || password.length() < 1) {
            TastyToast.makeText(LoginActivity.this, "fields must not be empty", TastyToast.LENGTH_SHORT, TastyToast.INFO);
            return;
        }
        //username check
        if (hashMap.containsKey(user)) {
            //password check:
            if (Objects.requireNonNull(hashMap.get(user)).getPassword().equals(password)) {
                //all checks are okay, logging in:
                Intent intent = new Intent(this, UserDetailsActivity.class);
                intent.putExtra(USERNAME, user);
                getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(SP_LOGGED_USER, user).apply();
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                //password error:
                Toast.makeText(this,
                        "password does not match, please retry"
                        , Toast.LENGTH_SHORT).show();
                etPassword.setText("");
            }
        } else {
            //username error:
            Toast.makeText(this,
                    "User does not exists, please try again"
                    , Toast.LENGTH_SHORT).show();
            clearEditTexts();
        }
    }

    private void signUp(String user, String password, String profilePicturePath) {
        if (user.length() < 1 || password.length() < 1) {
            Toast.makeText(this,
                    "user or pass must be above 1 chars",
                    Toast.LENGTH_SHORT).show();
        } else if (hashMap.containsKey(user)) {
            Toast.makeText(this,
                    "user already exists, please choose a different usename",
                    Toast.LENGTH_SHORT).show();
            clearEditTexts();
        } else {
            User newUser = new User(user, password, profilePicturePath);
            hashMap.put(user, newUser);
            registeredUsersSet.add(newUser.toString());
            getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                    .putStringSet(REGISTERED_USERS_SET, registeredUsersSet)
                    .apply();
            TastyToast.makeText(LoginActivity.this, "successfully registered used name " + user, TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
            clearEditTexts();
        }
    }

    private void clearEditTexts() {
        etUsername.setText("");
        etPassword.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                int destination = data.getIntExtra(DESTINATION, -1);
                if (destination == 0)
                    finish();
            }
        }
    }


}
