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

public interface AppointmentService {
    @GET("api/appointment")
    Call<List<Appointment>> getAllAppointment(@Header("api-key") String api_key);

    @GET("api/appointment/{id}")
    Call<Appointment> getAppointment(@Header("api-key") String api_key, @Path("id") int id);

    /*** Add book by sending a single Book JSON* @return book object*/
    @POST("api/appointment")
    Call<Appointment> addAppointment(@Header ("api-key") String apiKey, @Body Appointment appointment);

    /**
     * Delete book based on the id
     * @return DeleteResponse object
     */
    @POST("api/book/delete/{id}")
    Call<DeleteResponse> deleteAppointment(@Header ("api-key") String apiKey, @Path("id") int id);
}
