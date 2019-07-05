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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class  RegjistrohuActivity extends AppCompatActivity {

  private EditText reg_email,reg_emri,reg_mbiemri,reg_drejtimi;
  private EditText reg_fjalekalimi;
  private EditText reg_konfirmoF;
  private Button reg_btn;
  private Button reg_kycu_btn;
  private ProgressBar reg_progress;

  private DatabaseReference databaseReference;
  private FirebaseDatabase firebaseDatabase;
  private FirebaseAuth mAuth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_regjistrohu);

    mAuth = FirebaseAuth.getInstance();

    reg_emri = findViewById(R.id.emri);
    reg_mbiemri = findViewById(R.id.mbiemri);
    reg_drejtimi = findViewById(R.id.drejtimi);
    reg_email = findViewById(R.id.email);
    reg_fjalekalimi = findViewById(R.id.fjalekalimi);
    reg_konfirmoF = findViewById(R.id.konfirmo_fjalekalimin);
    reg_btn = findViewById(R.id.btn_regjistrohu);
    reg_kycu_btn = findViewById(R.id.kycu_btn);
    reg_progress = findViewById(R.id.regj_progress);

    databaseReference=FirebaseDatabase.getInstance().getReference("Users");

    reg_kycu_btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        finish();

      }
    });

    reg_btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

         final String emri = reg_emri.getText().toString();
        final String mbiemri = reg_mbiemri.getText().toString();
       final String drejtimi = reg_drejtimi.getText().toString();
        final String email = reg_email.getText().toString();
        final String pass = reg_fjalekalimi.getText().toString();
        final String confirm_pass = reg_konfirmoF.getText().toString();

        if(!TextUtils.isEmpty(emri) && !TextUtils.isEmpty(mbiemri) && !TextUtils.isEmpty(drejtimi) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) & !TextUtils.isEmpty(confirm_pass)){

          if(pass.equals(confirm_pass)){

            reg_progress.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {


                   DergojeMain();




                } else {

                  String errorMessage = task.getException().getMessage();
                  Toast.makeText(RegjistrohuActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                }

                reg_progress.setVisibility(View.INVISIBLE);

              }
            });

          } else {

            Toast.makeText(RegjistrohuActivity.this, "Fushat nuk perputhen !.", Toast.LENGTH_LONG).show();

          }
        }


      }
    });


  }

  @Override
  protected void onStart() {
    super.onStart();

    FirebaseUser currentUser = mAuth.getCurrentUser();
    if(currentUser != null){

      DergojeMain();

    }

  }


  private void DergojeMain() {

    Intent mainIntent = new Intent(RegjistrohuActivity.this, MainActivity.class);
    startActivity(mainIntent);
    finish();

  }
}







