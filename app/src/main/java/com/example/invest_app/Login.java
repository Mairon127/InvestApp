package com.example.invest_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
private EditText emailv2,passwordv2;
private Button loginBtn;
private ProgressBar progressBar;
private FirebaseAuth auth;
private TextView signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        auth=FirebaseAuth.getInstance();
        clickListener();
    }
    private void init(){
        emailv2=findViewById(R.id.emailv2);
        passwordv2=findViewById(R.id.passwordv2);
        progressBar=findViewById(R.id.progressBar);
        signup=findViewById(R.id.signup);
        loginBtn=findViewById(R.id.loginBtn);
    }
    private void clickListener(){
signup.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(Login.this,Register.class));
        finish();

    }
});
loginBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String email = emailv2.getText().toString();
        String password = passwordv2.getText().toString();
        if (TextUtils.isEmpty(email)){
            emailv2.setError(("Błędny email"));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordv2.setError("Wymagane !!!");
            return;
        }
        signIn(email,password);
    }
});
    }
    private void signIn(String email,String password){
        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(Login.this,MainActivity.class));
                            finish();
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Login.this, "Błąd"+
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}