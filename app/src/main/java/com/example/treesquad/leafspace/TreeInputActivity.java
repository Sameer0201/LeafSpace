package com.example.treesquad.leafspace;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.treesquad.leafspace.db.api.Api;
import com.example.treesquad.leafspace.db.TreeRecord;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.GeoPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TreeInputActivity extends AppCompatActivity{
    private Button submitButton;
    private Button getCurrentLocationButton;
    private TextView locationText;
    private EditText speciesEditText;
    private ImageView mImageView;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location lastLocation;


    private Api api;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private Bitmap submitBitmap;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_tree_input_2);


        //getSupportActionBar().setTitle("Add New Tree");
        submitButton = findViewById(R.id.AddTreeSubmitButton);
        getCurrentLocationButton = findViewById(R.id.getLocationButton);
        locationText = findViewById(R.id.TreeLocationTextView);
        speciesEditText = findViewById(R.id.TreeTypeEditText);
        mImageView = findViewById(R.id.TreeInputImage);



        //mImageView.setOnClickListener(v -> DispatchTakePictureIntent());
        //submitButton.setOnClickListener(this::submitTree);
        //getCurrentLocationButton.setOnClickListener(v -> GetLocation());


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        api = Api.getInstance();

    }

    private void submitTree(View view) {

        if(speciesEditText.getText().equals(""))
        {
            Toast.makeText(this,"You need to add a species type!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(lastLocation == null)
        {
            Toast.makeText(this,"You need to add a location!", Toast.LENGTH_SHORT).show();
            return;
        }


        /*
        String[] values = locationEditText.getText().toString().split(",");
        double lat = 0, lon = 0;
        if (values.length >= 2) {
            lat = Double.parseDouble(values[0]);
            lon = Double.parseDouble(values[1]);
        }
        */

        // TODO: replace this with the actual image
       // Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.raw.defaulttree1);

        TreeRecord record = new TreeRecord.Builder(new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude()))
                .species(speciesEditText.getText().toString())
                .image(submitBitmap)
                .user()
                .build();

        Toast.makeText(this,"Processing Submission...",Toast.LENGTH_LONG).show();
        api.putTree(record, (treeRecord, success) -> {
            // TODO: see if it worked here
            if(success)
            {
                Log.d("LeafSpace", "Tree submitted successfully!");
                Toast.makeText(this,"Tree submitted successfully!",Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {
                Log.d("LeafSpace", "Tree submission unsuccessful");
                Toast.makeText(this,"Tree submission unsuccessful :(",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DispatchTakePictureIntent() { //Straight from android dev guide
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = CreateImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("LeafSpace","Error creating image file!");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.treesquad.leafspace.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

       /* Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }*/
    }
    @Override //straight from android dev guide
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if(data!=null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                mImageView.setImageBitmap(imageBitmap);
            }

            Bitmap bitmap = null;
            File file = new File(mCurrentPhotoPath);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.fromFile(file));
            }
            catch(IOException e)
            {
                Log.d("LeafSpace","Photo wasn't found!");
            }
            if (bitmap != null) {
                mImageView.setImageBitmap(bitmap);
                submitBitmap = bitmap;
            }

        }
    }

    private File CreateImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "LeafSpace_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void GetLocation()
    {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        lastLocation = location;
                        locationText.setText("Tree Location: " + location.getLatitude() + "," + location.getLongitude());
                    }
                }
            });
        }
        else
        {
            Toast.makeText(getApplicationContext(),"App does not have permission to access location!", Toast.LENGTH_SHORT).show();
        }
    }



}

