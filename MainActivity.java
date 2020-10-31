package com.canada.cardelar.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    Button userRegistrationBtn,carDealerRegistrationBtn;
    FirebaseAuth auth;
    TextView signIN;
    String[] locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        getView();
        checkPermissions();
    }
    private void getView() {
               /*************************start Page*************/
               userRegistrationBtn=findViewById(R.id.userRegistrationBtn);
               carDealerRegistrationBtn=findViewById(R.id.carDelarRegistrationBtn);
               signIN=findViewById(R.id.textViewSignIn);
               signIN.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       startActivity(new Intent(MainActivity.this,UserLogin.class));
                   }
               });
               userRegistrationBtn.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       startActivity(new Intent(MainActivity.this,UserRegistration.class));

                   }
               });
               carDealerRegistrationBtn.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       startActivity(new Intent(MainActivity.this,DealerRegistration.class));
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
                    Toast.makeText(getApplicationContext(),"permissions granted SuccessFully ",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),"permissions dennayed ",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }
}
