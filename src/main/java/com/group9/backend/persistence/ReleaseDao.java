package com.group9.backend.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.sql.Timestamp;

import org.springframework.stereotype.Repository;

import com.group9.backend.model.Tables.Release;

@Repository
public class ReleaseDao extends AbstractDao {
    
    static Release grabRelease(ResultSet resultSet) throws SQLException {
        long vidId = resultSet.getLong("video_game_id");
        long platId = resultSet.getLong("platform_id");
        double price = resultSet.getDouble("price");
        Timestamp date = resultSet.getTimestamp("release_date");
        return new Release(platId,vidId, price, date);
    }

    public Optional<Release> save(Release release) throws DaoException {
        String message = "The release to be added should not be null";
        Release nonNullRelease = Objects.requireNonNull(release, message);
        String sql = """
            INSERT INTO
            release(platform_id, video_game_id, price, release_date)
            VALUES(?, ?, ?, ?)""";
        return insertSingle(ReleaseDao::grabRelease, sql,
            nonNullRelease.platformId(), nonNullRelease.videoGameId(),
            nonNullRelease.price(), nonNullRelease.releaseDate());
    }

    public Collection<Release> getByGame(long videoGameId) throws DaoException {
        String sql = """
            SELECT * FROM release
            WHERE video_game_id = ?""";
        return selectMany(ReleaseDao::grabRelease, sql, videoGameId);
    }

    public Collection<Release> getByPlatform(long platformId) throws DaoException {
        String sql = """
            SELECT * FROM release
            WHERE platform_id = ?""";
        return selectMany(ReleaseDao::grabRelease, sql, platformId);
    }

    public Optional<Release> get(long videoGameId, long platformId) throws DaoException {
        String sql = """
            SELECT * FROM release
            WHERE platform_id = ?
            AND video_game_id = ?""";
        return selectSingle(ReleaseDao::grabRelease, sql, platformId, videoGameId);
    }

    public Collection<Release> getAll() throws DaoException {
        return selectMany(ReleaseDao::grabRelease, "SELECT * FROM release");
    }

    public boolean update(Release release) throws DaoException {
        String message = "The release to be updated should not be null";
        Release nonNullRelease = Objects.requireNonNull(release, message);
        String sql = """
            UPDATE release
            SET price = ?, release_date = ?
            WHERE video_game_id = ?
            AND platform_id = ?""";
        return tryUpdate(sql,
            nonNullRelease.price(), nonNullRelease.releaseDate(),
            nonNullRelease.videoGameId(), nonNullRelease.platformId());
    }
    public boolean delete(Release release) throws DaoException {
        String message = "The release to be deleted should not be null";
        Release nonNullRelease = Objects.requireNonNull(release, message);
        String sql = "DELETE FROM release WHERE video_game_id = ? AND platform_id = ?";
        return tryUpdate(sql, nonNullRelease.videoGameId(), nonNullRelease.platformId());
    }
    
}
 