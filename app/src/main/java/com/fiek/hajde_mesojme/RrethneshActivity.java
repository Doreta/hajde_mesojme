package com.fiek.hajde_mesojme;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class RrethneshActivity extends AppCompatActivity {


    private ProgressBar rrethneshProgress;

     private Toolbar rrethnesh_Toolbar;
    private Toolbar mainToolbar;
    private BottomNavigationView mainbottomNav;

    private KryefaqjaFragment kryefaqjaFragment;

    private ProfiliFragment profiliFragment;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rrethnesh);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        rrethnesh_Toolbar = (Toolbar) findViewById(R.id.rrethnesh_Toolbar);
        setSupportActionBar(rrethnesh_Toolbar);

        getSupportActionBar().setTitle("Rreth Nesh");

    }
}