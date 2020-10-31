package com.canada.cardelar.application.PlacesSearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.canada.cardelar.application.DataBase.SQLiteDataBase;
import com.canada.cardelar.application.Models.PlacesInformation;
import com.canada.cardelar.application.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

public class TestingActivity extends AppCompatActivity {
    static TextView textView;
   static ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    int deleteID;
    FirebaseStorage storage;
    StorageReference storageReference;
    String gloablePlaceID=null;
    Button upload, delete;
    static Bitmap  uploadableBitMap=null;
    SQLiteDataBase sqLiteDataBase = new SQLiteDataBase(this);
    ImageView iv;
    Button button;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
button=findViewById(R.id.button);
button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        showDialg();
        uploadObject("forcelayUploadded");
    }
});
        textView = findViewById(R.id.textView8);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
      iv = (ImageView) findViewById(R.id.imageView);
        upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {//getImage
            @Override
            public void onClick(View v) {
               gloablePlaceID = sqLiteDataBase.getPlaceID();
               Cursor cursor=sqLiteDataBase.getSinglePlace(gloablePlaceID);
               cursor.moveToFirst();
            gloablePlaceID= cursor.getString(16);
           showToast(gloablePlaceID);
              String photoReference=cursor.getString(14);
              if(photoReference.equals("")){
                  uploadObject("");
                  showToast("no image");
                  showDialg();
              }else {
                  showDialg();
                  showToast(photoReference);

                  String imgURL  = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1024&photoreference="+photoReference+"&key=AIzaSyAaA9L4KHQty4lT89HEUOzFTXrGCGvdkKw";
                  new DownLoadImageTask(iv).execute(imgURL);
              }
            }
        });
        delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//upload

         uploadImage();




            }
        });
        textView.setText("");  // Display Total
textView.setText(String.valueOf(sqLiteDataBase.countPlaces()));


    }

    private void showToast(String gloablePlaceID) {
        Toast.makeText(getApplicationContext(),gloablePlaceID,
                Toast.LENGTH_LONG).show();
    }

    private boolean uploadImage() {
        showDialg();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
       uploadableBitMap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        String path = MediaStore.Images.Media.insertImage(
                getContentResolver(), uploadableBitMap, null, null);
        Uri filePath = Uri.parse(path);
        if (filePath != null) {
            // Defining the child of storageReference
            final String imageUrl = "carimages/" + UUID.randomUUID().toString();
            StorageReference ref
                    = storageReference
                    .child(imageUrl);

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                   showToast("image uploadded successfuulllyyy");
progressDialog.dismiss();
                                   uploadObject(imageUrl);
                                   showDialg();

                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                            // Error, Image not uploaded

                            Toast
                                    .makeText(TestingActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });

        }


        return false;
    }

    private void uploadObject(String imageURL) {
 Cursor cursor=sqLiteDataBase.getSinglePlace(gloablePlaceID);
 cursor.moveToFirst();
     final int id=cursor.getInt(0);
        PlacesInformation placesInformation=new PlacesInformation(
          cursor.getString(16),
          cursor.getString(9),
          cursor.getString(11),
          cursor.getString(12),
          cursor.getString(23)  ,
          cursor.getString(20),
                cursor.getString(19),
                cursor.getString(10),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(5),
                cursor.getString(3),
                cursor.getString(6),
                cursor.getString(8),
                cursor.getString(13),
                cursor.getString(14),
                cursor.getString(4),
                cursor.getString(7),
                cursor.getString(18),
                cursor.getString(21),
                cursor.getString(22),
                cursor.getString(24),
                imageURL,
                cursor.getString(17)
        );
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ontario").child(gloablePlaceID);
databaseReference.setValue(placesInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
    @Override
    public void onComplete(@NonNull Task<Void> task) {
        progressDialog.dismiss();

        if(task.isSuccessful()){
          sqLiteDataBase.deletePlace(id);
            showToast("ubject uploadded");
            gloablePlaceID=null;
            uploadableBitMap=null;
            textView.setText("Successfull =  "+String.valueOf(sqLiteDataBase.countPlaces()));
            iv.setImageResource(R.drawable.logo);


        }else {
            textView.setText("not Successfull =  "+String.valueOf(sqLiteDataBase.countPlaces()));
        }

    }
});

    }

    public  void showDialg(){
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("wait sorry");
        progressDialog.setCancelable(false);
        progressDialog.show();


    }
}






class DownLoadImageTask extends AsyncTask<String,Void, Bitmap> {
    ImageView imageView;

    public DownLoadImageTask(ImageView imageView){
        this.imageView = imageView;
    }

    /*
        doInBackground(Params... params)
            Override this method to perform a computation on a background thread.
     */
    protected Bitmap doInBackground(String...urls){
        String urlOfImage = urls[0];
        Bitmap logo = null;
        try{
            InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
            logo = BitmapFactory.decodeStream(is);
        }catch(Exception e){ // Catch the download exception
            e.printStackTrace();
        }
        return logo;
    }

    /*
        onPostExecute(Result result)
            Runs on the UI thread after doInBackground(Params...).
     */
    protected void onPostExecute(Bitmap result){
        TestingActivity.uploadableBitMap=result;
        imageView.setImageBitmap(result);
        TestingActivity.progressDialog.dismiss();
    }
}
