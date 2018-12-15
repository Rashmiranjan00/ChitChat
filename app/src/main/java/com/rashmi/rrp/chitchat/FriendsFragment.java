package com.rashmi.rrp.chitchat;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
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
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrentUserId;

    private View mMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendList = mMainView.findViewById(R.id.friendsList);
        mAuth = FirebaseAuth.getInstance();

        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUserId);
        mFriendsDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(getContext()));





        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(mFriendsDatabase, Friends.class)
                        .build();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull final Friends model) {

                //holder.setDate(model.getDate());

                final String listUserId = getRef(position).getKey();

                mUsersDatabase.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userStatus = dataSnapshot.child("status").getValue().toString();
                        String userThumbImage = dataSnapshot.child("thumb_image").getValue().toString();
                        //String userOnline = dataSnapshot.child("online").getValue().toString();

                        if(dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(userOnline);

                        }

                        holder.setName(userName);
                        holder.setStatus(userStatus);
                        holder.setUserImage(userThumbImage, getContext());

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence options[] = new CharSequence[]{"Open Profile", "Send Message"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //CLICK EVENT FOR EACH ITEM
                                        if(which == 0) {

                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                            profileIntent.putExtra("userId", listUserId);
                                            startActivity(profileIntent);

                                        }

                                        if(which == 1) {

                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("userId", listUserId);
                                            chatIntent.putExtra("userName", userName);
                                            startActivity(chatIntent);

                                        }

                                    }
                                });

                                builder.show();

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
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new FriendsViewHolder(view);
            }
        };


        mFriendList.setAdapter(friendsRecyclerViewerAdapter);
        friendsRecyclerViewerAdapter.startListening();

    }

    private class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setStatus(String status) {

            TextView userStatusView = mView.findViewById(R.id.userSingleStatus);
            userStatusView.setText(status);

        }

        public void setName(String name) {

            TextView userNameView = mView.findViewById(R.id.userSingleName);
            userNameView.setText(name);

        }

        public void setUserImage(String thumbImage, Context ctx) {

            CircleImageView userImageView = mView.findViewById(R.id.userSingleImage);
            Picasso.with(ctx).load(thumbImage).placeholder(R.drawable.default_avatar).into(userImageView);

        }

        public void setUserOnline(String onlineStatus) {

            ImageView userOnlineView = mView.findViewById(R.id.userSingleOnlineIcon);

            if(onlineStatus.equals("true")) {

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }

    }
}





















