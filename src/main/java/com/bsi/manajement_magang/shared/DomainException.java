package com.bsi.manajement_magang.shared;

public class DomainException extends RuntimeException {

    public enum Kind {
        EMPTY_FIELD,
        TOO_SHORT,
        TOO_LONG,
        INVALID_VALUE,
        INVALID_TRANSITION,
        CANNOT_DELETE_DEFAULT,
        FILE_TOO_LARGE,
        UNSUPPORTED_CONTENT_TYPE,
        NOT_FOUND,
        UNAUTHORIZED,
        CONFLICT,
        VALIDATION_FAILED,
        INTERNAL_ERROR,
        DATABASE_ERROR
    }

    private final Kind kind;

    private DomainException(Kind kind, String message) {
        super(message);
        this.kind = kind;
    }

    public Kind getKind() {
        return kind;
    }

    public static DomainException emptyField(String field) {
        return new DomainException(Kind.EMPTY_FIELD, "Field '" + field + "' cannot be empty");
    }

    public static DomainException tooShort(String field, int min) {
        return new DomainException(Kind.TOO_SHORT,
                "Field '" + field + "' is too short (minimum " + min + " characters)");
    }

    public static DomainException tooLong(String field, int max) {
        return new DomainException(Kind.TOO_LONG,
                "Field '" + field + "' is too long (maximum " + max + " characters)");
    }

    public static DomainException invalidValue(String field, String reason) {
        return new DomainException(Kind.INVALID_VALUE, "Field '" + field + "' is invalid: " + reason);
    }

    public static DomainException invalidTransition(String from, String to) {
        return new DomainException(Kind.INVALID_TRANSITION,
                "Invalid state transition from '" + from + "' to '" + to + "'");
    }

    public static DomainException cannotDeleteDefault(String resource) {
        return new DomainException(Kind.CANNOT_DELETE_DEFAULT,
                "Cannot delete protected default: " + resource);
    }

    public static DomainException fileTooLarge(long sizeBytes, long maxBytes) {
        return new DomainException(Kind.FILE_TOO_LARGE,
                "File size " + sizeBytes + " bytes exceeds maximum of " + maxBytes + " bytes");
    }

    public static DomainException unsupportedContentType(String contentType) {
        return new DomainException(Kind.UNSUPPORTED_CONTENT_TYPE,
                "Unsupported content type: " + contentType);
    }

    public static DomainException notFound(String message) {
        return new DomainException(Kind.NOT_FOUND, message);
    }

    public static DomainException unauthorized(String message) {
        return new DomainException(Kind.UNAUTHORIZED, message);
    }

    public static DomainException conflict(String message) {
        return new DomainException(Kind.CONFLICT, message);
    }

    public static DomainException validationFailed(String message) {
        return new DomainException(Kind.VALIDATION_FAILED, message);
    }

    public static DomainException internalError(String message) {
        return new DomainException(Kind.INTERNAL_ERROR, message);
    }

    public static DomainException databaseError(String message) {
        return new DomainException(Kind.DATABASE_ERROR, message);
    }
}
