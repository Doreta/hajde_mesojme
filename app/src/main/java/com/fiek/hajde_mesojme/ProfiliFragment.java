package com.fiek.hajde_mesojme;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import com.fiek.hajde_mesojme.R;

import static android.app.Activity.RESULT_OK;

public class ProfiliFragment extends Fragment {


    public ProfiliFragment() {

    }

    private CircleImageView Foto;
    private Uri mainFotoURI = null;

    private String user_id;

    private boolean isChanged = false;

    private TextView Emri;
    private Button ruajBtn;
    private ProgressBar profiliProgress;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private Bitmap compressedImageFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profili, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        Foto = view.findViewById(R.id.foto_profili);
        Emri = view.findViewById(R.id.emri);
        ruajBtn = view.findViewById(R.id.ruaj_btn);
        profiliProgress = view.findViewById(R.id.profili_progress);



        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {

                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        mainFotoURI = Uri.parse(image);

                        Emri.setText(name);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_image);

                        Glide.with(ProfiliFragment.this).setDefaultRequestOptions(placeholderRequest).load(image).into(Foto);


                    }

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();

                }

                profiliProgress.setVisibility(View.INVISIBLE);
               ruajBtn.setEnabled(true);

            }
        });

        ruajBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user_name = Emri.getText().toString();

                if (!TextUtils.isEmpty(user_name) && mainFotoURI != null) {

                    profiliProgress.setVisibility(View.VISIBLE);

                    if (isChanged) {

                        user_id = firebaseAuth.getCurrentUser().getUid();

                        File newImageFile = new File(mainFotoURI.getPath());
                        try {

                            compressedImageFile = new Compressor(getContext())
                                    .setMaxHeight(125)
                                    .setMaxWidth(125)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] thumbData = baos.toByteArray();

                        UploadTask image_path = storageReference.child("profile_images").child(user_id + ".jpg").putBytes(thumbData);

                        image_path.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {
                                    storeFirestore(task, user_name);

                                } else {

                                    String error = task.getException().getMessage();
                                    Toast.makeText(getContext(), "Error : " + error, Toast.LENGTH_LONG).show();

                                    profiliProgress.setVisibility(View.INVISIBLE);

                                }
                            }
                        });

                    } else {

                        storeFirestore(null, user_name);

                    }

                }

            }

        });

        Foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }

            }

        });
        return view;
    }
    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String user_name) {

        Uri downloadUri;

        if(task != null) {

            downloadUri = task.getResult().getUploadSessionUri();

            // Task<Uri> download_uri = task.getStorage().getDownloadUrl();

        } else {

            downloadUri = mainFotoURI;

        }

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", user_name);
        userMap.put("image", downloadUri.toString());

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(getContext(), "Te dhenat u ndryshuan me sukses !.", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(getContext(), MainActivity.class);
                    startActivity(mainIntent);
                    getActivity().finish();

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(getContext(), "Error : " + error, Toast.LENGTH_LONG).show();

                }

                profiliProgress.setVisibility(View.INVISIBLE);

            }
        });


    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(getActivity());

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainFotoURI = result.getUri();
                Foto.setImageURI(mainFotoURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}