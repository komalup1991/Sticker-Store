package edu.northeastern.stickers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.movieapi.R;
import edu.northeastern.stickers.adapters.UserStickerHistoryAdapter;
import edu.northeastern.stickers.models.UserStickerHistory;

public class StickerHistoryFragment extends Fragment {
    private RecyclerView recyclerDisplay;
    private UserStickerHistoryAdapter adapter;
    private List<UserStickerHistory> usersStickerHistoryList;
    DatabaseReference referenceOfUser;
    DatabaseReference referenceOfSticker;
    FirebaseUser user;
    String uid;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        recyclerDisplay = view.findViewById(R.id.recyclerOfDisplay);

        recyclerDisplay.setLayoutManager(new LinearLayoutManager(this.getContext()));
        usersStickerHistoryList = new ArrayList<UserStickerHistory>();

        createListData();

        adapter = new UserStickerHistoryAdapter(this.getContext(), usersStickerHistoryList);
        recyclerDisplay.setAdapter(adapter);
        recyclerDisplay.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayoutManager.VERTICAL));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sticker_history, container, false);
    }

    private void createListData(){
        usersStickerHistoryList = new ArrayList<>();
        referenceOfUser = FirebaseDatabase.getInstance().getReference().child("Users");
        referenceOfSticker = FirebaseDatabase.getInstance().getReference().child("Sticker")
                .child("StickerPack");

        referenceOfUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserStickerHistory newUserHistory;
                List<UserStickerHistory.StickerSentCount>  stickerSentCountList = new ArrayList<>();
                List<UserStickerHistory.StickerReceivedCount> stickerReceivedCountList = new ArrayList<>();
                if (dataSnapshot.child(uid).child("SentHistory").exists()){
                    DataSnapshot sentStickerHistorySnapshot = dataSnapshot.child(uid).child("SentHistory");
                    for (DataSnapshot snapshotChild : sentStickerHistorySnapshot.getChildren()){
                        String sendToUserId = snapshotChild.child("sendToUserID").getValue().toString();
                        String sentStickerId = snapshotChild.child("stickerSentID").getValue().toString();
                        newUserHistory = new UserStickerHistory(
                                dataSnapshot.child(sendToUserId).child("name").getValue().toString(),
                                snapshotChild.child("sentTimestamp").getValue().toString(),
                                sentStickerId);
                        usersStickerHistoryList.add(newUserHistory);
                    }
                }
                adapter.notifyDataSetChanged();

//                    if(!stickerReceivedCountList.isEmpty() && !stickerSentCountList.isEmpty()){
//                        newUserHistory = new UserStickerHistory(snapshot.getKey(),stickerSentCountList,stickerReceivedCountList);
//                    } else if (!stickerSentCountList.isEmpty()){
//                        newUserHistory = new UserStickerHistory(snapshot.getKey(),stickerSentCountList,null);
//                    } else if (!stickerReceivedCountList.isEmpty()){
//                        newUserHistory = new UserStickerHistory(snapshot.getKey(),null,stickerReceivedCountList);
//                    } else{
//                        newUserHistory = new UserStickerHistory(snapshot.getKey(),null,null);
//                    }

//                    usersStickerHistoryList.add(newUserHistory);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        referenceOfSticker.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (UserStickerHistory usersStickerHistory : usersStickerHistoryList){
                    String stickerId = usersStickerHistory.getStickerId();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        for (DataSnapshot snapshot2 : snapshot1.getChildren()){
                            if (snapshot2.getKey().toString().equals(stickerId)){
                                usersStickerHistory.setStickerPath(snapshot2.child("StickerPath").toString());
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}