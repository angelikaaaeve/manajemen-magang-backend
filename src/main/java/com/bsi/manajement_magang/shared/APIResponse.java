package com.bsi.manajement_magang.shared;

public class APIResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Object errors;

    public APIResponse() {
    }

    public APIResponse(boolean success, String message, T data, Object errors) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getErrors() {
        return errors;
    }

    public void setErrors(Object errors) {
        this.errors = errors;
    }

    public static <T> APIResponse<T> success(T data, String message) {
        return new APIResponse<>(true, message, data, null);
    }

    public static <T> APIResponse<T> success(T data) {
        return new APIResponse<>(true, "Success", data, null);
    }

    public static <T> APIResponse<T> error(String message, Object errors) {
        return new APIResponse<>(false, message, null, errors);
    }
}
