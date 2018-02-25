package com.dartmouth.kd.devents;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dartmouth.kd.devents.AuthOnCompleteListener;
import com.dartmouth.kd.devents.SignUpActivity;
import com.dartmouth.kd.devents.Utils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button logInButton;
    private TextView signUpTextView;
    FirebaseAuth firebaseAuth;
    Task<AuthResult> task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        signUpTextView = (TextView) findViewById(R.id.signUpText);
        emailEditText = (EditText) findViewById(R.id.emailField);
        passwordEditText = (EditText) findViewById(R.id.passwordField);
        logInButton = (Button) findViewById(R.id.loginButton);
        logInButton.setOnClickListener(this);
        signUpTextView.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void onClick(View view){
        if(view.getId() == R.id.signUpText){
            Utils.showActivity(this, SignUpActivity.class);
        }else if(view.getId() == R.id.loginButton){
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if(email.isEmpty() || password.isEmpty())
                Utils.showDialog(this, "Error!",
                        "Please make sure you enter an email address and password");
            else{
                task = firebaseAuth.signInWithEmailAndPassword(email, password);
                task.addOnCompleteListener(new AuthOnCompleteListener(this));
            }
        }
    }

}
