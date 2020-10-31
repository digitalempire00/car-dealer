package com.canada.cardelar.application;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.canada.cardelar.application.Models.Dealer;
import com.canada.cardelar.application.Models.UtilityPermissions;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@SuppressWarnings("deprecation")
public class DealerRegistration extends AppCompatActivity  {
    EditText editTextTitle,
            editTextOwnerName,
            editTextRegistrationNumber,
            editTextDescription,
            editTextContactNumber,
            editTextEmail,
            editTextPassword,
            editTextRepeatPassword;
    Button buttonNext;

    Bitmap profileBtm, coverPhotoBtm;
    ImageView imageViewProfilePhoto, imageViewCoverPhoto;
    private String userChoosenTask="none";
    private int  SELECT_FILE = 1;
    private Uri filePathCover = null;
    private Uri filePathProfile = null;
    FirebaseStorage storage;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    BitmapUtility bitmapUtility = new BitmapUtility();
    MiscHelperClass miscHelperClass=new MiscHelperClass();
    String[] locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_registration);

        getView();
        intiClickListener();
        iniBitsMap();
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().goOnline();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

    }

    private void intiClickListener() {
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();

            }
        });
        imageViewProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
                userChoosenTask="profile";

            }
        });
        imageViewCoverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           selectImage();
                userChoosenTask="cover";
            }
        });

    }

    private void validateData() {

       String message= miscHelperClass.dealerValidate(editTextTitle.getText().toString(),
                editTextOwnerName.getText().toString(),
                editTextRegistrationNumber.getText().toString(),
                editTextDescription.getText().toString(),
                editTextContactNumber.getText().toString(),
                editTextEmail.getText().toString(),
                editTextPassword.getText().toString(),
                editTextRepeatPassword.getText().toString());
       if(!message.equals("perfectData")){
           showToast(message,"missing");
       }else if(filePathProfile==null && filePathCover==null){
           showToast("Do you want to perform Registration without cover and profile images","skip");
       }else if(filePathProfile==null || filePathCover==null){

           if(filePathProfile==null)
               showToast("Do you want to perform Registration without  profile image","skip");
            else if(filePathCover==null)
               showToast("Do you want to perform Registration without  cover image","skip");
       }else {
           //Every thing perfect
if(checkPermissions()) {
    doAllStuff();
}
       }


    }

    private void doAllStuff() {
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Wait...");
        progressDialog.setMessage("Uploading Images ...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String coverURL=  uploadImage(filePathCover);
        String profileURL=  uploadImage(filePathCover);

        createAuthAndUploadData(coverURL,profileURL);
    }

    /***********upload data Methods ************/
    private String uploadImage(Uri filePath) {
    String imageUrl="";
        if (filePath != null) {
            imageUrl="images/" + UUID.randomUUID().toString();
            StorageReference ref
                    = storageReference
                    .child(imageUrl);
            Toast.makeText(getApplicationContext(),imageUrl,Toast.LENGTH_LONG).show();
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast
                                    .makeText(DealerRegistration.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        }
        return imageUrl;
    }
    private void createAuthAndUploadData(final String coverUrl, final String profileUrl) {
        progressDialog.setMessage("creating Auth...");





        auth.createUserWithEmailAndPassword(editTextEmail.getText().toString(),editTextPassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Dealer dealer = new Dealer(editTextTitle.getText().toString(),
                                    editTextOwnerName.getText().toString(),
                                    editTextRegistrationNumber.getText().toString(),
                                    editTextDescription.getText().toString(),
                                    editTextContactNumber.getText().toString(),
                                    coverUrl,
                                    profileUrl,
                                    editTextEmail.getText().toString(),
                                    editTextPassword.getText().toString(),
                                    0.00,
                                    0.00,
                                    "",
                                    "",
                                    "0"
                                    );
                            FirebaseUser user = auth.getCurrentUser();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                                    getReference().child("Dealer").child(user.getUid());
                            databaseReference.setValue(dealer)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(DealerRegistration.this, "Welcome.. Account has been successfully registered.", Toast.LENGTH_SHORT).show();


                                                Intent myIntent = new Intent(DealerRegistration.this, LocationSetting.class);
                                                startActivity(myIntent);
                                                finish();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "ERROR: The account could not be registered, Please try again.", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Unregistered",Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
    private void getView() {
        imageViewProfilePhoto = findViewById(R.id.profile);
        imageViewCoverPhoto = findViewById(R.id.cover);
        imageViewProfilePhoto.setAdjustViewBounds(true);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextOwnerName = findViewById(R.id.editTextOwinerName);
        editTextRegistrationNumber = findViewById(R.id.editTextRegistrationNumber);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextContactNumber = findViewById(R.id.editTextContactNumber);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRepeatPassword = findViewById(R.id.editTextRepeatPassword);
        buttonNext = findViewById(R.id.buttonNext);
    }
    private void iniBitsMap() {
        profileBtm = BitmapFactory.decodeResource(getResources(), R.drawable.simple_placeholder);
        profileBtm = bitmapUtility.getCircularBitmap(profileBtm);
        profileBtm = bitmapUtility.addBorderToCircularBitmap(profileBtm, 15, getResources().getColor(R.color.buttonColors));
        imageViewProfilePhoto.setImageBitmap(profileBtm);
        coverPhotoBtm = BitmapFactory.decodeResource(getResources(), R.drawable.simple_placeholder);
        coverPhotoBtm = bitmapUtility.setTopRounded(coverPhotoBtm, coverPhotoBtm.getWidth(), coverPhotoBtm.getHeight());
        imageViewCoverPhoto.setImageBitmap(coverPhotoBtm);
        imageViewCoverPhoto.setAdjustViewBounds(true);
    }

    private void selectImage() {
        final boolean result = UtilityPermissions.checkPermission(DealerRegistration.this);
        final Dialog photoDialog = new Dialog(this);
        photoDialog.setContentView(R.layout.photo_dialog);
        photoDialog.show();
        ImageView library =photoDialog.findViewById(R.id.imageViewLibrary);
        Button cancel =photoDialog.findViewById(R.id.cancel);
        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (result) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                    photoDialog.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoDialog.dismiss();

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE){
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
                    if(userChoosenTask.equals("cover"))
                    filePathCover=data.getData();
                    else if(userChoosenTask.equals("profile"))
                        filePathProfile=data.getData();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(bm!=null){
            if(userChoosenTask.equals("cover")){
               bm = bitmapUtility.setTopRounded(bm, bm.getWidth(), bm.getHeight());
                imageViewCoverPhoto.setImageBitmap(bm);
                imageViewCoverPhoto.setAdjustViewBounds(true);

            }else if(userChoosenTask.equals("profile")){
                bm = bitmapUtility.getCircularBitmap(bm);
                bm = bitmapUtility.addBorderToCircularBitmap(bm, 15, getResources().getColor(R.color.buttonColors));
                imageViewProfilePhoto.setImageBitmap(bm);

            }
        }else{
            Toast.makeText(getApplicationContext(),"please Try Again",Toast.LENGTH_SHORT).show();
        }
    }
    private void showToast(String Message,String messageType){
        final Dialog dialogMessage = new Dialog(this);
        dialogMessage.setContentView(R.layout.alret_dialog);
        dialogMessage.setCancelable(false);
        dialogMessage.show();
        LinearLayout linearLayoutSkip,linearLayoutAction;
        Button cancelBtn,yesBtn,okBtn;
        TextView textViewAlert,textViewMessage;

        linearLayoutSkip=dialogMessage.findViewById(R.id.linearLayoutSkip);
        linearLayoutAction=dialogMessage.findViewById(R.id.linearLayoutAction);
        cancelBtn=dialogMessage.findViewById(R.id.cancel);
        yesBtn=dialogMessage.findViewById(R.id.yes);
        okBtn=dialogMessage.findViewById(R.id.ok);
        textViewAlert=dialogMessage.findViewById(R.id.textViewAlert);
        textViewMessage=dialogMessage.findViewById(R.id.textViewMessage);
        if (messageType.equals("skip")){
            textViewAlert.setText("Do you want to continue ");
            linearLayoutSkip.setVisibility(View.GONE);
            linearLayoutAction.setVisibility(View.VISIBLE);
        }else if(messageType.equals("missing")) {
            textViewAlert.setText("Data Missing");
            linearLayoutSkip.setVisibility(View.VISIBLE);
            linearLayoutAction.setVisibility(View.GONE);
        }

        textViewMessage.setText(Message);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMessage.dismiss();

            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMessage.dismiss();
              if(checkPermissions()  ){
                  doAllStuff();
              }
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMessage.dismiss();

            }
        });

    }



    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : locationPermissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) { //this is okay yes
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            for (int i = 0; i < permissions.length; i++) {
                //   String permission = permissions[i];
                int grantResult = grantResults[i];

                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),"permissions granted press again Next ",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),"permissions dennayed ",Toast.LENGTH_LONG).show();

                }
            }

        }

    }



}

