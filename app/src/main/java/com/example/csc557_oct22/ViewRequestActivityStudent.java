package com.example.csc557_oct22;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.csc557_oct22.adapter.ViewAdapterStudent;  // do not copy this
import com.example.csc557_oct22.model.Appointment;  // do not copy this
import com.example.csc557_oct22.model.DeleteResponse;
import com.example.csc557_oct22.model.SharedPrefManager;  // do not copy this
import com.example.csc557_oct22.model.User;  // do not copy this
import com.example.csc557_oct22.remote.ApiUtils;  // do not copy this
import com.example.csc557_oct22.remote.AppointmentService;  // do not copy this

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewRequestActivityStudent extends AppCompatActivity {

    AppointmentService appointmentService;
    Context context;
    RecyclerView viewListStudent;
    ViewAdapterStudent adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request_student);
        context = this; // get current activity context

        // get reference to the RecyclerView viewList
        viewListStudent = findViewById(R.id.viewListStudent);

        //register for context menu
        registerForContextMenu(viewListStudent);


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
                viewListStudent.setAdapter(adapter);

                // set layout to recycler view
                viewListStudent.setLayoutManager(new LinearLayoutManager(context));

                // add separator between item in the list
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(viewListStudent.getContext(),
                        DividerItemDecoration.VERTICAL);
                viewListStudent.addItemDecoration(dividerItemDecoration);
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
        Log.d("MyApp", "selected "+selectedAppointment.toString());
        switch (item.getItemId()) {
            case R.id.menu_details://should match the id in the context menu file
                doViewDetails(selectedAppointment);
                break;
            case R.id.menu_delete://should match the id in the context menu file
                doDeleteAppointment(selectedAppointment);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void doViewDetails(Appointment selectedAppointment) {
        Log.d("MyApp:", "viewing details "+selectedAppointment.toString());
        Intent intent = new Intent(context, AppointmentDetailActivity.class);
        intent.putExtra("appointment_id", selectedAppointment.getId());
        startActivity(intent);
    }
    /**
     * Delete appointment record. Called by contextual menu "Delete"
     * @param selectedAppointment - appointment selected by user
     */
    private void doDeleteAppointment(Appointment selectedAppointment) {
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // prepare REST API call
        AppointmentService appointmentService = ApiUtils.getAppointmentService();
        Call<DeleteResponse> call = appointmentService.deleteAppointment(user.getToken(), selectedAppointment.getId());

        // execute the call
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.code() == 200) {
                    // 200 means OK
                    displayAlert("Appointment successfully deleted");

                } else {
                    displayAlert("Appointment failed to delete");
                    Log.e("MyApp:", response.raw().toString());
                }
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });
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