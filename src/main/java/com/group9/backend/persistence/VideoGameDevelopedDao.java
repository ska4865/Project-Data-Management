package com.group9.backend.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.group9.backend.model.Tables.VideoGameDeveloped;

@Repository
public class VideoGameDevelopedDao extends AbstractDao {
    
    static VideoGameDeveloped grabDev(ResultSet resultSet) throws SQLException {
        long vidId = resultSet.getLong("video_game_id");
        long conId = resultSet.getLong("contributor_id");
        return new VideoGameDeveloped(vidId, conId);
    }

    public Optional<VideoGameDeveloped> save(VideoGameDeveloped gameDev) throws DaoException {
        String message = "The game developed to be added should not be null";
        VideoGameDeveloped nonNullDev = Objects.requireNonNull(gameDev, message);
        String sql = """
            INSERT INTO
            video_game_developed(video_game_id, contributor_id)
            VALUES(?, ?)""";
        return insertSingle(VideoGameDevelopedDao::grabDev, sql,
            nonNullDev.videoGameId(), nonNullDev.contributorId());
    }

    public Collection<VideoGameDeveloped> getByGame(long videoGameId) throws DaoException {
        String sql = """
            SELECT * FROM video_game_developed
            WHERE video_game_id = ?""";
        return selectMany(VideoGameDevelopedDao::grabDev, sql, videoGameId);
    }

    public Collection<VideoGameDeveloped> getByDeveloper(long contributorId) throws DaoException {
        String sql = """
            SELECT * FROM video_game_developed
            WHERE contributor_id = ?""";
        return selectMany(VideoGameDevelopedDao::grabDev, sql, contributorId);
    }

    public Collection<VideoGameDeveloped> getAll() throws DaoException {
        return selectMany(VideoGameDevelopedDao::grabDev, "SELECT * FROM video_game_developed");
    }

    public boolean delete(VideoGameDeveloped gameDev) throws DaoException {
        String message = "The game developed to be deleted should not be null";
        VideoGameDeveloped nonNullDev = Objects.requireNonNull(gameDev, message);
        String sql = "DELETE FROM video_game_developed WHERE video_game_id = ? AND contributor_id = ?";
        return tryUpdate(sql, nonNullDev.videoGameId(), nonNullDev.contributorId());
    }
    
}
 