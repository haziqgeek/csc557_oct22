package com.example.csc557_oct22;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.csc557_oct22.adapter.LecturerSpinnerAdapter;
import com.example.csc557_oct22.adapter.TimeSpinnerAdapter;
import com.example.csc557_oct22.model.Appointment;
import com.example.csc557_oct22.model.SharedPrefManager;
import com.example.csc557_oct22.model.User;
import com.example.csc557_oct22.remote.ApiUtils;
import com.example.csc557_oct22.remote.AppointmentService;
import com.example.csc557_oct22.remote.UserService;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestAppointmentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText txtReason;
    private Spinner spLecturer;
    private static TextView tvDate; // static because need to be accessed by DatePickerFragment
    //private static TextView tvTime; // static because need to be accessed by TimePickerFragment
    private Spinner spTime;

    private String consTimeStr;
    private static String consDateStr;
    private Button btnAddAppt;

    private Context context;


    /**
     * Date picker fragment class
     * Reference: https://developer.android.com/guide/topics/ui/controls/pickers
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, day);
            dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            return dpd;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            view.setMinDate(System.currentTimeMillis() - 1000);
            // create a date object from selected year, month and day
            consDateStr = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day);

            // display in the label beside the button with specific date format
            tvDate.setText(consDateStr);
        }
    }

    /**
     * Time picker fragment class
     * Reference: https://developer.android.com/guide/topics/ui/controls/pickers
     */
    /*
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user

            // create a time object from selected hour and minute
            consTime = LocalTime.of(hourOfDay, minute);

            // display in the label beside the button with specific time format
            tvTime.setText(consTime.toString());
        }
    }

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_appointment);

        btnAddAppt = (Button) findViewById(R.id.btnAddAppt);

        spTime = (Spinner) findViewById(R.id.spTime);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.timeslot, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spTime.setAdapter(adapter);

        // store context
        context = this;

        // get view objects references
        txtReason = findViewById(R.id.txtReason);
        spLecturer = findViewById(R.id.spLecturer);
        tvDate = findViewById(R.id.tvDate);
        //tvTime = findViewById(R.id.tvTime);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        // set default consDate value, get current date
        consDateStr = dateFormat.format(date);
        // display in the label beside the button with specific date format
        tvDate.setText(consDateStr);

        // display in the label beside the button with specific time format
        // tvTime.setText(consTime.toString());

        // retrieved list of user and set to spinner
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        // send request to add new book to the REST API
        UserService userService = ApiUtils.getUserService();
        Call<List<User>> call = userService.getAllLecturers(user.getToken());
        // execute
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // invalid session?
                if (response.code() == 401)
                    displayAlert("Invalid session. Please re-login");

                // consultation added successfully?
                List<User> lecturers = response.body();
                if (lecturers != null) {
                    // display message
                    Toast.makeText(getApplicationContext(),
                            "retrieved " + lecturers.size() + " lecturers.",
                            Toast.LENGTH_LONG).show();

                    // set to spinner
                    LecturerSpinnerAdapter lsa = new LecturerSpinnerAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, lecturers);
                    spLecturer.setAdapter(lsa);
                } else {
                    displayAlert("Retrieve users failed.");
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
            }
        });



    }
    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    /**
     * Called when pick date button is clicked. Display a date picker dialog
     * @param v
     */
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Called when pick time button is clicked. Display a time picker dialog
     * @param v
     */
    /*
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
     */

    /**
     * Called when Request New Consultation button is clicked
     * @param v
     */
    public void addNewAppointment(View v) {
        btnAddAppt.setEnabled(false);
        // get values in form
        String reason = txtReason.getText().toString();
        User lecturer = (User) spLecturer.getSelectedItem();
        User student = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        consTimeStr = (String) spTime.getSelectedItem();

        // create an Appointment object
        // set id to 0, it will be automatically generated by the db later
        Appointment a = new Appointment(0, student.getId(), lecturer.getId(), reason, "New", consDateStr, consTimeStr);

        // send request to add new book to the REST API
        AppointmentService appointmentService = ApiUtils.getAppointmentService();
        Call<List<Appointment>> callBooked = appointmentService.getBookedAppointment(student.getToken(), a.getLecturer_id(), a.getDate(), a.getTime());
        callBooked.enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> callB, Response<List<Appointment>> response) {
                // for debug purpose
                Log.d("MyAppisbooked:", "Response: " + response.raw().toString());
                btnAddAppt.setEnabled(true);
                if(response.code() == 204)
                {
                    Call<Appointment> call = appointmentService.addAppointment(student.getToken(), a);
                    // execute
                    call.enqueue(new Callback<Appointment>() {
                        @Override
                        public void onResponse(Call<Appointment> call, Response<Appointment> response) {

                            // for debug purpose
                            Log.d("MyApp:", "Response: " + response.raw().toString());

                            // invalid session?
                            if (response.code() == 401)
                                displayAlert("Invalid session. Please re-login");

                            // book added successfully?
                            Appointment addedAppointment = response.body();
                            if (addedAppointment != null) {
                                // display message
                                Toast.makeText(context,
                                        "Your consultation request is successfully sent to " + lecturer.getName() + " . ",
                                        Toast.LENGTH_LONG).show();

                                // end this activity and forward user to BookListActivity
                                Intent intent = new Intent(context, ViewRequestActivityStudent.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                displayAlert("Request New Consultation failed.\n\n" + a.toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<Appointment> call, Throwable t) {
                            displayAlert("Error [" + t.getMessage() + "]");
                            // for debug purpose
                            Log.d("MyApp:", "Error: " + t.getCause().getMessage());
                            btnAddAppt.setEnabled(true);
                        }
                    });
                }
                else
                    displayAlert("The consultation slot is already booked. Please choose another slot.");
            }

            @Override
            public void onFailure(Call<List<Appointment>> callB, Throwable t) {
                // for debug purpose
                Log.d("MyAppisbooked:", "Error: " + t.getCause().getMessage());
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

    /*
    public boolean isBooked(String apiKey, int lectId, String date, String time) {
        // send request to add new book to the REST API
        final boolean[] status = new boolean[1];
        AppointmentService appointmentService = ApiUtils.getAppointmentService();
        Call<Appointment> call = appointmentService.getBookedAppointment(apiKey, lectId, date, time);
        call.enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                // for debug purpose
                Log.d("MyAppisbooked:", "Response: " + response.raw().toString());

                    status[0] = true;
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                // for debug purpose
                status[0] = true;
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
            }
        });
        return status[0];
    }
     */

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
