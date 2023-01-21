package com.example.twitterclone.HomePage;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import static com.example.twitterclone.MainActivity.LOGGED_USER;
import static com.example.twitterclone.MainActivity.MDATABASE;
import static com.example.twitterclone.MainActivity.MTSTORAGE;
import static com.example.twitterclone.MainActivity.USER_UID;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.twitterclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class PostFragment extends Fragment implements View.OnClickListener {

    private ImageView ivProfile, ivPost1;
    private EditText etPost;
    private Button btPost;

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Uri imagenUri;

    private DateTimeFormatter FOMATTER;
    private boolean[] position_used = new boolean[3];
    private ArrayList<Uri> used_uri = new ArrayList<>();
    private int select_post = 0;
    private int post_count;
    private boolean post_save;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post, container, false);

        ivProfile = v.findViewById(R.id.imagePostProfile);
        ivPost1 = v.findViewById(R.id.imagePost1);
        ivProfile.setOnClickListener(this);
        ivPost1.setOnClickListener(this);

        etPost = v.findViewById(R.id.etPostFill);
        btPost = v.findViewById(R.id.btPost);


        FOMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy 'at' hh:mm a");


        MDATABASE.child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    if(data.getKey().equals("postCount")){
                            post_count = Integer.parseInt(data.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        post_save = false;
        //Cargador de imagenes
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK && result.getData() != null){
                    //Obtiene datos de imagen
                    Intent data = result.getData();
                    imagenUri = data.getData();
                    ContentResolver contentResolver = getActivity().getContentResolver();
                    try {
                        //Dependiendo de que version sea, ejecutara un codigo u otro
                        Bitmap bitmap;
                        if(Build.VERSION.SDK_INT < 28) {
                            checkPositionIsUsed(select_post);
                            position_used[select_post] = true;
                            used_uri.add(imagenUri);
                            switch (select_post){
                                case 1:
                                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagenUri);
                                    ivPost1.setImageBitmap(bitmap);
                                    break;
                            }
                        }
                        else{
                            ImageDecoder.Source source = ImageDecoder.createSource(contentResolver,imagenUri);
                            bitmap = ImageDecoder.decodeBitmap(source);
                            checkPositionIsUsed(select_post);
                            position_used[select_post] = true;
                            used_uri.add(imagenUri);
                            switch (select_post){
                                case 1:
                                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagenUri);
                                    ivPost1.setImageBitmap(bitmap);
                                    break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(result.getResultCode() == RESULT_CANCELED){
                    Toast.makeText(PostFragment.this.getContext(),"Cancelado...",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String post_text = etPost.getText().toString();

                //Local date time instance
                LocalDateTime localDateTime = LocalDateTime.now();

                if(post_text.isEmpty()){
                    Toast.makeText(PostFragment.this.getContext(), "No puedes dejar vacio el post", Toast.LENGTH_SHORT).show();
                    etPost.requestFocus();
                    return;
                }
                else{
                    //Preparar la pantalla de carga
                    AlertDialog loading_dialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostFragment.this.getContext());
                    builder.setCancelable(false);

                    //Preparar para agregar el layout
                    LayoutInflater inflater = getLayoutInflater();
                    v = inflater.inflate(R.layout.resource_alertdialog_loading, null);

                    //Configurando el layout en el view
                    builder.setView(v);
                    loading_dialog = builder.create();

                    //Mostrar el layout
                    loading_dialog.show();

                    post_count++;
                    MDATABASE.child("Posts").child("Post"+ post_count).child("UID").setValue(USER_UID);
                    MDATABASE.child("Posts").child("Post"+ post_count).child("User").setValue(LOGGED_USER.getUser());
                    MDATABASE.child("Posts").child("Post"+ post_count).child("textPost").setValue(post_text);
                    MDATABASE.child("Posts").child("Post"+ post_count).child("postTime").setValue(ServerValue.TIMESTAMP);
                    MDATABASE.child("Posts").child("Post"+ post_count).child("pic").setValue(LOGGED_USER.getURL_image());
                    MDATABASE.child("Posts").child("Post"+ post_count).child("liked_users").setValue("");
                    MDATABASE.child("Posts").child("postCount").setValue(Integer.toString(post_count));
                    int number_image = 0;
                    if(used_uri.size() != 0){
                        for(Uri data_image: used_uri){
                            number_image++;
                            String number_string_image =  Integer.toString(number_image);
                            StorageReference ubicationImagen = MTSTORAGE.child("Posts").child("Post"+Integer.toString(post_count)).child("Image"+number_string_image+".png");
                            ubicationImagen.putFile(data_image).addOnSuccessListener(taskSnapshot -> ubicationImagen.getDownloadUrl().addOnCompleteListener(task2 -> {
                                Uri imageURL = task2.getResult();
                                MDATABASE.child("Posts").child("Post"+Integer.toString(post_count)).child("Image"+number_string_image).setValue(imageURL.toString());
                                if(!post_save){
                                    post_save = true;
                                    loading_dialog.dismiss();
                                    Toast.makeText(PostFragment.this.getContext(), "Posteado!", Toast.LENGTH_SHORT).show();
                                }
                            }));
                        }
                    }
                    else{
                        loading_dialog.dismiss();
                        Toast.makeText(PostFragment.this.getContext(), "Posteado!", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

        //Rellenar el perfil
        Uri product_image = Uri.parse(LOGGED_USER.getURL_image());
        Glide.with(v).load(String.valueOf(product_image)).into(ivProfile);

        return v;
    }

    @Override
    public void onClick(View v){
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        switch (v.getId()){
            case R.id.imagePost1:
                select_post = 1;
                activityResultLauncher.launch(i);
                break;
        }

    }

    public void checkPositionIsUsed(int position){
        if(position_used[position] == true){
            used_uri.remove(position);
        }
    }
}