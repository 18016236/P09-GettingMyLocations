package com.example.p09_gettingmylocations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {

    Button btnStart, btnStop,btnCheck;
    LocationRequest mLocationRequest;

    String folderLocation;
    TextView tvLat,tvLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnCheck = findViewById(R.id.btnCheck);

        int permissionCheck_Storage = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        final FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);


        final LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location data = locationResult.getLastLocation();
                    String msg = "New Loc Detected\n"+"Lat: "+ data.getLatitude()+","+"Lng: "+data.getLongitude();
                    Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                }
            };
        };
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,MyService.class);
                startService(i);
                if (checkPermission() == true){
                    Task<Location> task= client.getLastLocation();
                    task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location !=null){
                                String msg = "Lat: " + location.getLatitude() + "Lng: "+location.getLongitude();
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }else{
                                String msg = "No Last Known Location found";
                                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                File targetFile = new File (folderLocation,"data.txt");
                try {
                    FileWriter writer = new FileWriter(targetFile,true);
                    writer.write("Hello World"+"\n");
                    writer.flush();
                    writer.close();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this,"Failed to Write!",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(MainActivity.this,MyService.class);
                stopService(i);
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,MyService.class);
                startActivity(i);

                String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/p09";
                File targetFile = new File(folderLocation,"data.txt");

                if (targetFile.exists()){
                    String data = "";
                    try {
                        FileReader reader = new FileReader(targetFile);
                        BufferedReader br = new BufferedReader(reader);
                        String line = br.readLine();
                        while (line != null){
                            data += line+"\n";
                            line = br.readLine();
                        }
                        br.close();
                        reader.close();
                    }catch (Exception e ){
                        Toast.makeText(MainActivity.this,"Failed to read!",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this,data,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"Record file doesn't exist",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkPermission() {
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck_Storage = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                && permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED
                && permissionCheck_Storage == PermissionChecker.PERMISSION_GRANTED
        ) {
            return true;
        } else {
            return false;
        }
        };

}