package com.example.calories;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, DataHandler {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_TAKE_IMAGE = 1;
    private Button Sbutton;
    private StorageReference mStorageRef;
    private static String imagePath = "";
    private CommunicationHandler handler;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_IMAGE && resultCode == Activity.RESULT_OK) {
            loadImage();
        }
    }

    private void loadImage() {
        if (imagePath.isEmpty()) return;

        Bitmap myPictureBitmap = BitmapFactory.decodeFile(imagePath);
        Log.i(TAG, "loadImage: Bitmap Loaded ");

        uploadFile(myPictureBitmap);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = CommunicationHandler.getInstance(this, this);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Sbutton = findViewById(R.id.Tbutton);
        Sbutton.setOnClickListener(this);
        getSupportActionBar().hide();
    }

    @Override
    public void onClick(View view) {
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
            dispatchTakePictureIntent();
        }else{
            requestStoragePermission();
        }
    }
    public void requestStoragePermission(){
        ActivityCompat.requestPermissions(this
                ,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},1234);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1234:if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }

            default:super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }


    private void dispatchTakePictureIntent() {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir.getAbsolutePath() + "/my_picture.jpg");
        imagePath = image.getAbsolutePath();
        Uri imageUri = FileProvider.getUriForFile(
                MainActivity.this,
                "com.example.calories.provider", //(use your app signature + ".provider" )
                image);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(takePictureIntent, REQUEST_TAKE_IMAGE);
    }
    private void uploadFile(Bitmap bitmap) {
        handler.sendImage(bitmap);
        Toast.makeText(this, "Uploading Image", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDataReceived(Data data) {
        Log.d(TAG, "onDataReceived() called with: data = [" + data + "]");
    }

//    private void uploadFile(Bitmap bitmap) {
//        Toast.makeText(this, "Uploading Image", Toast.LENGTH_LONG).show();
//        StorageReference mountainImagesRef = mStorageRef.child("images/" + String.valueOf(SystemClock.currentThreadTimeMillis()) + ".jpg");
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//        byte[] data = byteArrayOutputStream.toByteArray();
//        UploadTask uploadTask = mountainImagesRef.putBytes(data);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//                        Log.d("downloadUrl-->", "" + task.getResult());
//                        Toast.makeText(MainActivity.this, "Uploading Image Done", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }
}

