package com.dc.menu_master.ImageTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dc.menu_master.MainActivity;
import com.dc.menu_master.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;

/**
 * This AsyncTask will upload image from internal storage to Google Cloud Storgae.
 */

public class ImageUploadTask extends AsyncTask<String, Void, Void> {
    private String imagePath;
    private FirebaseStorage storage;
    private WeakReference<Activity> mActivityReference;
    private ProgressDialog progressDialog;

    public ImageUploadTask(Activity activity){
        this.mActivityReference = new WeakReference<Activity>(activity);
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        // Show progress.
        progressDialog = new ProgressDialog(mActivityReference.get(), R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Uploading Picture ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(String... params) {
        imagePath = params[0];
        storage = FirebaseStorage.getInstance();

        // Scale image as aspect ratio.
        Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
        float[] newLen = new float[2];
        newLen[0] = imageBitmap.getWidth();
        newLen[1] = imageBitmap.getHeight();
        float maxLen = Math.max(newLen[0], newLen[1]);
        while (maxLen > 2000) {
            maxLen *= 0.9;
            newLen[0] *= 0.9;
            newLen[1] *= 0.9;
        }
        Bitmap scaledImageBitmap = Bitmap.createScaledBitmap(imageBitmap, (int)newLen[0], (int)newLen[1], true);

        // Compress image.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaledImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 90;
        while (baos.toByteArray().length / 1024 >= 2000) {
            baos.reset();
            scaledImageBitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        byte[] data = baos.toByteArray();

        // Create a storage reference from our app.
        StorageReference storageRef = storage.getReference();
        StorageReference menuRef = storageRef.child("images/" + MainActivity.userName + "/Menu.jpg");

        UploadTask uploadTask = menuRef.putBytes(data);

        // Register observers to listen for when the Download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("Failure", "Unsuccessful uploads");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                // Once successfully uploaded, execute TextRecTask to recognize texts in that picture.
                progressDialog.dismiss();
                TextRecTask textRecTask = new TextRecTask(mActivityReference.get());
                textRecTask.executeOnExecutor(SERIAL_EXECUTOR);
            }
        });

        return null;
    }
}
