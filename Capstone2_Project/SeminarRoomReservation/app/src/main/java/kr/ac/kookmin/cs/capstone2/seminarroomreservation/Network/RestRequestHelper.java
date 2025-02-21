package kr.ac.kookmin.cs.capstone2.seminarroomreservation.Network;

import com.google.gson.JsonObject;

import kr.ac.kookmin.cs.capstone2.seminarroomreservation.Authentication.TransmissionLoginInfo;
import kr.ac.kookmin.cs.capstone2.seminarroomreservation.Join.TransmissionJoinInfo;
import kr.ac.kookmin.cs.capstone2.seminarroomreservation.Manager.TransmissionHistory;
import kr.ac.kookmin.cs.capstone2.seminarroomreservation.Reservation.TransmissionData;
import kr.ac.kookmin.cs.capstone2.seminarroomreservation.Reservation.TransmissionResInfo;
import kr.ac.kookmin.cs.capstone2.seminarroomreservation.Reservation.TransmissionReservation;
import kr.ac.kookmin.cs.capstone2.seminarroomreservation.Reservation.TransmissionUserInfo;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by ehye on 2015-09-14.
 */
public class RestRequestHelper {

    private static RestRequestHelper instance;

    private RestAdapter restAdapter;
    private RestRequest restRequest;


    private static final String url = "http://192.168.1.102:8081/smartdoorlock";

    public static RestRequestHelper newInstance(){
        if(instance == null){
            instance = new RestRequestHelper();
        }
        return instance;
    }

    public RestRequestHelper(){
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(url).build();
        restRequest = restAdapter.create(RestRequest.class);

    }

    public interface RestRequest{
        @POST("/signup")
        void signUp(@Body TransmissionJoinInfo transmissionJoinInfo,
                    Callback<Integer> signUpCallback);

        @POST("/login")
        void login(@Body TransmissionLoginInfo transmissionLoginInfo,
                   Callback<JsonObject> loginCallback
        );


        @POST("/bookingrequest")
        void makeReservation(@Body TransmissionResInfo transmissionResInfo,
                Callback<Integer> makeReservationCallback);


        @POST("/usingstatus")
        void receiveUsingStatus(@Body TransmissionData transmissionData,
                                Callback<JsonObject> usingStatusCallback
        );


        @POST("/roomstatus")
        void roomStatus(@Field("id") int id,
                        @Field("roomName")String roomName,
                        Callback<JsonObject> roomStatusCallback);

        @FormUrlEncoded
        @POST("/doorcontrol")
        void controlDoor(@Field("id") int id,
                         @Field("roomName") int roomId,
                         @Field("command") int status,
                         Callback<JsonObject> doorControllCallback
        );

        @FormUrlEncoded
        @POST("/room")
        void roomList(@Field("id") String id,
                      Callback<String> roomListCallback);


        @POST("/roomhistory")
        void roomHistory(@Body TransmissionHistory info,
                         Callback<JsonObject> roomHistoryCallback);


        @POST("/requestlist")
        void requestList(@Body TransmissionUserInfo info,
                         Callback<JsonObject> requestListCallback);


        @POST("/mybooking")
        void myBooking(@Body TransmissionUserInfo info,
                       Callback<JsonObject> mybookingListCallback);

        @FormUrlEncoded
        @POST("/bookingfilter")
        void bookingFilter(@Field("id") int id,
                           @Field("command") int command,
                           Callback<JsonObject> bookingFilterCallback);

        @POST("/smartkey")
        void getSmartKey(@Body TransmissionUserInfo info,
                         Callback<JsonObject> getSmartKeyCallback);

        @FormUrlEncoded
        @POST("/userlist")
        void dayWatch(@Field("date") String date,
                      Callback<JsonObject> userlistCallback);

        @FormUrlEncoded
        @POST("/bookingspec")
        void getReservationInfo(@Field("reservationId") int reservationId,
                      Callback<JsonObject> resrvationInfoCallback);

        @POST("/cancelmybooking")
        void cancelBooking(@Body TransmissionReservation info,
                           Callback<JsonObject> cancelBookingCallback);

    }

    // for signup
    public void signUp(TransmissionJoinInfo transmissionJoinInfo, Callback<Integer> signUpCallback){
        restRequest.signUp(transmissionJoinInfo, signUpCallback);
    }

    // for login
    public void login(TransmissionLoginInfo transmissionLoginInfo,  Callback<JsonObject> loginCallback){
        restRequest.login(transmissionLoginInfo, loginCallback);
    }

    // for make a reservation
    public void makeReservation(TransmissionResInfo transmissionResInfo, Callback<Integer> makeReservationCallback){
        restRequest.makeReservation(transmissionResInfo, makeReservationCallback);
    }


    // for getting seminar room using status
    public void receiveUsingStatue(TransmissionData transmissionData, Callback<JsonObject> usingStatusCallback){
        restRequest.receiveUsingStatus(transmissionData, usingStatusCallback);
    }


    public void getReservationInfo(int reservationId, Callback<JsonObject> reservationInfoCallback){
        restRequest.getReservationInfo(reservationId, reservationInfoCallback);
    }

    public void roomList(String id,Callback<String> roomListCallback){
        restRequest.roomList(id, roomListCallback);
    }

    public void roomStatus(int id, String roomName, Callback<JsonObject> roomStatusCallback){
        restRequest.roomStatus(id, roomName, roomStatusCallback);
    }

    public void controlDoor(int id, int doorId, int status, Callback<JsonObject> controlDoorCallback ){
        restRequest.controlDoor(id, doorId, status, controlDoorCallback);
    }

    public void getHistory(TransmissionHistory info, Callback<JsonObject> getHistoryCallback){
        restRequest.roomHistory(info, getHistoryCallback);
    }


    public void requestList(TransmissionUserInfo requestDate,Callback<JsonObject> requestCallback){
        restRequest.requestList(requestDate, requestCallback);
    }

    public void bookingFilter(int id, int command, Callback<JsonObject> bookingFilterCallback){
        restRequest.bookingFilter(id, command, bookingFilterCallback);
    }

    public void myBooking(TransmissionUserInfo info, Callback<JsonObject> mybookingListCallback){
        restRequest.myBooking(info, mybookingListCallback);
    }

    public void getSmartKey(TransmissionUserInfo info, Callback<JsonObject> getSmartKeyCallback){
        restRequest.getSmartKey(info, getSmartKeyCallback);
    }

    public void getUserList(String date, Callback<JsonObject> userlistCallback){
        restRequest.dayWatch(date, userlistCallback);
    }
    public void cancelBooking(TransmissionReservation info, Callback<JsonObject> cancelBookingCallback) {
        restRequest.cancelBooking(info, cancelBookingCallback);
    }


    /////////////////////////////



}
