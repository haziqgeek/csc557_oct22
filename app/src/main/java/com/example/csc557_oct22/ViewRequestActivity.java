package com.example.csc557_oct22;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.csc557_oct22.adapter.ViewAdapter;  // do not copy this
import com.example.csc557_oct22.model.Appointment;  // do not copy this
import com.example.csc557_oct22.model.SharedPrefManager;  // do not copy this
import com.example.csc557_oct22.model.User;  // do not copy this
import com.example.csc557_oct22.remote.ApiUtils;  // do not copy this
import com.example.csc557_oct22.remote.AppointmentService;  // do not copy this

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewRequestActivity extends AppCompatActivity {

    AppointmentService appointmentService;
    Context context;
    RecyclerView viewList;
    ViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);
        context = this; // get current activity context

        // get reference to the RecyclerView viewList
        viewList = findViewById(R.id.viewList);

        //register for context menu
        registerForContextMenu(viewList);

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

                // Get list of appointment object from response
                List<Appointment> appointments = response.body();

                // initialize adapter
                adapter = new ViewAdapter(context, appointments);

                // set adapter to the RecyclerView
                viewList.setAdapter(adapter);

                // set layout to recycler view
                viewList.setLayoutManager(new LinearLayoutManager(context));

                // add separator between item in the list
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(viewList.getContext(),
                        DividerItemDecoration.VERTICAL);
                viewList.addItemDecoration(dividerItemDecoration);
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                Toast.makeText(context, "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.getMessage());
            }
        });
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
        }
        return super.onContextItemSelected(item);
    }

    private void doViewDetails(Appointment selectedAppointment) {
        Log.d("MyApp:", "viewing details "+selectedAppointment.toString());
        Intent intent = new Intent(context, AppointmentDetailActivity.class);
        intent.putExtra("appointment_id", selectedAppointment.getId());
        startActivity(intent);
    }
}