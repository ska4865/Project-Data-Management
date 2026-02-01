package com.group9.backend.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.group9.backend.model.Tables.VideoGamePublished;

@Repository
public class VideoGamePublishedDao extends AbstractDao {
    
    static VideoGamePublished grabPub(ResultSet resultSet) throws SQLException {
        long vidId = resultSet.getLong("video_game_id");
        long conId = resultSet.getLong("contributor_id");
        return new VideoGamePublished(vidId, conId);
    }

    public Optional<VideoGamePublished> save(VideoGamePublished gamePub) throws DaoException {
        String message = "The game published to be added should not be null";
        VideoGamePublished nonNullPub = Objects.requireNonNull(gamePub, message);
        String sql = """
            INSERT INTO
            video_game_published(video_game_id, contributor_id)
            VALUES(?, ?)""";
        return insertSingle(VideoGamePublishedDao::grabPub, sql,
            nonNullPub.videoGameId(), nonNullPub.contributorId());
    }

    public Collection<VideoGamePublished> getByGame(long videoGameId) throws DaoException {
        String sql = """
            SELECT * FROM video_game_published
            WHERE video_game_id = ?""";
        return selectMany(VideoGamePublishedDao::grabPub, sql, videoGameId);
    }

    public Collection<VideoGamePublished> getByPublisher(long contributorId) throws DaoException {
        String sql = """
            SELECT * FROM video_game_published
            WHERE contributor_id = ?""";
        return selectMany(VideoGamePublishedDao::grabPub, sql, contributorId);
    }

    public Collection<VideoGamePublished> getAll() throws DaoException {
        return selectMany(VideoGamePublishedDao::grabPub, "SELECT * FROM video_game_published");
    }

    public boolean delete(VideoGamePublished gamePub) throws DaoException {
        String message = "The game published to be deleted should not be null";
        VideoGamePublished nonNullPub = Objects.requireNonNull(gamePub, message);
        String sql = "DELETE FROM video_game_published WHERE video_game_id = ? AND contributor_id = ?";
        return tryUpdate(sql, nonNullPub.videoGameId(), nonNullPub.contributorId());
    }
    
}
 