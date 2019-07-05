package com.fiek.hajde_mesojme;

import android.content.Intent;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String user_id;

    private FloatingActionButton shtoPostimBtn;

    private BottomNavigationView mainbottomNav;

    private KryefaqjaFragment kryefaqjaFragment;

    private ProfiliFragment profiliFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        getSupportActionBar().setTitle("Hajde Mesojme");

        if(mAuth.getCurrentUser() != null) {

            mainbottomNav = findViewById(R.id.mainBottomNav);
            
            kryefaqjaFragment = new KryefaqjaFragment();

            profiliFragment = new ProfiliFragment();
            
            initializeFragment();

            mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

                    switch (item.getItemId()) {

                        case R.id.btn_kryefaqja:

                            replaceFragment(kryefaqjaFragment, currentFragment);
                            return true;

                        case R.id.btn_profili:

                            replaceFragment(profiliFragment, currentFragment);
                            return true;

                        default:
                            return false;


                    }

                }
            });


            shtoPostimBtn = findViewById(R.id.shto_postim);
            shtoPostimBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent newPostIntent = new Intent(MainActivity.this, ShtoPostimActivity.class);
                    startActivity(newPostIntent);

                }
            });

        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){

            DergoKycje();

        } else {

           user_id = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                        if(!task.getResult().exists()){

                            Intent setupIntent = new Intent(MainActivity.this, RegjistrohuActivity.class);
                            startActivity(setupIntent);
                            finish();

                        }

                    } else {

                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();


                    }

                }
            });

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.btn_ckycu:
                Ckycja();
                return true;

            case R.id.btn_rrethnesh:

                Intent settingsIntent = new Intent(MainActivity.this, RrethneshActivity.class);
                startActivity(settingsIntent);

                return true;


            default:
                return false;


        }

    }

    private void Ckycja() {


        mAuth.signOut();
        DergoKycje();
    }

    private void DergoKycje() {

        Intent loginIntent = new Intent(MainActivity.this, KycuActivity.class);
        startActivity(loginIntent);
        finish();

    }

    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_container, kryefaqjaFragment);

        fragmentTransaction.add(R.id.main_container, profiliFragment);


        fragmentTransaction.hide(profiliFragment);

        fragmentTransaction.commit();

    }

    private void replaceFragment(Fragment fragment, Fragment currentFragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(fragment == kryefaqjaFragment){

            fragmentTransaction.hide(profiliFragment);


        }
        if(fragment == profiliFragment){

            fragmentTransaction.hide(kryefaqjaFragment);


        }
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();

    }


}