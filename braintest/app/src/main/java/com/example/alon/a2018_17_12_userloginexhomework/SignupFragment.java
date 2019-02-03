package com.example.alon.a2018_17_12_userloginexhomework;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.FileProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.tastytoast.TastyToast;

import java.io.File;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class SignupFragment extends DialogFragment implements View.OnClickListener {

    private static final int REQUEST_CODE = 123;
    private static final String PREFS = "prefs";
    public static final String PROFILE_PICTURE_PATH = "profile pic";
    private EditText etUsername, etPassword;
    private Button btnSignup;
    private TextView tvGoback;
    private String username, password;
    private OnSignupFragmentListener listener;
    private ImageView profilePicture;
    //private String photoPath;
    private String profilePicturePath;


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setListener(OnSignupFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(getDialog().getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("Sign-up");
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.signup_background);
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        btnSignup = view.findViewById(R.id.btnSignup);
        tvGoback = view.findViewById(R.id.tvGobacktologin);
        profilePicture = view.findViewById(R.id.profilePicture);
        btnSignup.setOnClickListener(this);
        tvGoback.setOnClickListener(this);
        profilePicture.setOnClickListener(this);
        if (username != null) {
            etUsername.setText(username);
        }
        if (password != null) {
            etPassword.setText(password);
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignup:
                signup();
                break;
            case R.id.tvGoBack:
                dismiss();
                break;
            case R.id.profilePicture:
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePhotoIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    File file = createImageFile();
                    Uri photoUri = FileProvider.getUriForFile(getContext(), "com.example.alon.a2018_17_12_userloginexhomework.fileprovider", file);
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePhotoIntent, REQUEST_CODE);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            setPic();

        }
    }


    private void signup() {
        String userName = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        if (userName.isEmpty() || password.isEmpty()) {
            TastyToast.makeText(getContext(), "username and password are required", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            return;
        }
        if (listener != null && profilePicturePath != null) {
            listener.onSignup(userName, password, profilePicturePath);
            dismiss();
        }
    }


    public interface OnSignupFragmentListener {
        void onSignup(String username, String password, String profilePicturePath);
    }

    private File createImageFile() {
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(storageDir, "photo.jpg");
        profilePicturePath = file.getAbsolutePath();
        getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(PROFILE_PICTURE_PATH, profilePicturePath).apply();
        return file;
    }

    private void setPic() {
        int imageWidth = profilePicture.getWidth();
        int imageHeight = profilePicture.getHeight();
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(profilePicturePath, bitmapOptions);
        int photoWidth = bitmapOptions.outWidth;
        int photoHeight = bitmapOptions.outHeight;
        int scaleFactor = Math.min(photoWidth / imageWidth, photoHeight / imageHeight);
        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(profilePicturePath, bitmapOptions);
        profilePicture.setImageBitmap(bitmap);
        profilePicture.setRotation(-90);

    }


}
