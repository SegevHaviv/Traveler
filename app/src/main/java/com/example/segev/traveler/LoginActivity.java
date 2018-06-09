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

//TODO Upgrading the spinner to alert dialog spinner

public class LoginActivity extends AppCompatActivity {
    private final static String TAG = LoginActivity.class.getSimpleName();

    //Views
    private EditText mEmailField;
    private EditText mPasswordField;
    private TextView createAccountText;
    //Views

    //Buttons
    private Button mLoginButton;
    //Buttons

    //ProgressBar
    private ProgressBar spinner;
    //ProgressBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(UserModel.getInstance().getCurrentUser() != null){
            Log.d(TAG,"User already logged in with " + UserModel.getInstance().getCurrentUser().getEmail());
            Intent switchActivityIntent = new Intent(this,MainScreenActivity.class);
            switchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(switchActivityIntent);
            finish();
        }

        initializeViews();
        initializeButtons();
        bindButtons();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void initializeViews(){
        mEmailField = findViewById(R.id.login_email_ed);
        mPasswordField = findViewById(R.id.login_password_ed);
    }

    public void initializeButtons(){
        spinner = findViewById(R.id.login_ProgressBar);
        spinner.setVisibility(View.GONE);

        mLoginButton = findViewById(R.id.login_button);
        createAccountText = findViewById(R.id.createAccount);
    }

    public void bindButtons(){
        createAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToMoveActivity = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intentToMoveActivity);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginButton.setEnabled(false);

                hideKeyboard();

                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();

                spinner.setVisibility(View.VISIBLE);
                UserModel userModel = UserModel.getInstance();
                userModel.login(email, password, new UserModel.UserModelLoginListener() {
                    @Override
                    public void onLogin() {
                        spinner.setVisibility(View.GONE);
                        Intent switchActivityIntent = new Intent(getApplicationContext(),MainScreenActivity.class);
                        switchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(switchActivityIntent);
                        finish();
                    }

                    @Override
                    public void onLoginFail() {
                        spinner.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                mLoginButton.setEnabled(true);
            }});
    }

    public void hideKeyboard(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
