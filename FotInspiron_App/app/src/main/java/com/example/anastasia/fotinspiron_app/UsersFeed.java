package com.example.anastasia.fotinspiron_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UsersFeed extends AppCompatActivity {

   String username = "";
    int count = 0;
    ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
    GridView gv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_feed);

        gv = findViewById(R.id.gv);

        Intent i = getIntent();
        username = i.getStringExtra("username");

        setTitle(username + "'s Feed");

        //get info for users feed from the parse database
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Images");
        query.whereEqualTo("username", username);
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){

                        for(ParseObject object : objects) {

                            //pointer to the file
                            ParseFile file = (ParseFile) object.get("images");
                            if (file != null) {
                                file.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null) {
                                            //convert the byte array to an image
                                            Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                                            bitmaps.add(image);

                                            gv.setAdapter(new ImageAdapterGridView(getApplicationContext()));

                                            count++;

                                            Log.i("NumImagesFound", "numImages: " +count);

                                        }

                                    }

                                });
                            }
                        }
                    }
                }
            }
        });

        //if we want to display something when the user clicks on an image
       /* gv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id)
            {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Images");
                query.whereEqualTo("username", username);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if(e == null){
                            Log.i("findInBg", "Retrieved" + objects.size() + " results");

                            for(ParseObject object : objects){
                                Log.i("FindInBg", String.valueOf(object.get("score")));
                            }
                        }
                    }
                });
            }
        });*/

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_view, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.profile){
            Intent main = new Intent(getApplicationContext(),UserProfile.class);
            main.putExtra("username", username);
            startActivity(main);
        }
        return super.onOptionsItemSelected(item);

    }


    public class ImageAdapterGridView extends BaseAdapter {
        private Context mContext;

        public ImageAdapterGridView(Context c) {
            mContext = c;
        }

        public int getCount() {
            return bitmaps.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView mImageView;

            if (convertView == null) {
                mImageView = new ImageView(mContext);
                //mImageView.setImageBitmap(bitmap);
                mImageView.setLayoutParams(new GridView.LayoutParams(300, 300));
                mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mImageView.setPadding(20, 16, 20, 16);
            } else {
                mImageView = (ImageView) convertView;
            }
            //mImageView.setImageResource(imageIDs[position]);
            mImageView.setImageBitmap(bitmaps.get(position));
            return mImageView;
        }
    }



}