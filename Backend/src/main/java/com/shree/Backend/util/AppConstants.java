package com.shree.Backend.util;

public class AppConstants {

    public static final String AUTH_CONTROLLER = "/api/auth";
    public static final String REGISTER = "/register";
    public static final String VERIFY_EMAIL = "verify-email";
    public static final String RESEND_VERIFICATION = "/resend-verification";
    public static final String FILE_UPLOAD = "/upload-image";
    public static final String LOGIN = "/login";
    public static final String PROFILE = "/profile";

    //Resume related endpoints

    public static final String ID = "/{id}";
    public static final String UPLOAD_IMAGES = "/{id}/upload-images";


    //Payment related endpoints

    public static final String CREATE_ORDER = "/create-order";
    public static final String VERIFY = "/verify";
    public static final String HISTORY = "/history";
    public static final String GET_ORDER= "/order/{orderId}";


}
