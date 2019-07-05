package com.fiek.hajde_mesojme;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    public List<Postimet> lista;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public RecyclerAdapter(List<Postimet> lista){

        this.lista = lista;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_postimeve, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String PostimiId = lista.get(position).PostimetID;
        final String UserId = firebaseAuth.getCurrentUser().getUid();

        String pershkrimi = lista.get(position).getpershkrimi();
        holder.setPershkrimi(pershkrimi);

        String image_url = lista.get(position).getfoto_url();
        String thumbUri = lista.get(position).getfoto_thumb();
        holder.setFoto(image_url, thumbUri);

        String user_id = lista.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String userName = task.getResult().getString("name");
                    String usrFoto = task.getResult().getString("image");

                    holder.setUserData(userName, usrFoto);


                } else {

                    //Firebase Exception

                }

            }
        });

        try {
            long millisecond = lista.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setTime(dateString);
        } catch (Exception e) {

            Toast.makeText(context, "Gabimi : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        //Get Likes Count
        firebaseFirestore.collection("Posts/" + PostimiId + "/Likes").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    holder.updatePelqimet(count);

                } else {

                    holder.updatePelqimet(0);

                }

            }
        });


        //Get Likes
        firebaseFirestore.collection("Posts/" + PostimiId + "/Likes").document(UserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){

                    holder.pelqejBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));

                } else {

                    holder.pelqejBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));

                }

            }
        });

        //Likes Feature
        holder.pelqejBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts/" + PostimiId + "/Likes").document(UserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){

                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + PostimiId + "/Likes").document(UserId).set(likesMap);

                        } else {

                            firebaseFirestore.collection("Posts/" + PostimiId + "/Likes").document(UserId).delete();

                        }

                    }
                });
            }
        });

        holder.komentoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, KomentoActivity.class);
                commentIntent.putExtra("post_id", PostimiId);
                context.startActivity(commentIntent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView Pershkrimi;
        private ImageView FotoPostimit;
        private TextView DataPostimit;

        private TextView UserName;
        private CircleImageView UserFoto;

        private ImageView pelqejBtn;
        private TextView numeroPelqimet;

        private ImageView komentoBtn;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            pelqejBtn = mView.findViewById(R.id.pelqej_btn);
            komentoBtn = mView.findViewById(R.id.foto_koment);

        }

        public void setPershkrimi(String descText){

            Pershkrimi = mView.findViewById(R.id.pershkrimi);
            Pershkrimi.setText(descText);

        }

        public void setFoto(String downloadUri, String thumbUri){

            FotoPostimit = mView.findViewById(R.id.foto);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.image_placeholder);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(FotoPostimit);

        }

        public void setTime(String date) {

            DataPostimit = mView.findViewById(R.id.data_postimit);
            DataPostimit.setText(date);

        }

        public void setUserData(String name, String image){

            UserFoto = mView.findViewById(R.id.user_foto);
            UserName = mView.findViewById(R.id.emri);

            UserName.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(UserFoto);

        }

        public void updatePelqimet(int count){

            numeroPelqimet = mView.findViewById(R.id.numeruesi_pelqimeve);
            numeroPelqimet.setText(count + " Likes");

        }

    }

}
