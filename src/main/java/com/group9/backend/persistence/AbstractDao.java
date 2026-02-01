package com.group9.backend.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

public abstract class AbstractDao {
    
    protected final Logger LOGGER = Logger.getLogger(getClass().getName());
    
    public static class DaoException extends Exception {
        public DaoException(String message) {
            super(message);
        }
        public DaoException(SQLException cause) {
            super(cause);
        }
    }
    
    public static class MissingConnectionException extends DaoException {
        public MissingConnectionException() {
            super("No connection to database");
        }
    };
    
    public static class SaveConflictException extends DaoException {
        public SaveConflictException() {
            super("Could not insert due to conflict");
        }
    }
    
    public static interface Grabber<T> {
        T grab(ResultSet resultSet) throws SQLException;
    }
    
    @Autowired
    private JSchConnection connection;
    
    private Connection getConnection() throws DaoException {
        return connection.getConnection().orElseThrow(MissingConnectionException::new);
    }
    
    /**
     * Logs an error to the console in a readable way (no stack trace)
     */
    private void log(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                // e.printStackTrace(System.err);
                LOGGER.log(Level.SEVERE, "SQLState: {0}",
                    ((SQLException)e).getSQLState());

                LOGGER.log(Level.SEVERE, "Error Code: {0}",
                    ((SQLException)e).getErrorCode());

                LOGGER.log(Level.SEVERE, "Message: {0}", e.getMessage());

                Throwable t = ex.getCause();
                while(t != null) {
                    LOGGER.log(Level.SEVERE, "Cause: {0}", t);
                    t = t.getCause();
                }
            }
        }
    }
    
    /**
     * Logs the given SQLException then throws a DaoException
     * @param ex the exception to be handled
     * @throws DaoException corresponding to the given exception
     */
    private void handle(SQLException ex) throws DaoException {
        log(ex);
        // loose check -- in reality, this catches *any* integrity constraint violation
        if (ex.getSQLState().startsWith("23")) {
            throw new SaveConflictException();
        } else {
            throw new DaoException(ex);
        }
    }
    
    /**
     * Runs an "INSERT" statement and grabs the generated row from the database.
     * 
     * @param <T> the type of the row that will be inserted
     * @param grabber the method to extract from a ResultSet
     * @param sql the SQL query to execute
     * @param args the arguments to go in place of `?`s
     * @return the inserted row, if any -- only empty if ran without errors but didn't insert anything
     * @throws DaoException if the insertion failed
     */
    protected <T> Optional<T> insertSingle(@NonNull Grabber<T> grabber, @NonNull String sql, Object... args) throws DaoException {
        LOGGER.log(Level.INFO, "Inserting \"{0}\" with args {1}", new Object[]{sql, Arrays.toString(args)});
        
        Optional<T> result = Optional.empty();
        try (PreparedStatement statement = getConnection().
                prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i+1, args[i]);
            }
            
            int numberOfInsertedRows = statement.executeUpdate();
            
            if (numberOfInsertedRows > 0) {
                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        result = Optional.ofNullable(grabber.grab(resultSet));
                        LOGGER.log(Level.INFO, "Created {0} in database", result.get());
                    }
                }
            }
        } catch (SQLException ex) {
            handle(ex);
        }
        return result;
    }
    
    /**
     * Runs a "SELECT" statement for a single element, grabs the returned row
     * 
     * @param <T> the type of the row to select
     * @param grabber the method to extract from a ResultSet
     * @param sql the SQL query to execute
     * @param args the arguments to go in place of `?`s
     * @return the selected row, if any
     * @throws DaoException if the statement failed
     */
    protected <T> Optional<T> selectSingle(@NonNull Grabber<T> grabber, @NonNull String sql, Object... args) throws DaoException {
        LOGGER.log(Level.INFO, "Selecting \"{0}\" with args {1}", new Object[]{sql, Arrays.toString(args)});
        
        Optional<T> result = Optional.empty();
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i+1, args[i]);
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    result = Optional.ofNullable(grabber.grab(resultSet));
                    LOGGER.log(Level.INFO, "Found {0} in database", result.get());
                }
            }
            
        } catch (SQLException ex) {
            handle(ex);
        }
        return result;
    }
    
    /**
     * Runs a "SELECT" statement for many elements, grabs returned rows
     * 
     * @param <T> the type of a single row to select
     * @param grabber the method to extract from a ResultSet
     * @param sql the SQL query to execute
     * @param args the arguments to go in place of `?`s
     * @return the selected rows, if any
     * @throws DaoException if the statement failed
     */
    protected <T> Collection<T> selectMany(@NonNull Grabber<T> grabber, @NonNull String sql, Object... args) throws DaoException {
        LOGGER.log(Level.INFO, "Selecting \"{0}\" with args {1}", new Object[]{sql, Arrays.toString(args)});
        
        Collection<T> result = new ArrayList<T>();
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i+1, args[i]);
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    T row = grabber.grab(resultSet);
                    result.add(row);
                    // can cause too many messages for large queries -- best to leave out:
                    // LOGGER.log(Level.INFO, "Found {0} in database", row);
                }
            }
            
        } catch (SQLException ex) {
            handle(ex);
        }
        return result;
    }
    
    /**
     * Runs an "UPDATE" or "DELETE" statement, returns `true` if any affected
     * 
     * @param sql the SQL query to execute
     * @param args the arguments to go in place of `?`s
     * @return whether the query affected any rows in the database
     * @throws DaoException if there was no database connection
     */
    protected boolean tryUpdate(@NonNull String sql, Object... args) throws DaoException {
        LOGGER.log(Level.INFO, "Executing \"{0}\" with args {1}", new Object[]{sql, Arrays.toString(args)});
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            
            int numberOfAffectedRows = statement.executeUpdate();
            
            LOGGER.log(Level.INFO, "Was the query successful? {0}", numberOfAffectedRows > 0);
            
            return numberOfAffectedRows > 0;
        } catch (SQLException ex) {
            log(ex);
            // TODO: should we still throw in this case?
        }
        return false;
    }
}
