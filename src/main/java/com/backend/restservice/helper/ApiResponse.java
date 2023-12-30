package com.backend.restservice.helper;

public class ApiResponse<T> {

    private boolean success;
    private T result;

    ApiResponse() {
    }

    public ApiResponse(T result) {
        this(true, result);
    }

    public ApiResponse(boolean success, T result) {
        this.success = success;
        this.result = result;
    }

    public T getResult() {
        return this.result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}