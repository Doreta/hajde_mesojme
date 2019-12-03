package com.fiek.hajde_mesojme;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fiek.hajde_mesojme.Common.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity
{

    private Button btnSignIn;
    private TextView txtNoAccount;
    private Intent intent;
    private EditText txtUsername, txtPassword;
    private CheckBox chckboxRememberMe;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String password;


    // validon fushat e SignIn
    public void validate()
    {
        String username = txtUsername.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if(username.length() == 0)
        {
            txtUsername.setError("Enter username");
        }
        if(password.length() == 0)
        {
            txtPassword.setError("Enter password");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btnSignIn = findViewById(R.id.btnSignIn);
        txtNoAccount = findViewById(R.id.txtNoAccount);
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        chckboxRememberMe = findViewById(R.id.chckboxRememberMe);

        Paper.init(this);


        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("tblUsers");


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                validate();
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        addUsers loginUser = dataSnapshot.child(txtUsername.getText().toString()).getValue(addUsers.class);
                        String salt = loginUser.salt;
                        password = txtPassword.getText().toString().trim();
                        password = SHA512.hash(password, salt);

                        // Save user & password ne menyre qe heren e ardhshme kur user hyn ne app mos me pas nevoj me ba login
                        if(chckboxRememberMe.isChecked())
                        {
                            Paper.book().write(Common.USER_KEY, txtUsername.getText().toString());
                            Paper.book().write(Common.PASSWORD_KEY, password);
                        }

                        // pjesa ne try catch trajton pjesen e login ku kontrollohen te dhenat e marrura nga useri
                        try
                        {
                           if(loginUser.username.equals(txtUsername.getText().toString().trim()))
                            {
                                if(loginUser.password.equals(password))
                                {


                                    Toast.makeText(SignIn.this, "Login Successfully!", Toast.LENGTH_SHORT).show();
                                    intent = new Intent(getBaseContext(), MainActivity.class);
                                    Common.currentUser = loginUser;
                                    intent.putExtra("current_user", loginUser.username);
                                    intent.putExtra("current_password", txtPassword.getText().toString());
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(SignIn.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Toast.makeText(SignIn.this, "Username doesn't exists!", Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });



        txtNoAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                intent = new Intent(getBaseContext(), SignUp.class);
                startActivity(intent);
            }
        });


    }

    // bllokon button back per tmos mundur te kthehemi prapa kur dalim signout
    @Override
    public  void onBackPressed()
    {
    }

}
