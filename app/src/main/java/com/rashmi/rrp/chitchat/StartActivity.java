package com.rashmi.rrp.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class StartActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button mLoginButton;
    private Button mRegButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private ProgressDialog mLoginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mRegButton = findViewById(R.id.startRegButton);
        mLoginButton = findViewById(R.id.startLoginBtn);
        mEmail = findViewById(R.id.getloginEmail);
        mPassword = findViewById(R.id.getLoginPassword);

        mAuth = FirebaseAuth.getInstance();

        mLoginProgress = new ProgressDialog(this);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent regIntent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(regIntent);

            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we check your credentials.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    loginUser(email, password);

                } else {

                    Toast.makeText(StartActivity.this, "Please FillUp", Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {

                    mLoginProgress.dismiss();

                    String currentUserId = mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    mUserDatabase.child(currentUserId).child("device_token").setValue(deviceToken)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();

                                }
                            });


                } else {

                    mLoginProgress.hide();
                    Toast.makeText(StartActivity.this, "Can not signIn. Please check the form and Try again..", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
