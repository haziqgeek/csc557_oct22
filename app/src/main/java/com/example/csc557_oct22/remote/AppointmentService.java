package com.example.csc557_oct22.remote;

import com.example.csc557_oct22.model.Appointment;
import com.example.csc557_oct22.model.DeleteResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AppointmentService {
    @GET("api/appointment/?order=date&orderType=asc")
    Call<List<Appointment>> getAllAppointment(@Header("api-key") String api_key);

    @GET("api/appointment/{id}")
    Call<Appointment> getAppointment(@Header("api-key") String api_key, @Path("id") int id);

    @GET("api/appointment/?status=Approved")
    Call<List<Appointment>> getBookedAppointment(@Header("api-key") String api_key, @Query("lecturer_id") int lectId, @Query("date") String date, @Query("time") String time);

    @GET("api/appointment/?status=New")
    Call<List<Appointment>> getNewAppointmentsByLecturer(@Header("api-key") String api_key, @Query("lecturer_id") int lectId);


    /*** Add appointment by sending a single Appointment JSON* @return book object*/
    @POST("api/appointment")
    Call<Appointment> addAppointment(@Header ("api-key") String apiKey, @Body Appointment appointment);

    /**
     * Delete book based on the id
     * @return DeleteResponse object
     */
    @POST("api/appointment/delete/{id}")
    Call<DeleteResponse> deleteAppointment(@Header ("api-key") String apiKey, @Path("id") int id);

    /**
     * Update appointment
     * @return book object
     */
    @POST("api/appointment/update")
    Call<Appointment> updateAppointment(@Header ("api-key") String apiKey, @Body Appointment appointment);
}
