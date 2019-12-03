package com.fiek.hajde_mesojme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignUp extends AppCompatActivity
{
    private Button btnSignUp;
    private TextView txtAlreadyHaveAccountSignIn;
    private Intent intent;
    private EditText txtInputFirstName, txtInputLastName, txtInputEmail, txtInputUsername, txtInputPassword;
    private RadioButton rdbtnGender;
    private RadioGroup rdgrpGender;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String userRole;
    private String salt;
    private String gender;
    private addUsers newUser;


    // metoda validate validon te gjitha fushat ne SignUp
    public void validate()
    {
        String firstname = txtInputFirstName.getText().toString().trim();
        String firstnamePattern = "[A-Z].{1}[a-zA-Z]*";
        if(TextUtils.isEmpty(firstName))
        {
            txtInputFirstName.setError("Enter First Name");
        }
        else if(!firstname.matches(firstnamePattern))
        {
            txtInputFirstName.setError("The first name must start with a capital letter and should contain more than two letters.");
        }
        else
        {
            txtInputLastName.setEnabled(true);
        }

        String lastname = txtInputLastName.getText().toString().trim();
        String lasttnamePattern = "[A-Z].{1}[a-zA-Z]*";
        if(TextUtils.isEmpty(lastName))
        {
            txtInputLastName.setError("Enter Last Name");
        }
        else if(!lastname.matches(lasttnamePattern))
        {
            txtInputLastName.setError("The last name must start with a capital letter and should contain more than two letters.");
        }


        String email = txtInputEmail.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(TextUtils.isEmpty(email))
        {
            txtInputEmail.setError("Enter Email");
        }
        else if(!email.matches(emailPattern))
        {
            txtInputEmail.setError("Invalid email address");
        }


        String username = txtInputUsername.getText().toString().trim();
        String usernamePattern = "[a-zA-Z0-9]*";
        if(TextUtils.isEmpty(username))
        {
            txtInputUsername.setError("Enter Username");
        }
        else if(!username.matches(usernamePattern) || username.length() < 8)
        {
            txtInputUsername.setError("The username must contain at least eight characters from following categories: Uppercase characters (A-Z) Lowercase characters (a-z) Digits (0-9)");
        }
        else if(username.length() < 8)
        {
            txtInputUsername.setError("The username must contain at least eight characters from following categories: Uppercase characters (A-Z) Lowercase characters (a-z) Digits (0-9)");
        }


        String password = txtInputPassword.getText().toString().trim();
        String passwordPattern = "[a-zA-Z0-9]*";
        if(txtInputPassword.getText().toString().length()==0)
        {
            txtInputPassword.setError("Enter Password");
        }
        else if((!password.matches(passwordPattern) && password.length() > 6))
        {
            txtInputPassword.setError("The password must contain at least six characters from following categories: Uppercase characters (A-Z) Lowercase characters (a-z) Digits (0-9)");
        }
        else if(password.length() < 6)
        {
            txtInputPassword.setError("The password must contain 6 or more characters");
        }

        if(TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(gender))
        {
            Toast.makeText(SignUp.this, "Fill all fields!", Toast.LENGTH_SHORT).show();
        }

    }


    // metoda e cila thirr vonesen gjate signup
    public void delay()
    {
        int seconds = 3;

        Utils.delay(seconds, new Utils.DelayCallback() {
            @Override
            public void afterDelay()
            {
                intent = new Intent(getBaseContext(), SignIn.class);
                startActivity(intent);
            }
        });
    }


    public void addUser()
    {
        firstName = txtInputFirstName.getText().toString().trim();
        lastName = txtInputLastName.getText().toString().trim();
        email = txtInputEmail.getText().toString().trim();
        username = txtInputUsername.getText().toString().trim();
        password = txtInputPassword.getText().toString().trim();
        userRole = "guest";
        salt = SHA512.gjeneroSalt();

        rdgrpGender = findViewById(R.id.rdgrpGender);
        int genderID = rdgrpGender.getCheckedRadioButtonId();
        rdbtnGender = findViewById(genderID);
        gender = rdbtnGender.getText().toString();
        password = SHA512.hash(password, salt);

        if(firstName.length()>0)
        {
            firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
        }
        if(lastName.length()>0)
        {
            lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
        }


        validate();

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.child(txtInputUsername.getText().toString()).exists())
                {
                    if (!(TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(gender))) {
                        newUser = new addUsers(firstName, lastName, email, username, password, gender, userRole, salt);
                        databaseReference.child(username).setValue(newUser);
                        Toast.makeText(SignUp.this, "You are registered successfully", Toast.LENGTH_SHORT).show();
                        txtInputFirstName.setText("");
                        txtInputLastName.setText("");
                        txtInputEmail.setText("");
                        txtInputUsername.setText("");
                        txtInputPassword.setText("");
                        txtInputUsername.setError(null);
                        delay();
                        finish();
                    }

                }
                else
                {
                    txtInputUsername.setError("Username exists");
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnSignUp = findViewById(R.id.btnSignUp);
        txtAlreadyHaveAccountSignIn = findViewById(R.id.txtAlreadyHaveAccountSignIn);
        txtInputFirstName = findViewById(R.id.txtInputFirstName);
        txtInputLastName = findViewById(R.id.txtInputLastName);
        txtInputEmail = findViewById(R.id.txtInputEmail);
        txtInputUsername = findViewById(R.id.txtInputUsername);
        txtInputPassword = findViewById(R.id.txtInputPassword);
        rdgrpGender = findViewById(R.id.rdgrpGender);


        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("tblUsers");

        btnSignUp.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {



                addUser();


            }
        });
        txtAlreadyHaveAccountSignIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                intent = new Intent(getBaseContext(), SignIn.class);
                startActivity(intent);
            }
        });

    }
}


// klase e cila gjeneron nje vonese
class Utils {

    // Delay mechanism

    public interface DelayCallback{
        void afterDelay();
    }

    public static void delay(int secs, final DelayCallback delayCallback){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayCallback.afterDelay();
            }
        }, secs * 1000); // afterDelay will be executed after (secs*1000) milliseconds.
    }
}
