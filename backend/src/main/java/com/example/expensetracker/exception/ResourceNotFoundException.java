package com.example.expensetracker.exception;

/*
 * Thrown when something the client asked for does not exist (an expense id, a category id).
 * It extends RuntimeException (an "unchecked" exception), so methods don't have to
 * declare "throws ..." everywhere. Spring's @Transactional also rolls back on these.
 * In part 2 we'll translate it into an HTTP 404 response in ONE central place.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}