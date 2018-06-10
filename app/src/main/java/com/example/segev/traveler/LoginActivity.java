package com.example.segev.traveler;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.segev.traveler.Model.UserModel;
import com.google.firebase.auth.FirebaseUser;

//TODO Upgrading the spinner to alert dialog spinner

public class LoginActivity extends AppCompatActivity {
    private final static String LOG_TAG = LoginActivity.class.getSimpleName();

    //Views
    private EditText mEmailField;
    private EditText mPasswordField;
    private TextView createAccountText;
    //Views

    //Buttons
    private Button mLoginButton;
    //Buttons

    //ProgressBar
    private ProgressBar mSpinner;
    //ProgressBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkIfUserIsLoggedIn();

        initializeViews();

        initializeButtons();
        bindButtons();
    }

    private void checkIfUserIsLoggedIn(){
        FirebaseUser currentUser = UserModel.getInstance().getCurrentUser();

        if(currentUser != null){
            Log.d(LOG_TAG,"User already logged in with " + currentUser.getEmail());
            Intent switchActivityIntent = new Intent(this,MainScreenActivity.class);
            switchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(switchActivityIntent);
            finish();
        }
    }

    private void initializeViews(){
        mEmailField = findViewById(R.id.login_email_ed);
        mPasswordField = findViewById(R.id.login_password_ed);
    }

    private void initializeButtons(){
        mSpinner = findViewById(R.id.login_ProgressBar);
        mSpinner.setVisibility(View.GONE);

        mLoginButton = findViewById(R.id.login_button);
        createAccountText = findViewById(R.id.createAccount);
    }

    private void bindButtons(){
        createAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent switchActivityIntent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(switchActivityIntent);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginButton.setEnabled(false);
                mSpinner.setVisibility(View.VISIBLE);

                hideKeyboard();

                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();

                UserModel currentUser = UserModel.getInstance();
                currentUser.login(email, password, new UserModel.UserModelLoginListener() {
                    @Override
                    public void onLogin() {
                        mSpinner.setVisibility(View.GONE);
                        Intent switchActivityIntent = new Intent(getApplicationContext(),MainScreenActivity.class);
                        switchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(switchActivityIntent);
                        finish();
                    }

                    @Override
                    public void onLoginFail() {
                        mSpinner.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                mLoginButton.setEnabled(true);
            }});
    }

    private void hideKeyboard(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
