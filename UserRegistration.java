package com.canada.cardelar.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.canada.cardelar.application.MiscHelper.MiscHelperClass;
import com.canada.cardelar.application.Models.BitmapUtility;
import com.canada.cardelar.application.Models.User;
import com.canada.cardelar.application.Models.UtilityPermissions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class UserRegistration extends AppCompatActivity {
    EditText editTextUserRegistrationName,editTextUserRegistrationEmail,
            editTextUserRegistrationPhoneNumber,editTextUserRegistrationPassword;
    Button buttonUserRegistrationSubmit;
    TextView textViewUserRegistrationNotUsable,textViewUserRegistrationLogin;
    LinearLayout SignUpLinearLayout;
    BitmapUtility bitmapUtility=new BitmapUtility();
    ProgressDialog dialog;
    FirebaseAuth auth;
    ImageView imageViewProfile;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Uri filePath=null;
    String imageUrl=null;
    String userChoosenTask="";
    FirebaseStorage storage;
    StorageReference storageReference;
    MiscHelperClass miscHelperClass=new MiscHelperClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        auth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().goOnline();

       initView();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }
    private void initView() {
        imageViewProfile=findViewById(R.id.profileImage);
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        ImageViewRounded(BitmapFactory.decodeResource(getResources(), R.drawable.user_place_holder));
        editTextUserRegistrationName=findViewById(R.id.editTextUserRegistrationName);
        editTextUserRegistrationEmail=findViewById(R.id.editTextUserRegistrationEmail);
        editTextUserRegistrationPhoneNumber=findViewById(R.id.editTextUserRegistrationPhoneNumber);
        editTextUserRegistrationPassword=findViewById(R.id.editTextUserRegistrationPassword);
        buttonUserRegistrationSubmit=findViewById(R.id.buttonUserRegistrationSubmit);
        buttonUserRegistrationSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUSerRegistration();
            }
        });
        textViewUserRegistrationNotUsable=findViewById(R.id.textViewUserRegistrationNotUsable);
        textViewUserRegistrationLogin=findViewById(R.id.textViewUserRegistrationLogin);
        textViewUserRegistrationLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(UserRegistration.this,UserLogin.class));
            }
        });
        SignUpLinearLayout=findViewById(R.id.SignUpLinearLayout);
    }
    private  void ImageViewRounded(Bitmap profileBtm){
        profileBtm = bitmapUtility.getCircularBitmap(profileBtm);
        profileBtm = bitmapUtility.addBorderToCircularBitmap(profileBtm, 15, getResources().getColor(R.color.buttonColors));
        imageViewProfile.setImageBitmap(profileBtm);
    }
    private void doUSerRegistration() {
        final String Name=editTextUserRegistrationName.getText().toString();
        final String Email=editTextUserRegistrationEmail.getText().toString();
        final String Number=editTextUserRegistrationPhoneNumber.getText().toString();
        final String Password=editTextUserRegistrationPassword.getText().toString();
        if(Name.equals("") || Email.equals("") || Number.equals("") || Password.equals("") || filePath==null){
            ToastMessage("Please Provide all input data ");
        }else {
            if(!miscHelperClass.isEmailValid(Email)){
                ToastMessage("email is InValid");
            }else {  // everything valid do registration
                dialog = new ProgressDialog(this);// create dialog
                dialog.setMessage("please Wait Account Logging");
                dialog.setTitle("Wait...");
                dialog.show();
                auth.createUserWithEmailAndPassword(Email,Password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    uploadImage();
                                }
                                else
                                {
                                    dialog.dismiss();
                                    ToastMessage("Unregistered Try Again");
                                }
                            }
                        });
            }
        }
    }
    private boolean uploadImage() {
        if (filePath != null) {
            // Defining the child of storageReference
            imageUrl="images/" + UUID.randomUUID().toString();
            StorageReference ref
                    = storageReference
                    .child(imageUrl);
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    uploadObject(imageUrl);
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            ToastMessage("Failed please Try Again");
                            dialog.dismiss();
                        }
                    });
        }
        return false;
    }

    private void uploadObject(String imageUrl) {
        User obj = new User(editTextUserRegistrationName.getText().toString(),
        editTextUserRegistrationEmail.getText().toString(),
        editTextUserRegistrationPhoneNumber.getText().toString(),
        editTextUserRegistrationPassword.getText().toString(),imageUrl);
        FirebaseUser user = auth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                getReference().child("User").child(user.getUid());
        databaseReference.setValue(obj)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            ToastMessage("Welcome.. Account has been successfully registered.");
                            Intent myIntent = new Intent(UserRegistration.this, GoogleMapNavigation.class);
                            startActivity(myIntent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "ERROR: The account could not be registered, Please try again.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });


    }

    private void selectImage() {
        final boolean result=UtilityPermissions.checkPermission(UserRegistration.this);
        final Dialog photoDialog=new Dialog(this);
        photoDialog.setContentView(R.layout.photo_dialog);
        photoDialog.show();

        ImageView library=(ImageView)photoDialog.findViewById(R.id.imageViewLibrary);
        Button cancel=(Button)photoDialog.findViewById(R.id.cancel);


        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userChoosenTask ="Choose from Library";
                if(result)
                    galleryIntent();
                photoDialog.dismiss();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoDialog.dismiss();

            }
        });

    }
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction( Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            }

        }
    }
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                if(bm!=null){
                    filePath=data.getData();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(bm!=null){
           ImageViewRounded(bm);
        }else{
            Toast.makeText(getApplicationContext(),"Failed to upload image try again",Toast.LENGTH_SHORT).show();
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