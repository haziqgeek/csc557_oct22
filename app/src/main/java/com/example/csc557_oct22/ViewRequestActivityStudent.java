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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.csc557_oct22.adapter.ViewAdapterStudent;  // do not copy this
import com.example.csc557_oct22.model.Appointment;  // do not copy this
import com.example.csc557_oct22.model.DeleteResponse;
import com.example.csc557_oct22.model.SharedPrefManager;  // do not copy this
import com.example.csc557_oct22.model.User;  // do not copy this
import com.example.csc557_oct22.remote.ApiUtils;  // do not copy this
import com.example.csc557_oct22.remote.AppointmentService;  // do not copy this
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewRequestActivityStudent extends AppCompatActivity {

    AppointmentService appointmentService;
    Context context;
    RecyclerView viewListStudent;
    ViewAdapterStudent adapter;
    private Spinner spFilter;
    private List<Appointment> appointments;
    private List<Appointment> allAppointments = new ArrayList<Appointment>();
    private List<Appointment> approvedAppointments = new ArrayList<Appointment>();
    private List<Appointment> declinedAppointments = new ArrayList<Appointment>();
    private List<Appointment> newAppointments = new ArrayList<Appointment>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request_student);
        context = this; // get current activity context

        spFilter = (Spinner) findViewById(R.id.spFilter);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(this, R.array.filterStatus, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spFilter.setAdapter(filterAdapter);

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
                appointments = response.body();

                if (appointments != null) {


                    for (Appointment appointment : appointments) {
                        if (appointment.getStudent().getId() == user.getId()) {
                            allAppointments.add(appointment);
                            if (appointment.getStatus().equals("New"))
                                newAppointments.add(appointment);
                            else if (appointment.getStatus().equals("Approved"))
                                approvedAppointments.add(appointment);
                            else
                                declinedAppointments.add(appointment);
                        }
                    }

                    appointments.clear();
                    appointments.addAll(allAppointments);
                    adapter = new ViewAdapterStudent(context, appointments);
                    // set adapter to the RecyclerView
                    viewListStudent.setAdapter(adapter);

                    // set layout to recycler view
                    viewListStudent.setLayoutManager(new LinearLayoutManager(context));

                    // add separator between item in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(viewListStudent.getContext(),
                            DividerItemDecoration.VERTICAL);
                    viewListStudent.addItemDecoration(dividerItemDecoration);

                    spFilter.setOnItemSelectedListener(
                            new AdapterView.OnItemSelectedListener() {
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    appointments.clear();
                                    switch (position) {
                                        case 0:
                                            appointments.addAll(allAppointments);
                                            break;
                                        case 1:
                                            appointments.addAll(newAppointments);
                                            break;
                                        case 2:
                                            appointments.addAll(approvedAppointments);
                                            break;
                                        case 3:
                                            appointments.addAll(declinedAppointments);
                                            break;
                                    }

                                    // notify adapter
                                    adapter.notifyDataSetChanged();
                                }

                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            }
                    );
                }

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
        if (!adapter.getSelectedItem().getStatus().equals("New"))
            menu.findItem(R.id.menu_delete).setVisible(false);
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
                appointments.remove(adapter.getCurrentPos());
                adapter.notifyItemRemoved(adapter.getCurrentPos());
                newAppointments.remove(selectedAppointment);
                allAppointments.remove(selectedAppointment);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // get the menu inflater
        MenuInflater inflater = super.getMenuInflater();

        // inflate the menu using our XML menu file id, options_menu
        inflater.inflate(R.menu.app_bar_menu, menu);

        return true;
    }


}