package com.example.csc557_oct22;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

        // assign action to Request New Consultation button

        Button btnAddAppt = findViewById(R.id.btnAddAppt);
        btnAddAppt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // forward user to RequestAppointmentActivity
                Intent intent = new Intent(context, RequestAppointmentActivity.class);
                startActivity(intent);
            }
        });

        // assign action to Consultation List button

        Button btnApptList = findViewById(R.id.btnApptList);
        btnApptList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // forward user to ViewRequestActivity
                Intent intent = new Intent(context, ViewRequestActivityStudent.class);
                startActivity(intent);
            }
        });
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // get the menu inflater
        MenuInflater inflater = super.getMenuInflater();

        // inflate the menu using our XML menu file id, options_menu
        inflater.inflate(R.menu.app_bar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_logout:
                // clear the shared preferences
                SharedPrefManager.getInstance(getApplicationContext()).logout();

                // display message
                Toast.makeText(getApplicationContext(),
                        "You have successfully logged out.",
                        Toast.LENGTH_LONG).show();
                // forward to LoginActivity
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                return true;
        }

        // if menu clicked not in list, call the original superclass method
        return super.onOptionsItemSelected(item);
    }
}