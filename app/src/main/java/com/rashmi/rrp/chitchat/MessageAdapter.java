package com.rashmi.rrp.chitchat;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;


    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public TextView displayName;
        public TextView displayTime;
        public CircleImageView profileImage;
        public ImageView messageImage;

        public MessageViewHolder(View view) {
            super(view);

            messageText = view.findViewById(R.id.messageTextLayout);
            profileImage = view.findViewById(R.id.messageProfileLayout);
            messageImage = view.findViewById(R.id.messageImageLayout);
            displayName = view.findViewById(R.id.nameTextLayout);
            displayTime = view.findViewById(R.id.timeTextLayout);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();

        Messages c = mMessageList.get(position);

        String fromUser = c.getFrom();
        String messageType = c.getType();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUser);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                holder.displayName.setText(name);

                Picasso.with(holder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.default_avatar).into(holder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(messageType.equals("text")) {

            holder.messageText.setText(c.getMessage());
//            holder.displayTime.setText(c.getTime());
            holder.messageImage.setVisibility(View.INVISIBLE);

        } else {

            holder.messageText.setVisibility(View.INVISIBLE);
            Picasso.with(holder.profileImage.getContext()).load(c.getMessage())
                    .placeholder(R.drawable.default_avatar).into(holder.messageImage);

        }


    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

}
