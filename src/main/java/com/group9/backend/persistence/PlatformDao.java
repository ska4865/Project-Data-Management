package com.group9.backend.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.group9.backend.model.Tables.Platform;

@Repository
public class PlatformDao extends AbstractDao {
    
    static Platform grabPlatform(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("platform_id");
        String name = resultSet.getString("name");
        
        return new Platform(id, name);
    }

    public Optional<Platform> save(@NonNull Platform platform) throws DaoException {
        String message = "The Platform to be added should not be null";
        Platform nonNullPlatform = Objects.requireNonNull(platform, message);
        String sql = """
            INSERT INTO
            platform(name)
            VALUES(?)""";
        return insertSingle(PlatformDao::grabPlatform, sql, nonNullPlatform.name());
    }

    
    public Optional<Platform> get(long id) throws DaoException {
        String sql = """
            SELECT platform_id, name
            FROM platform
            WHERE platform_id = ?""";
        return selectSingle(PlatformDao::grabPlatform, sql, id);
    }

    public Collection<Platform> getByName(@NonNull String name) throws DaoException {
        String sql = "SELECT * FROM platform WHERE name LIKE CONCAT('%', ?, '%')";
        return selectMany(PlatformDao::grabPlatform, sql, name);
    }
    
    
    public Collection<Platform> getAll() throws DaoException {
        return selectMany(PlatformDao::grabPlatform, "SELECT * FROM platform");
    }

    
    public boolean update(@NonNull Platform platform) throws DaoException {
        String message = "The platform to be updated should not be null";
        Platform nonNullPlatform = Objects.requireNonNull(platform, message);
        String sql = """
            UPDATE platform
            SET name = ?
            WHERE platform_id = ?""";
        return tryUpdate(sql, nonNullPlatform.name(), nonNullPlatform.platformId());
    }
    
    public boolean delete(@NonNull Platform platform) throws DaoException {
        String message = "The platform to be deleted should not be null";
        Platform nonNullPlatform = Objects.requireNonNull(platform, message);
        String sql = "DELETE FROM platform WHERE platform_id = ?";
        return tryUpdate(sql, nonNullPlatform.platformId());
    }
}
