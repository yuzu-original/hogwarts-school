package ru.hogwarts.school.exception;

public class ErrorInfo {
    public final String message;

    public ErrorInfo(Exception e) {
        this.message = e.getLocalizedMessage();
    }
}
