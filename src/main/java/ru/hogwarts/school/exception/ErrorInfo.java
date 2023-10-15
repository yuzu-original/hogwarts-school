package ru.hogwarts.school.exception;

import java.util.Objects;

public class ErrorInfo {
    private String message;

    public ErrorInfo(Exception e) {
        this.message = e.getLocalizedMessage();
    }

    public ErrorInfo() {
        this.message = "Unknown error";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorInfo errorInfo = (ErrorInfo) o;
        return Objects.equals(message, errorInfo.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return "ErrorInfo{" +
                "message='" + message + '\'' +
                '}';
    }
}
