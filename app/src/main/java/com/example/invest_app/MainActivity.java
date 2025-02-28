package com.example.invest_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.invest_app.model.ProfileModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private CardView OperationCard, CurrencyCard, PlanCard, WalletCard, ExchangeCard, AboutCard;
    private CircleImageView profileImage;
    private TextView coins, name, email;
    Toolbar toolbar;
    DatabaseReference reference;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setSupportActionBar(toolbar);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        getDataFromDatabase();
        clickListener();
    }
    private void clickListener(){
        profileImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this,Profile.class));
            }
        });
        OperationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Operation.class));
            }
        });
        CurrencyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Currency.class));
            }
        });
        PlanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Plan.class));
            }
        });
        AboutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, About.class));
            }
        });
        ExchangeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Exchange.class));
            }
        });
        WalletCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Wallet.class));
            }
        });
    }

    private void init() {
        OperationCard = findViewById(R.id.OperationCard);
        CurrencyCard = findViewById(R.id.CurrencyCard);
        PlanCard = findViewById(R.id.PlanCard);
        WalletCard = findViewById(R.id.WalletCard);
        ExchangeCard = findViewById(R.id.ExchangeCard);
        AboutCard = findViewById(R.id.AboutCard);
        coins = findViewById(R.id.coins);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        profileImage = findViewById(R.id.profileImage);
        toolbar = findViewById(R.id.toolbar);
    }
    private void getDataFromDatabase() {
        DatabaseReference userReference = reference.child(user.getUid());
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProfileModel model = snapshot.getValue(ProfileModel.class);
                if (model != null) {
                    name.setText(model.getName());
                    email.setText(model.getEmail());
                    coins.setText(String.valueOf(model.getCoins()));
                    Glide.with(getApplicationContext())
                            .load(model.getImage())
                            .placeholder(R.drawable.profile)
                            .timeout(6000)
                            .into(profileImage);
                } else {
                    name.setText("Brak danych");
                    email.setText("Brak danych");
                    coins.setText("0");
                    Toast.makeText(MainActivity.this, "Brak danych użytkownika", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Błąd:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

}