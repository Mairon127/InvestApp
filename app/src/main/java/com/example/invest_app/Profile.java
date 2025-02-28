package com.example.invest_app;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.invest_app.managers.CoinManager;
import com.example.invest_app.model.ProfileModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    private CircleImageView profileImage;
    private TextView name, email, policy, coins, logout;
    private ImageButton editImageBtn;
    private Button updateBtn;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private static final int IMAGE_PICKER = 1;
    private Uri photoUri;
    private String imageUrl;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
        loadDataFromDatabase();
        clickListener();
        CoinManager.addCoinsIfTimeElapsed(this);

    }

    private void clickListener() {
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(Profile.this, Login.class));
                finish();
            }
        });
        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, RulesActivity.class));
            }
        });
        editImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(Profile.this)
                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType(("image/*"));
                                    startActivityForResult(intent, IMAGE_PICKER);
                                } else {
                                    Toast.makeText(Profile.this, "Proszę o pozwolenie", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                            }
                        }).check();

            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER && resultCode == RESULT_OK) {
            if (data != null) {
                photoUri = data.getData();
                updateBtn.setVisibility(View.VISIBLE);

            }
        }
    }

    private void init() {
        profileImage = findViewById((R.id.profileImage));
        name = findViewById((R.id.name));
        email = findViewById((R.id.email));
        policy = findViewById((R.id.policy));
        logout = findViewById((R.id.logoutv2));
        coins = findViewById((R.id.coins));
        editImageBtn = findViewById((R.id.editImage));
        updateBtn = findViewById(R.id.updateBtn);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Proszę czekaj");
        progressDialog.setCancelable(false);
    }

    private void loadDataFromDatabase() {
        reference.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ProfileModel model = snapshot.getValue(ProfileModel.class);

                        name.setText(model.getName());
                        email.setText(model.getEmail());
                        coins.setText(String.valueOf(model.getCoins()));

                        Glide.with(getApplicationContext())
                                .load(model.getImage())
                                .placeholder(R.drawable.profile)
                                .into(profileImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Profile.this, "Błąd: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void uploadImage() {
        if (photoUri == null) {
            return;
        }

        String fileName = user.getUid() + ".jpg";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images/" + fileName);

        progressDialog.show();

        storageReference.putFile(photoUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUrl = uri.toString();
                    uploadImageUrlToDatabase();
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(Profile.this, "Błąd: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(taskSnapshot -> {
                    long totalSize = taskSnapshot.getTotalByteCount();
                    long transferSize = taskSnapshot.getBytesTransferred();
                    progressDialog.setMessage("Wczytane " + (transferSize / 1024) + "KB / " + (totalSize / 1024) + "KB");
                });
    }

    private void uploadImageUrlToDatabase() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("image", imageUrl);

        reference.child(user.getUid()).updateChildren(map)
                .addOnCompleteListener(task -> {
                    updateBtn.setVisibility(View.GONE);
                    progressDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(Profile.this, "Błąd podczas aktualizacji bazy danych: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}