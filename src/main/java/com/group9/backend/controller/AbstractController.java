package com.group9.backend.controller;

import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

import com.group9.backend.persistence.AbstractDao.DaoException;
import com.group9.backend.persistence.AbstractDao.MissingConnectionException;
import com.group9.backend.persistence.AbstractDao.SaveConflictException;

public abstract class AbstractController {
    
    protected final Logger LOGGER = Logger.getLogger(getClass().getName());
    
    protected interface DaoMethod<T> {
        T get() throws DaoException;
    }
    
    private String callerName() {
        return StackWalker.
            getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).
            walk(
                stream ->
                    stream.dropWhile(
                        frame ->
                            frame.getDeclaringClass() == AbstractController.class
                ).findFirst().get()).
            getMethodName();
    }
    
    protected void log(Exception e) {
        LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[] {callerName(), e});
    }
    
    protected <T> ResponseEntity<T> handleOptional(
            @NonNull DaoMethod<Optional<T>> method,
            @NonNull HttpStatusCode successCode,
            @NonNull HttpStatusCode failureCode) {
        ResponseEntity<T> result;
        try {
            result = method.get().map(obj ->
                new ResponseEntity<>(obj, successCode)
            ).orElse(
                new ResponseEntity<>(failureCode)
            );
        } catch (SaveConflictException e) {
            log(e);
            result = new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (MissingConnectionException e) {
            log(e);
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (DaoException e) {
            log(e);
            // assume the user's fault
            result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log(e);
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
    
    protected <T> ResponseEntity<T> handleGet(@NonNull DaoMethod<Optional<T>> method) {
        return handleOptional(method, HttpStatus.OK, HttpStatus.NOT_FOUND);
    }
    
    protected <T> ResponseEntity<T> handleCreate(@NonNull DaoMethod<Optional<T>> method) {
        return handleOptional(method, HttpStatus.CREATED, HttpStatus.BAD_REQUEST);
    }
    
    protected ResponseEntity<Void> handleBoolean(
            @NonNull DaoMethod<Boolean> method,
            @NonNull HttpStatusCode successCode,
            @NonNull HttpStatusCode failureCode) {
        ResponseEntity<Void> result;
        try {
            if (method.get()) {
                result = new ResponseEntity<>(successCode);
            } else {
                result = new ResponseEntity<>(failureCode);
            }
        } catch (MissingConnectionException e) {
            log(e);
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (DaoException e) {
            log(e);
            // assume the user's fault
            result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log(e);
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
    
    protected ResponseEntity<Void> handleUpdate(@NonNull DaoMethod<Boolean> method) {
        return handleBoolean(method, HttpStatus.OK, HttpStatus.NOT_FOUND);
    }
    
    protected <T> ResponseEntity<Collection<T>> handleCollection(
            @NonNull DaoMethod<Collection<T>> method) {
        ResponseEntity<Collection<T>> result;
        try {
            result = new ResponseEntity<>(method.get(), HttpStatus.OK);
        } catch (MissingConnectionException e) {
            log(e);
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (DaoException e) {
            log(e);
            // assume the user's fault
            result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log(e);
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
    
}
