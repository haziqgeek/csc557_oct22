package com.example.csc557_oct22;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csc557_oct22.model.Appointment;
import com.example.csc557_oct22.model.SharedPrefManager;
import com.example.csc557_oct22.model.User;
import com.example.csc557_oct22.remote.ApiUtils;
import com.example.csc557_oct22.remote.AppointmentService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentDetailActivity extends AppCompatActivity {

    AppointmentService appointmentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        // get appointment id sent by ViewRequestActivity, -1 if not found
        Intent intent = getIntent();
        int id = intent.getIntExtra("appointment_id", -1);

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get appointment service instance
        appointmentService = ApiUtils.getAppointmentService();

        // execute the API query. send the token and appointment id
        appointmentService.getAppointment(user.getToken(), id).enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // get appointment object from response
                Appointment appointment = response.body();

                // get references to the view elements
                TextView tvReason = findViewById(R.id.tvReason);
                TextView tvName = findViewById(R.id.tvName);
                TextView tvStatus = findViewById(R.id.tvStatus);
                TextView tvDate = findViewById(R.id.tvDate);
                TextView tvTime = findViewById(R.id.tvTime);

                // set values
                tvReason.setText(appointment.getReason());
                tvName.setText(appointment.getLecturer().getName());
                tvStatus.setText(appointment.getStatus());
                tvDate.setText(appointment.getDate());
                tvTime.setText(appointment.getTime());
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                Toast.makeText(null, "Error connecting", Toast.LENGTH_LONG).show();
            }
        });
        // enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // terminate this Activity and go back to caller
                return true;
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // get the menu inflater
        MenuInflater inflater = super.getMenuInflater();

        // inflate the menu using our XML menu file id, options_menu
        inflater.inflate(R.menu.app_bar_menu, menu);

        return true;
    }

}