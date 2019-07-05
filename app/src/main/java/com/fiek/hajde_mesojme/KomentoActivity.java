package com.fiek.hajde_mesojme;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KomentoActivity extends AppCompatActivity {

    private Toolbar komentToolbar;

    private EditText fusha_komentit;
    private ImageView btn_komento;

    private RecyclerView lista_koment;
    private KomentetRecyclerAdapter komentetRecyclerAdapter;
    private List<Komentet> lista_komenteve;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String postimi_id;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komentet);

        komentToolbar = findViewById(R.id.koment_toolbar);
        setSupportActionBar(komentToolbar);
        getSupportActionBar().setTitle("Komentet");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();
        postimi_id = getIntent().getStringExtra("postimi_id");

        fusha_komentit = findViewById(R.id.komenti);
        btn_komento = findViewById(R.id.btn_komento);
        lista_koment = findViewById(R.id.lista_komenteve);


        lista_komenteve = new ArrayList<>();
        komentetRecyclerAdapter = new KomentetRecyclerAdapter(lista_komenteve);
        lista_koment.setHasFixedSize(true);
        lista_koment.setLayoutManager(new LinearLayoutManager(this));
        lista_koment.setAdapter(komentetRecyclerAdapter);


        firebaseFirestore.collection("Posts/" + postimi_id + "/Comments")
                .addSnapshotListener(KomentoActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (!documentSnapshots.isEmpty()) {

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    String commentId = doc.getDocument().getId();
                                    Komentet comments = doc.getDocument().toObject(Komentet.class);
                                    lista_komenteve.add(comments);
                                    komentetRecyclerAdapter.notifyDataSetChanged();


                                }
                            }

                        }

                    }
                });

        btn_komento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment_message = fusha_komentit.getText().toString();


                Map<String, Object> commentsMap = new HashMap<>();
                commentsMap.put("message", comment_message);
                commentsMap.put("user_id", user_id);
                commentsMap.put("timestamp", FieldValue.serverTimestamp());

                firebaseFirestore.collection("Posts/" + postimi_id + "/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        if(!task.isSuccessful()){

                            Toast.makeText(KomentoActivity.this, "Error  : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        } else {

                            fusha_komentit.setText("");

                        }

                    }
                });

            }
        });


    }
}
