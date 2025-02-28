package com.example.invest_app;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.invest_app.databinding.ActivityOperationBinding;
import com.example.invest_app.managers.BlockChainManager;
import com.example.invest_app.managers.CoinManager;
import com.example.invest_app.managers.SharedPreferencesManager;
import com.example.invest_app.model.BlockModel;
import com.example.invest_app.utils.CipherUtils;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Operation extends AppCompatActivity implements View.OnClickListener {
    private ActivityOperationBinding viewBindingContent;
    private ProgressDialog progressDialog;
    private BlockChainManager blockChain;
    private SharedPreferencesManager prefs;
    private boolean isEncryptionActivated;
    private AppUpdateManager appUpdateManager;
    private static final int UPDATE_REQUEST_CODE = 1000;
    private CoinManager coinManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        prefs = new SharedPreferencesManager(this);
        super.onCreate(savedInstanceState);
        ActivityOperationBinding viewBinding = ActivityOperationBinding.inflate(getLayoutInflater());
        viewBindingContent = viewBinding;
        setContentView(viewBinding.getRoot());

        ImageView btn_encrypt = findViewById(R.id.btn_encrypt);
        coinManager = new CoinManager();

        viewBindingContent.btnAdd.setOnClickListener(this);

        checkUpdate();

        FirebaseAuth auth = FirebaseAuth.getInstance(); 

        viewBindingContent.recyclerContent.setHasFixedSize(true);
        viewBindingContent.recyclerContent.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        showProgressDialog(getResources().getString(R.string.text_creating_block_chain));
        new Thread(() -> runOnUiThread(() -> {
            blockChain = new BlockChainManager(this, prefs.getPowValue());
            viewBindingContent.recyclerContent.setAdapter(blockChain.adapter);
            cancelProgressDialog(progressDialog);
        })).start();

        viewBindingContent.btnAdd.setOnClickListener(this);

        btn_encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEncryptionActivated) {
                    Toast.makeText(Operation.this, "Kwota nieszyfrowana", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Operation.this, "Kwota szyfrowana", Toast.LENGTH_SHORT).show();
                }
                isEncryptionActivated = !isEncryptionActivated;
            }
        });
    }

    private void checkUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                startTheUpdate(appUpdateManager, appUpdateInfo);
            }
        });
    }

    private void startTheUpdate(@NonNull AppUpdateManager appUpdateManager, @NonNull AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    UPDATE_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeTheUpdate();
    }

    private void resumeTheUpdate() {
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                startTheUpdate(appUpdateManager, appUpdateInfo);
                            }
                        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void startBlockChain() {
        runOnUiThread(() -> {
            if (blockChain != null && viewBindingContent.editMessage.getText() != null && viewBindingContent.recyclerContent.getAdapter() != null) {
                String message = viewBindingContent.editMessage.getText().toString();
                if (!message.isEmpty()) {
                    int amountToSubtract = Integer.parseInt(message);

                    if (amountToSubtract <= 0) {
                        Toast.makeText(Operation.this, "Wprowadź kwotę większą niż zero.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!isEncryptionActivated) {
                            CoinManager.getCurrentCoinsFromFirebase(FirebaseAuth.getInstance().getCurrentUser().getUid(), new CoinManager.CoinsCallback() {
                                                            @Override
                                                            public void onCallback(int currentCoinsFirebase) {
                                                                if (amountToSubtract <= currentCoinsFirebase) {
                                                                    BlockModel newBlock = blockChain.newBlock(message);
                                                                    blockChain.addBlock(newBlock);
                                                                    CoinManager.subtractCoins(Operation.this, amountToSubtract);
                                                                } else {
                                                                    Toast.makeText(Operation.this, "Brak wystarczających środków na koncie.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                        } else {
                            try {
                                String encryptedMessage = CipherUtils.encryptIt(message).trim();
                                BlockModel newBlock = blockChain.newBlock(encryptedMessage);
                                blockChain.addBlock(newBlock);
                                CoinManager.subtractCoins(Operation.this, amountToSubtract);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Coś poszło nie tak.", Toast.LENGTH_LONG).show();
                            }
                        }

                        viewBindingContent.recyclerContent.scrollToPosition(blockChain.adapter.getItemCount() - 1);
                        System.out.println(getResources().getString(R.string.log_block_chain_valid, blockChain.isBlockChainValid()));
                        if (blockChain.isBlockChainValid()) {
                            viewBindingContent.recyclerContent.getAdapter().notifyDataSetChanged();
                            viewBindingContent.editMessage.setText("");
                        } else {
                            printErrorBlockchainCorrupted();
                        }
                    }
                } else {
                    printErrorEmptyData();
                }

                cancelProgressDialog(progressDialog);
            } else {
                printErrorSomethingWrong();
            }
        });
    }

    private void printErrorBlockchainCorrupted() {
        Toast.makeText(this, "Blockchain próba sfałszowania przy wprowadzaniu nowego bloku", Toast.LENGTH_LONG).show();
    }

    private void printErrorEmptyData() {
        Toast.makeText(this, "Nie wpisano żadnej kwoty.", Toast.LENGTH_LONG).show();
    }

    private void printErrorSomethingWrong() {
        Toast.makeText(this, "Coś jest źle", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(@NonNull View view) {
        if (view.getId() == R.id.btn_add) {
            startBlockChain();
        }
    }

    private void showProgressDialog(@NonNull String loadingMessage) {
        progressDialog = new ProgressDialog(Operation.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(loadingMessage);
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.show();
    }

    private void cancelProgressDialog(@Nullable ProgressDialog progressDialog) {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }
}
