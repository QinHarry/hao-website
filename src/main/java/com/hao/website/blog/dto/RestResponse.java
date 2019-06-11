package com.hao.website.blog.dto;

import lombok.Data;

@Data
public class RestResponse<T> {

    private T payload;

    private boolean success;

    private String msg;

    private int code = -1;

    private long timestamp;

    public RestResponse() {
        timestamp = System.currentTimeMillis() / 1000;
    }

    public RestResponse(boolean success) {
        this();
        this.success = success;
    }

    public RestResponse(boolean success, T payload) {
        this(success);
        this.payload = payload;
    }

    public RestResponse(boolean success, String msg) {
        this(success);
        this.msg = msg;
    }

    public RestResponse(boolean success, T payload, int code) {
        this(success, payload);
        this.code = code;
    }

    public RestResponse(boolean success, String msg, int code) {
        this(success, msg);
        this.code = code;
    }


    public static RestResponse ok() {
        return new RestResponse(true);
    }

    public static <T> RestResponse ok(T payload) {
        return new RestResponse(true, payload);
    }

    public static <T> RestResponse ok(int code) {
        return new RestResponse(true, null, code);
    }

    public static <T> RestResponse ok(T payload, int code) {
        return new RestResponse(true, payload, code);
    }

    public static RestResponse fail() {
        return new RestResponse(false);
    }

    public static RestResponse fail(String msg) {
        return new RestResponse(false, msg);
    }

    public static RestResponse fail(int code) {
        return new RestResponse(false, null, code);
    }

    public static RestResponse fail(int code, String msg) {
        return new RestResponse(false, msg, code);
    }
}
