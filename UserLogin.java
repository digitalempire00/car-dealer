package com.canada.cardelar.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.canada.cardelar.application.MiscHelper.MiscHelperClass;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class UserLogin extends AppCompatActivity {
    EditText editTextLoginUserEmail,editTextLoginUserPassword;
    Button loginconform;
    TextView userloginTagDisplayNonUsable,textViewloginSignup;


    ProgressDialog dialog;
    FirebaseAuth auth;

    MiscHelperClass miscHelperClass=new MiscHelperClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        auth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().goOnline();
        initView();

    }

    private void initView() {
        editTextLoginUserEmail=findViewById(R.id.editTextLoginUserEmail);
        editTextLoginUserPassword=findViewById(R.id.editTextLoginUserPassword);

        loginconform=findViewById(R.id.loginconform);

        loginconform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
        userloginTagDisplayNonUsable=findViewById(R.id.userloginTagDisplayNonUsable);
        textViewloginSignup=findViewById(R.id.textViewloginSignup);
        textViewloginSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(UserLogin.this, UserRegistration.class));
            }
        });

    }
    private void doLogin(){
        String Email= editTextLoginUserEmail.getText().toString();
        String Password=editTextLoginUserPassword.getText().toString();
        if(Email.equals("") || Password.equals("")){
            ToastMessage("please provide Both Email and Password");
        }else if(miscHelperClass.isEmailValid(Email)) {
            dialog = new ProgressDialog(this);// create dialog
            dialog.setMessage("Please Wait, Account is Logging You In.");
            dialog.show();
            auth.signInWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                dialog.dismiss();
                                Intent loginIntent = new Intent(UserLogin.this, GoogleMapNavigation.class);
                                startActivity(loginIntent);
                                finish();
                            } else {
                                ToastMessage("Invalid email or password entered, please try again...");
                                dialog.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ToastMessage(e.getMessage());
                }
            });
        }else {
            ToastMessage("InValid Email format ");
        }
    }
    private void ToastMessage(String message){
        Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_SHORT
        ).show();
    }
}