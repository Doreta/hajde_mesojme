package com.fiek.hajde_mesojme;

import android.content.Intent;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fiek.hajde_mesojme.Common.ItemClickListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String txtInputUsername;

    private String user_id;

    private FloatingActionButton shtoPostimBtn;

    private BottomNavigationView mainbottomNav;

    private KryefaqjaFragment kryefaqjaFragment;

    private ProfiliFragment profiliFragment;


    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private String currentUser;
    private String currentPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("lendet");

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        getSupportActionBar().setTitle("Hajde Mesojme");
        Paper.init(this);
        currentUser = getIntent().getExtras().get("current_user").toString();
        currentPassword = getIntent().getExtras().get("current_password").toString();


        txtInputUsername = currentUser;

        if(currentUser != null)
        {

        }

    }




    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case R.id.btn_profili:
                dergoProfile();
                return true;

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

    private void dergoProfile() {

        Intent intent = new Intent(MainActivity.this, userProfile.class);
        intent.putExtra("current_user1", currentUser);
        intent.putExtra("current_password1", currentPassword);
        startActivity(intent);
        finish();

    }

    private void Ckycja() {


        mAuth.signOut();
        DergoKycje();
    }

    private void DergoKycje() {

        Intent loginIntent = new Intent(MainActivity.this, SignIn.class);
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