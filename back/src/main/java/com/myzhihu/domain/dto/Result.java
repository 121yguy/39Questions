package com.myzhihu.domain.dto;

public class Result<T> {
    int code;
    T data;
    String msg;

    public Result() {
    }

    public Result(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public Result(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}';
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(1, data);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(0, null, msg);
    }

}

