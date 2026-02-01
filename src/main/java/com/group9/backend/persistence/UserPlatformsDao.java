package com.group9.backend.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.group9.backend.model.Tables.Platform;
import com.group9.backend.model.Tables.UserPlatforms;

@Repository
public class UserPlatformsDao extends AbstractDao {
    
    static UserPlatforms grabUserPlatforms(ResultSet resultSet) throws SQLException {
        long userId = resultSet.getLong("user_id");
        long platId = resultSet.getLong("platform_id");
        return new UserPlatforms(userId, platId);
    }


    public Optional<UserPlatforms> save(UserPlatforms userPlatform) throws DaoException {
        String message = "The user platforms to be added should not be null";
        UserPlatforms nonNullUserPlatforms = Objects.requireNonNull(userPlatform, message);
        String sql = """
            INSERT INTO
            user_platforms(user_id, platform_id)
            VALUES(?, ?)""";
        return insertSingle(UserPlatformsDao::grabUserPlatforms, sql,
            nonNullUserPlatforms.userId(), nonNullUserPlatforms.platformId());
    }

    public Collection<Platform> getByUser(long userId) throws DaoException {
        String sql = """
            SELECT platform.platform_id, name
            FROM user_platforms JOIN platform ON platform.platform_id = user_platforms.platform_id
            WHERE user_id = ?""";
        return selectMany(PlatformDao::grabPlatform, sql, userId);
    }

    public Collection<UserPlatforms> getByPlatform(long platformId) throws DaoException {
        String sql = """
            SELECT * FROM user_platforms
            WHERE platform_id = ?""";
        return selectMany(UserPlatformsDao::grabUserPlatforms, sql, platformId); 
    }

    public Collection<UserPlatforms> getAll() throws DaoException {
        return selectMany(UserPlatformsDao::grabUserPlatforms, "SELECT * FROM user_platforms");
    }

    public boolean delete(UserPlatforms userPlatform) throws DaoException {
        String message = "The user platforms to be deleted should not be null";
        UserPlatforms nonNullUserPlatforms = Objects.requireNonNull(userPlatform, message);
        String sql = "DELETE FROM user_platforms WHERE user_id = ? AND platform_id = ?";
        return tryUpdate(sql, nonNullUserPlatforms.userId(), nonNullUserPlatforms.platformId());
    }
    
}
 