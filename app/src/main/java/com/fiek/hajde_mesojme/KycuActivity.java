package com.fiek.hajde_mesojme;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class KycuActivity extends AppCompatActivity {

  private EditText kycuEmail;
  private EditText kycuFjalekalimi;
  private Button kycuBtn;
  private Button regjKycuBtn;

  private FirebaseAuth mAuth;

  private ProgressBar kycuProgress;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_kycu);

    mAuth = FirebaseAuth.getInstance();

    kycuEmail = findViewById(R.id.email);
    kycuFjalekalimi = findViewById(R.id.fjalekalimi);
    kycuBtn = findViewById(R.id.kycu_btn);
    regjKycuBtn = findViewById(R.id.regj_btn);
    kycuProgress = findViewById(R.id.kycu_progress);

    regjKycuBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        Intent regIntent = new Intent(KycuActivity.this, RegjistrohuActivity.class);
        startActivity(regIntent);

      }
    });


    kycuBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        String Email = kycuEmail.getText().toString();
        String Fjalekalimi = kycuFjalekalimi.getText().toString();

        if(!TextUtils.isEmpty(Email) && !TextUtils.isEmpty(Fjalekalimi)){
          kycuProgress.setVisibility(View.VISIBLE);

          mAuth.signInWithEmailAndPassword(Email, Fjalekalimi).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

              if(task.isSuccessful()){

                dergoMain();

              } else {

                String errorMessage = task.getException().getMessage();
                Toast.makeText(KycuActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();


              }

              kycuProgress.setVisibility(View.INVISIBLE);

            }
          });
        }
      }
    });
  }
  @Override
  protected void onStart() {
    super.onStart();

    FirebaseUser currentUser = mAuth.getCurrentUser();

    if(currentUser != null){

      dergoMain();

    }


  }

  private void dergoMain() {

    Intent mainIntent = new Intent(KycuActivity.this, MainActivity.class);
    startActivity(mainIntent);
    finish();

  }
}
