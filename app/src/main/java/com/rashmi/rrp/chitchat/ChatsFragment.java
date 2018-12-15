package com.rashmi.rrp.chitchat;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private RecyclerView mConvList;

    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrentUserId;

    private View mMainView;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);

        mConvList = mMainView.findViewById(R.id.convList);
        mAuth = FirebaseAuth.getInstance();

        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrentUserId);

        mConvDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrentUserId);
        mUsersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);


        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query conversationQuery = mConvDatabase.orderByChild("timestamp");

        FirebaseRecyclerOptions<Conv> options =
                new FirebaseRecyclerOptions.Builder<Conv>()
                .setQuery(conversationQuery, Conv.class)
                .build();

        FirebaseRecyclerAdapter<Conv, ConvViewHolder> firebaseConvAdapter = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ConvViewHolder holder, int position, @NonNull final Conv model) {

                final String listUserId = getRef(position).getKey();

                Query lastMessageQuery = mMessageDatabase.child(listUserId).limitToLast(1);

                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String data = dataSnapshot.child("message").getValue().toString();
                        holder.setMessage(data, model.isSeen());

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mUsersDatabase.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
//                        String userStatus = dataSnapshot.child("status").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if(dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(userOnline);

                        }

                        holder.setName(userName);
//                        holder.setStatus(userStatus);
                        holder.setUserImage(userThumb, getContext());

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra("userId", listUserId);
                                chatIntent.putExtra("userName", userName);
                                startActivity(chatIntent);

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ConvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new ConvViewHolder(view);
            }
        };

        mConvList.setAdapter(firebaseConvAdapter);
        firebaseConvAdapter.startListening();

    }

    public static class ConvViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ConvViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setMessage(String message, boolean isSeen){

            TextView userStatusView = mView.findViewById(R.id.userSingleStatus);
            userStatusView.setText(message);

            if(!isSeen){
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
            } else {
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
            }

        }

        public void setName(String name){

            TextView userNameView = mView.findViewById(R.id.userSingleName);
            userNameView.setText(name);

        }

//        public void setStatus(String status) {
//
//            TextView userStatusView = mView.findViewById(R.id.userSingleStatus);
//            userStatusView.setText(status);
//
//        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = mView.findViewById(R.id.userSingleImage);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

        }

        public void setUserOnline(String online_status) {

            ImageView userOnlineView = mView.findViewById(R.id.userSingleOnlineIcon);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }


    }

}




















