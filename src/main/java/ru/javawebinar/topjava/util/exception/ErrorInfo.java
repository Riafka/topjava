package ru.javawebinar.topjava.util.exception;

public class ErrorInfo {
    private final String url;
    private final ErrorType type;
    private final String[] details;

    public ErrorInfo(CharSequence url, ErrorType type, String detail) {
        this.url = url.toString();
        this.type = type;
        this.details = new String[1];
        this.details[0] = detail;
    }

    public ErrorInfo(CharSequence url, ErrorType type, String[] details) {
        this.url = url.toString();
        this.type = type;
        this.details = details;
    }
}