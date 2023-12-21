package ru.skypro.homework.exception;

public class CommentDoesntExistException extends RuntimeException {
    public CommentDoesntExistException(String message) {
        super(message);
    }
}
