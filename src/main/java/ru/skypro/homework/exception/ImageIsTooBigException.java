package ru.skypro.homework.exception;

public class ImageIsTooBigException extends RuntimeException {
    public ImageIsTooBigException(String message) {
        super(message);
    }
}
