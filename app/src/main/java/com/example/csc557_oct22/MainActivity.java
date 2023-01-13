package com.example.csc557_oct22;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csc557_oct22.model.SharedPrefManager;
import com.example.csc557_oct22.model.User;

public class MainActivity extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        // get reference to the textview
        TextView txtHello = findViewById(R.id.txtHello);

        // get user infor from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // set the textview to display username
        txtHello.setText("Hello " + user.getUsername() + " !");

        // assign action to Book List button
        /*
        Button btnBookList = findViewById(R.id.btnBookList);
        btnBookList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // forward user to BookListActivity
                Intent intent = new Intent(context, BookListActivity.class);
                startActivity(intent);
            }
        });
         */
    }

    public void doLogout(View view) {
        // clear the shared preferences
        SharedPrefManager.getInstance(getApplicationContext()).logout();

        // display message
        Toast.makeText(getApplicationContext(),
                "You have successfully logged out.",
                Toast.LENGTH_LONG).show();
        // forward to LoginActivity
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}