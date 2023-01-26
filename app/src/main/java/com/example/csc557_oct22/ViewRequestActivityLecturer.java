package com.example.csc557_oct22;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csc557_oct22.adapter.ViewAdapterStudent;
import com.example.csc557_oct22.model.Appointment;
import com.example.csc557_oct22.model.SharedPrefManager;
import com.example.csc557_oct22.model.User;
import com.example.csc557_oct22.remote.ApiUtils;
import com.example.csc557_oct22.remote.AppointmentService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewRequestActivityLecturer extends AppCompatActivity {

    AppointmentService appointmentService;
    Context context;
    RecyclerView viewListLecturer;
    ViewAdapterStudent adapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request_lecturer);
        context = this; // get current activity context

        // get reference to the RecyclerView viewList
        viewListLecturer = findViewById(R.id.viewListLecturer);

        //register for context menu
        registerForContextMenu(viewListLecturer);


        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get appointment service instance
        appointmentService = ApiUtils.getAppointmentService();

        // execute the call. send the user token when sending the query
        appointmentService.getAllAppointment(user.getToken()).enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // token is not valid/expired
                if (response.code() == 401) {
                    displayAlert("Session Invalid");
                }

                // Get list of appointment object from response
                List<Appointment> appointments = response.body();

                // initialize adapter
                adapter = new ViewAdapterStudent(context, appointments);

                // set adapter to the RecyclerView
                viewListLecturer.setAdapter(adapter);

                // set layout to recycler view
                viewListLecturer.setLayoutManager(new LinearLayoutManager(context));

                // add separator between item in the list
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(viewListLecturer.getContext(),
                        DividerItemDecoration.VERTICAL);
                viewListLecturer.addItemDecoration(dividerItemDecoration);
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                Toast.makeText(context, "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.getMessage());
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
        }

        // if menu clicked not in list, call the original superclass method
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appointment_context_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Appointment selectedAppointment = adapter.getSelectedItem();
        Log.d("MyApp", "selected " + selectedAppointment.toString());
        switch (item.getItemId()) {
            case R.id.menu_details://should match the id in the context menu file
                doViewDetails(selectedAppointment);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void doViewDetails(Appointment selectedAppointment) {
        Log.d("MyApp:", "viewing details " + selectedAppointment.toString());
        Intent intent = new Intent(context, AppointmentDetailActivity.class);
        intent.putExtra("appointment_id", selectedAppointment.getId());
        startActivity(intent);
    }

    /**
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
     */
    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


}