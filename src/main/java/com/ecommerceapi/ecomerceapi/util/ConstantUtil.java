package com.ecommerceapi.ecomerceapi.util;

public class ConstantUtil {

    public static Integer STATUS_SUCCESS = 200;
    public static Integer STATUS_ACTIVE = 1;
    public static Integer STATUS_INACTIVE = 1;
    public static Integer STATUS_ERROR = 500;
    public static Integer STATUS_INCOMPLETE_DATA = 400;
    public static Integer STATUS_INVALID_FORMAT = 400;
    public static Integer STATUS_ACCESS_DENIED = 401;
    public static Integer STATUS_EXISTING_DATA = 409;
    public static Integer STATUS_DATA_NOT_FOUND = 404;
    public static Integer STATUS_ERROR_SYSTEM = 500;

    public static String MESSAGE_OTP_EXPIRED = "Invalid OTP / Expired";
    public static String MESSAGE_DATA_NOT_FOUND = "Data not found";
    public static String OUT_OF_STOCK = "Out of stock";
    public static String MESSAGE_SUCCESS = "successfully loaded";
    public static String MESSAGE_EXISTING_DATA = "Data already exist";
    public static String FRONTENDLINK = "https://merchant.redigan.co.id/incoming-order";
    public static String TRACKINGLINK = "https://customer.redigan.co.id/track-order?orderID=";
    public static String NO_REPLY_EMAIL = "cs@redigan.co.id";
    public static String NO_REPLY_EMAIL_ADMIN = "admin@redigan.co.id";

    public static String PT_TRANSFER = "SIAP PESAN SEKARANG PT";
    public static String NOREK = "5245300821";
    public static String BANK = "BCA";

    public static final String API_SECRET_KEY = "secretkey";
    public static final Integer TOKEN_VALIDTY = 1 * 60 * 60 * 100000;

}
