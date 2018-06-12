package com.example.segev.traveler;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.segev.traveler.Model.UserModel;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getSimpleName();

    //Views
    private EditText mEmailField;
    private EditText mPasswordField;
    //Views

    //Buttons
    private Button mRegisterButton;
    //Buttons

    //ProgressBar
    private ProgressBar mSpinner;
    //ProgressBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        initializeViews();

        initializeButtons();
        bindButtons();
    }

    private void initializeViews(){
        mEmailField = findViewById(R.id.register_email_ed);
        mPasswordField = findViewById(R.id.register_password_ed);
    }

    private void initializeButtons(){
        mSpinner = findViewById(R.id.register_ProgressBar);
        mSpinner.setVisibility(View.GONE);
        mRegisterButton = findViewById(R.id.register_button);
    }

    private void bindButtons(){
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegisterButtonClicked();
            }
        });
    }

    private void onRegisterButtonClicked(){
        mRegisterButton.setEnabled(false);
        mSpinner.setVisibility(View.VISIBLE);

        hideKeyboard();

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        UserModel userModel = UserModel.getInstance();

        userModel.createUser(email, password, new UserModel.UserModelRegisterListener() {
            @Override
            public void onRegister() {
                mSpinner.setVisibility(View.GONE);
                Intent switchActivityIntent = new Intent(getApplicationContext(),MainScreenActivity.class);
                switchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(switchActivityIntent);
                finish();
            }

            @Override
            public void onRegisterFail() {
                mSpinner.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Register Failed", Toast.LENGTH_SHORT).show();
            }
        });
        mRegisterButton.setEnabled(true);
    }

    private void hideKeyboard(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }
}