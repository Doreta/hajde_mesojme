package com.fiek.hajde_mesojme;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


// klasa e cila shfaqe te dhenat e current user
public class userProfile extends AppCompatActivity
{
    private Button btnUserChangeData;
    private EditText txtInputFirstName, txtInputLastName, txtInputEmail, txtInputUsername, txtInputPassword;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ImageView imageProfile;
    private Button btn1;

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String userRole;
    private String gender;
    private String salt;
    private addUsers newUser;


    private String currentUser;
    private String currentPassword;


    // validimet nese ndryshohet email ose password
    public void validate()
    {
        email = txtInputEmail.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(TextUtils.isEmpty(email))
        {
            txtInputEmail.setError("Enter Email");
        }
        else if(!email.matches(emailPattern))
        {
            txtInputEmail.setError("Invalid email address");
        }


        password = txtInputPassword.getText().toString().trim();
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
    }


    // metoda e cila validon te dhenat e userit
    public void updateUser()
    {
        password = txtInputPassword.getText().toString().trim();
        salt = SHA512.gjeneroSalt();
        final String newPassword = SHA512.hash(password, salt);

        validate();



        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                addUsers loginUser = dataSnapshot.child(txtInputUsername.getText().toString()).getValue(addUsers.class);

                firstName = txtInputFirstName.getText().toString();
                lastName = txtInputLastName.getText().toString();
                username = txtInputUsername.getText().toString();
                gender = loginUser.gender;
                userRole = loginUser.userRole;

                if(currentUser.equals(loginUser.username))
                {
                    if (!(TextUtils.isEmpty(email) ||  TextUtils.isEmpty(password)))
                    {
                        newUser = new addUsers(firstName, lastName, email, username, newPassword, gender, userRole, salt);
                        databaseReference.child(username).setValue(newUser);

                        Toast.makeText(userProfile.this, "User's data updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });



    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        btnUserChangeData = findViewById(R.id.btnChangeUserData);
        txtInputFirstName = findViewById(R.id.txtInputFirstName);
        txtInputLastName = findViewById(R.id.txtInputLastName);
        txtInputEmail = findViewById(R.id.txtInputEmail);
        txtInputPassword = findViewById(R.id.txtInputPassword);
        txtInputUsername = findViewById(R.id.txtInputUsername);
        //btn1 = findViewById(R.id.btn1);
        imageProfile = findViewById(R.id.imageProfile);

        currentUser = getIntent().getExtras().get("current_user1").toString();
        txtInputUsername.setText(currentUser);
        currentPassword = getIntent().getExtras().get("current_password1").toString();

        txtInputFirstName.setEnabled(false);
        txtInputLastName.setEnabled(false);
        txtInputUsername.setEnabled(false);



        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("tblUsers");



        databaseReference.addValueEventListener(new ValueEventListener()
        {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                addUsers loginUser = dataSnapshot.child(txtInputUsername.getText().toString()).getValue(addUsers.class);
                if(currentUser.equals(loginUser.username))
                {
                    txtInputFirstName.setText(loginUser.firstName);
                    txtInputLastName.setText(loginUser.lastName);
                    txtInputEmail.setText(loginUser.email);
                    txtInputPassword.setText(currentPassword);
                    String gender = loginUser.gender;
                    if(gender.equals("Female"))
                    {
                        imageProfile.setImageResource(R.drawable.user_female);
                    }
                    else if(gender.equals("Male"))
                    {
                        imageProfile.setImageResource(R.drawable.user_male);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnUserChangeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                updateUser();
            }
        });
    }


}
