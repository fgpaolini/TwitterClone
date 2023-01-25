package com.example.twitterclone.HomePage;

import static com.example.twitterclone.MainActivity.MDATABASE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.twitterclone.AdpTweet.AdpTweet;
import com.example.twitterclone.AdpTweet.AdpUsers;
import com.example.twitterclone.ModelUser.ModelUser;
import com.example.twitterclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private EditText etWord;
    private Button btSearch;
    private ArrayList<ModelUser> found_users, all_users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        etWord = v.findViewById(R.id.editTextTextPersonName);
        btSearch = v.findViewById(R.id.buttonSearch);
        RecyclerView r = v.findViewById(R.id.recycleView);

        found_users = new ArrayList<>();
        all_users = new ArrayList<>();

        fillUsers();

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = etWord.getText().toString();
                if(word.isEmpty()){
                    return;
                }
                found_users = new ArrayList<>();
                for(ModelUser user: all_users){
                    if(user.getUser().contains(word)){
                        found_users.add(user);
                    }
                }
                createRecycleViewUsers(v, r, found_users);
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    public void fillUsers(){
        MDATABASE.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot user: snapshot.getChildren()){
                    ModelUser found_user = new ModelUser("NO_NAME", "NO_USER", "NO_MAIL", "NO_UID", "NO_URL","NO_DESCRIPTION");
                    found_user.setUID(user.getKey());
                    for(DataSnapshot data_user: user.getChildren()){
                        if (data_user.getKey().equals("name")) {
                            found_user.setName(data_user.getValue().toString());
                        } else if (data_user.getKey().equals("email")) {
                            found_user.setMail(data_user.getValue().toString());
                        } else if (data_user.getKey().equals("pic")) {
                            found_user.setURL_image(data_user.getValue().toString());
                        } else if (data_user.getKey().equals("user")) {
                            found_user.setUser(data_user.getValue().toString());
                        } else if (data_user.getKey().equals("description")) {
                            found_user.setUser_description(data_user.getValue().toString());
                        }
                    }
                    all_users.add(found_user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void createRecycleViewUsers(@NonNull View v,RecyclerView recyclerView, ArrayList<ModelUser> list_to_show){
        AdpUsers adpShop_adaptor_2 = new AdpUsers(v.getContext(), list_to_show);
        recyclerView.setLayoutManager(new GridLayoutManager(v.getContext(),2));
        recyclerView.setAdapter(adpShop_adaptor_2);
    }


}