package com.example.segev.traveler;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.segev.traveler.Model.ModelFirebase;
import com.example.segev.traveler.Model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hiding the time bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); //Remove title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//Remove notification bar

        setContentView(R.layout.activity_login);
        //Hiding the action bar
        getSupportActionBar().hide();

        if(UserModel.instance.getCurrentUser() != null){
            Log.d(TAG,"User already logged in with " + UserModel.instance.getCurrentUser().getEmail());
            Intent switchActivityIntent = new Intent(this,MainScreenActivity.class);
            switchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(switchActivityIntent);
            finish();
        }

        initializeViews();
        initializeButtons();
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

                //Hiding the keyboard
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }


                if(!validateForm())
                    return;

                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();

                spinner.setVisibility(View.VISIBLE);
                UserModel.instance.login(email, password, new UserModel.UserModelLoginListener() {
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

    private boolean validateForm() {
        boolean valid = true;


        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        }else if(!isValidEmailAddress(email)){
            mEmailField.setError("Invalid Email.");
            valid = false;
        }
        else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else if (password.length() < 8){
            mPasswordField.setError("Password Must Be At Least 8 Characters.");
            valid = false;
        } else{
            mPasswordField.setError(null);
        }

        return valid;
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}
