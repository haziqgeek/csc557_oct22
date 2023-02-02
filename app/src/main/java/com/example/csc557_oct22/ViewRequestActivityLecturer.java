package com.example.csc557_oct22;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csc557_oct22.adapter.ViewAdapterLecturer;
import com.example.csc557_oct22.model.Appointment;
import com.example.csc557_oct22.model.SharedPrefManager;
import com.example.csc557_oct22.model.User;
import com.example.csc557_oct22.remote.ApiUtils;
import com.example.csc557_oct22.remote.AppointmentService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewRequestActivityLecturer extends AppCompatActivity {

    AppointmentService appointmentService;
    Context context;
    RecyclerView viewListLecturer;
    ViewAdapterLecturer adapter;

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
        appointmentService.getNewAppointmentsByLecturer(user.getToken(), user.getId()).enqueue(new Callback<List<Appointment>>() {
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
                adapter = new ViewAdapterLecturer(context, appointments);

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

        // action handler for Add Book floating button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // forward user to NewBookActivity
                Intent intent = new Intent(context, ViewApprovedActivity.class);
                startActivity(intent);
            }
        });
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

    /**
     * Called when Request New Consultation button is clicked
     * @param v
     */
    public void approveAppointment(View v) {

        // update the book object retrieved in onCreate with the new data. the book object
        // already contains the id
        Appointment appointment = adapter.getSelectedItem();
        appointment.setStatus("Approved");

        Log.d("MyApp:", "Note info: " + appointment.toString());

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // send request to update the book record to the REST API
        AppointmentService appointmentService = ApiUtils.getAppointmentService();
        Call<Appointment> call = appointmentService.updateAppointment(user.getToken(), appointment);

        Context context = this;
        // execute
        call.enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // invalid session?
                if (response.code() == 401)
                    displayAlert("Invalid session. Please re-login");

                // book updated successfully?
                Appointment updatedAppointment = response.body();
                if (updatedAppointment != null) {
                    // display message
                    Toast.makeText(context,
                            updatedAppointment.getStudent().getName() + "'s appointment is approved.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and forward user to BookListActivity
                    Intent intent = new Intent(context, ViewRequestActivityLecturer.class);
                    startActivity(intent);
                    finish();
                } else {
                    displayAlert("Failed to approve appointment.");
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
            }
        });
    }
    public void declineAppointment(View v) {

        // update the book object retrieved in onCreate with the new data. the book object
        // already contains the id
        Appointment appointment = adapter.getSelectedItem();
        appointment.setStatus("Declined");

        Log.d("MyApp:", "Note info: " + appointment.toString());

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // send request to update the book record to the REST API
        AppointmentService appointmentService = ApiUtils.getAppointmentService();
        Call<Appointment> call = appointmentService.updateAppointment(user.getToken(), appointment);

        Context context = this;
        // execute
        call.enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // invalid session?
                if (response.code() == 401)
                    displayAlert("Invalid session. Please re-login");

                // book updated successfully?
                Appointment updatedAppointment = response.body();
                if (updatedAppointment != null) {
                    // display message
                    Toast.makeText(context,
                            updatedAppointment.getStudent().getName() + "'s appointment is approved.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and forward user to BookListActivity
                    Intent intent = new Intent(context, ViewRequestActivityLecturer.class);
                    startActivity(intent);
                    finish();
                } else {
                    displayAlert("Failed to approve appointment.");
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
            }
        });

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