package com.group9.backend.persistence;

import java.sql.*;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import jakarta.annotation.PreDestroy;

@Component
public class JSchConnection {

    private static final Logger LOGGER =
        Logger.getLogger(JSchConnection.class.getName());
    
    private int lport = 5432;
    private String rhost = "starbug.cs.rit.edu";
    private int rport = 5432;
    private String databaseName = "p320_09"; //change to your database name
    
    @Value("${DB_user}")
    private String user;
    @Value("${DB_pass}")
    private String password;
    
    private Optional<Session> session = Optional.empty();
    private Optional<Connection> connection = Optional.empty();

    public Optional<Connection> getConnection() {
        if (connection.isEmpty()) {
            String driverName = "org.postgresql.Driver";
            Session session = null;
            try {
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                JSch jsch = new JSch();
                session = jsch.getSession(user, rhost, 22);
                session.setPassword(password);
                session.setConfig(config);
                session.setConfig("PreferredAuthentications","publickey,keyboard-interactive,password");
                session.connect();
                
                this.session = Optional.of(session);
                
                LOGGER.info("Connected");
                int assigned_port = session.setPortForwardingL(lport, "127.0.0.1", rport);
                LOGGER.info("Port Forwarded");

                // Assigned port could be different from 5432 but rarely happens
                String url = "jdbc:postgresql://127.0.0.1:"+ assigned_port + "/" + databaseName;

                LOGGER.log(Level.INFO, "database Url: {0}", url);
                Properties props = new Properties();
                props.put("user", user);
                props.put("password", password);

                Class.forName(driverName);
                connection = Optional.of(DriverManager.getConnection(url, props));
                LOGGER.info("Database connection established");

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, null, e);
            }
        }

        return connection;
    }
    
    @PreDestroy
    public void close() {
        LOGGER.info("Closing connection");
        try {
            if (connection.isPresent() && !connection.get().isClosed()) {
                connection.get().close();
            }
        } catch (SQLException e) { }
        if (session.isPresent() && session.get().isConnected()) {
            session.get().disconnect();
        }
        
        connection = Optional.empty();
        session = Optional.empty();
    }
}