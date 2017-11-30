package com.example.anastasia.fotinspiron_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class updateDescription extends AppCompatActivity {

    EditText descripton;
    Button done; String id;
    private static final String CHANNEL_ID = "media_playback_channel";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_description);

        //to get image change intent of the app so that it can be accessed through another activity
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);

        descripton = findViewById(R.id.et_description);
        done = findViewById(R.id.btn_done);

        id = getIntent().getStringExtra("id");

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent i = new Intent(updateDescription.this, UserList.class);
                startActivity(i);*/
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Images");
                //query.whereEqualTo("objectID", id);
                query.getInBackground(id, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if(e == null){
                            object.put("description", descripton.getText().toString());
                            object.saveInBackground();
                            Toast.makeText(getApplicationContext(), "Description updated!", Toast.LENGTH_SHORT).show();
                            Log.i("DescriptionUpdate", "Success");
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Description update failed!", Toast.LENGTH_SHORT).show();
                            Log.i("DescriptionUpdate", "Failed");
                        }
                    }
                });

                /*ParseObject object = new ParseObject("Images");
                object.put("description", descripton.getText().toString());

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Toast.makeText(getApplicationContext(), "Description updated!", Toast.LENGTH_SHORT).show();
                            Log.i("DescriptionUpdate", "Success");
                            Intent i = new Intent(updateDescription.this, UserList.class);
                            startActivity(i);

                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Description update failed!", Toast.LENGTH_SHORT).show();
                            Log.i("DescriptionUpdate", "Failed");
                        }
                    }
                });*/
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                ImageView imageView = findViewById(R.id.iv_uploadedImage);
                imageView.setImageBitmap(bitmapImage);

                /*Log.i("AppInfo", "ImageRecieved");

                //in order to pass it into parse
                //(like the stream we get in the web when we download it)
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                //do not compress for now
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //store the image as a byte array
                byte[] byteArray = stream.toByteArray();
                System.out.println(byteArray.length);
                //convert to parse file before passing into parse
                ParseFile file = new ParseFile("image.png", byteArray);
                final ParseObject object = new ParseObject("Images");
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            System.out.println("Done");
                        } else {
                            e.printStackTrace();
                        }
                    }
                }, new ProgressCallback() {
                    @Override
                    public void done(Integer percentDone) {
                        System.out.println(percentDone);
                    }
                });


                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.put("images", file);

                ParseACL parseACL = new ParseACL();
                parseACL.setPublicReadAccess(true);

                object.setACL(parseACL);
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){

                            //Intent i = new Intent(getApplicationContext(), updateDescription.class);
                            String text = "Your image has been posted!";
                            id = object.getObjectId();
                            //i.putExtra("id", id);
                            //startActivity(i);
                            NotifyUser(text);
                            //picUpdateStatus = true;
                        }
                        else{
                            String text = "Could not save";
                            NotifyUser(text);
                            //picUpdateStatus = false;
                            //e.printStackTrace();
                        }
                    }
                });*/


            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplication().getBaseContext(), "Error Before save in Bg", Toast.LENGTH_LONG).show();

            }

        }
}
    private void NotifyUser(String text){
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID);
        mBuilder.setContentTitle("Photo upload status");
        mBuilder.setContentText(text);
        mBuilder.setSmallIcon(R.drawable.ic_file_upload_black_24dp);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        Notification notification = mBuilder.build();
        NotificationManager notificationManager  = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);


    }
    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationManager
                mNotificationManager =
                (NotificationManager) getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
        String id = CHANNEL_ID;
        CharSequence name = "Media playback";
        String description = "Media playback controls";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setShowBadge(false);
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        mNotificationManager.createNotificationChannel(mChannel);
    }

}
