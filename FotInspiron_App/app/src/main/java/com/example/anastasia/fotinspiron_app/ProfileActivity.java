package com.example.anastasia.fotinspiron_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
 EditText editText1;
 EditText editText2;
 EditText editText3;
 EditText editText4;
 String username;
 String email = "";
 String preferredname = "";
 String phone = "";
 Button button;

 LinearLayout ll_forGrid;
 GridView gridView;
 ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        editText1 = (EditText)findViewById(R.id.textView3);
        editText2 = (EditText) findViewById(R.id.textView5);
        editText3 = (EditText)findViewById(R.id.textView6);
        editText4 = (EditText)findViewById(R.id.textView8);

        button = (Button)findViewById(R.id.button2) ;
        //ll_forGrid = findViewById(R.id.ll_grid);
        gridView = findViewById(R.id.grid);
        String name = ParseUser.getCurrentUser().getUsername();
        setTitle(name + "'s Profile");


        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                username=   object.getString("username");
                System.out.println("username: "+username);
                if (object.containsKey("email")){
                    email = object.getString("email");

                }
                if (object.containsKey("preferredname")){
                    preferredname = object.getString("preferredname");
                }
                if (object.containsKey("phone")){
                    phone = object.getString("phone");

                }

                editText1.setText(username);
                editText2.setText(email);
                editText3.setText(preferredname);
                editText4.setText(phone);


            }
        });

        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String email = editText2.getText().toString().trim();
                if (email.matches(emailPattern) && editable.length()> 0 ){
                    Toast.makeText(getApplicationContext(),"valid email address",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Invalid email address",Toast.LENGTH_SHORT).show();
                }

            }
        });
        editText4.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.getCurrentUser().put("username",editText1.getText().toString().replace("\n", "").replace("\r", ""));
                ParseUser.getCurrentUser().put("email",editText2.getText().toString().replace("\n", "").replace("\r", ""));
                ParseUser.getCurrentUser().put("preferredname",editText3.getText().toString().replace("\n", "").replace("\r", ""));
                ParseUser.getCurrentUser().put("phone",editText4.getText().toString().replace("\n", "").replace("\r", ""));

                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("Parse", "Save Succeeded");
                            Intent refresh = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(refresh);//Start the same Activity
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),"Unable to update information",Toast.LENGTH_LONG).show();
                            Log.i("Parse", "Save Failed");
                        }
                    }
                });


            }
        });

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Images");
        query.whereEqualTo("username", name);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {

                        for (ParseObject object : objects) {

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

                                            gridView.setAdapter(new ProfileActivity.ImageAdapterGridView(getApplicationContext()));


                                            //Log.i("NumImagesFound", "numImages: " + count);

                                        }

                                    }

                                });
                            }
                        }
                    }
                }
            }
        });
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



