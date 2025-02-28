package com.example.invest_app;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    private Button registerBtn;
    private EditText Name,Email,Password,ConfirmPassword;
    private ProgressBar progressBar;
    private TextView login;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth=FirebaseAuth.getInstance();
        init();
        clickListener();

    }
    private void init(){
        registerBtn=findViewById(R.id.registerBtn);
        Name=findViewById(R.id.namev2);
        Email=findViewById(R.id.emailv2);
        Password=findViewById(R.id.passwordv2);
        ConfirmPassword=findViewById(R.id.confirmPass);
        progressBar=findViewById(R.id.progressBar);
        login=findViewById(R.id.login);

    }
    private void clickListener(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,Login.class));
                finish();

            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=Name.getText().toString();
                String email=Email.getText().toString();
                String pass=Password.getText().toString();
                String confirmPass=ConfirmPassword.getText().toString();
                if (name.isEmpty()){
                    Name.setError("Wymagane");
                }
                if (email.isEmpty()){
                    Email.setError("Wymagane");
                    return;
                }
                if (pass.isEmpty()){
                    Password.setError("Wymagane");
                    return;
                }
                if (confirmPass.isEmpty()|| !pass.equals(confirmPass)){
                    ConfirmPassword.setError("Błędne hasło");
                    return;
                }
                createAccount(email,pass);
            }
        });
    }
    private void createAccount(String email, String password){
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email,password)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user=auth.getCurrentUser();
                    assert user != null;
                    updateUi(user, email);
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this,"Błąd"+
                            task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void updateUi(FirebaseUser user, String email){
        HashMap<String,Object>map=new HashMap<>();
        map.put("name",Name.getText().toString());
        map.put("email",email);
        map.put("uid", user.getUid());
        map.put("image","");
        map.put("coins",0);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(user.getUid())
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                     if(task.isSuccessful()){
                         Toast.makeText(Register.this,"Witamy tutaj",Toast.LENGTH_SHORT).show();
                         startActivity(new Intent(Register.this, Login.class));
                         finish();
                     }
                     else {
                         Toast.makeText(Register.this,"Błąd:  "+
                                 task.getException().getMessage(),
                                 Toast.LENGTH_SHORT).show();
                     }
                progressBar.setVisibility(View.GONE);
                    }
                });
    }
}