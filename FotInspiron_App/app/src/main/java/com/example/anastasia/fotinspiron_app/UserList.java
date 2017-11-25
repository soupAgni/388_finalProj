package com.example.anastasia.fotinspiron_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserList extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ArrayList<String> usernames;
    ArrayAdapter arrayAdapter;
    SearchView searchview;

     ListView userList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_user_list);

        searchview = findViewById(R.id.search);
         usernames = new ArrayList<String>();

         arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, usernames);

         userList = findViewById(R.id.lv_userslist);
        //getting the query from the parse users class
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        //You dont want it to get your own username
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseUser user : objects){
                            //Put every user into an array list
                            usernames.add(user.getUsername());
                        }
                        userList.setAdapter(arrayAdapter);
                    }
                }
            }
        });
        userList.setTextFilterEnabled(true);
        setupSearchView();
    }
    private void setupSearchView(){
        searchview.setIconifiedByDefault(false);
        searchview.setOnQueryTextListener(this);
        searchview.setSubmitButtonEnabled(true);
        searchview.setQueryHint("Search Here");
    }
    @Override
    public boolean onQueryTextChange(String newText){
        if(TextUtils.isEmpty(newText)){
            userList.clearTextFilter();
        }
        else{
            userList.setFilterText(newText.toString());
        }
        return true;
    }
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}
