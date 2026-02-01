package com.group9.backend.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.sql.Timestamp;

import org.postgresql.util.PGInterval;
import org.springframework.stereotype.Repository;

import com.group9.backend.model.Tables.Plays;

@Repository
public class PlaysDao extends AbstractDao {

    public record PlayInfo(Long id, String playtime) { }
    
    static Plays grabPlays(ResultSet resultSet) throws SQLException {
        long userId = resultSet.getLong("user_id");
        long vidId = resultSet.getLong("video_game_id");
        long sessionId = resultSet.getLong("session_id");
        Timestamp start = resultSet.getTimestamp("start");
        Timestamp finish = resultSet.getTimestamp("finish");
        return new Plays(userId, vidId, sessionId, start, finish);
    }

    public Optional<Plays> save(Plays play) throws DaoException {
        String message = "The play to be added should not be null";
        Plays nonNullPlay = Objects.requireNonNull(play, message);
        String sql = """
            INSERT INTO
            plays(user_id, video_game_id, start, finish)
            VALUES(?, ?, ?, ?)""";
        return insertSingle(PlaysDao::grabPlays, sql,
            nonNullPlay.userId(), nonNullPlay.videoGameId(), nonNullPlay.start(), nonNullPlay.finish());
    }


    public Collection<Plays> getByGame(Long id) throws DaoException {
        String sql = """
            SELECT * FROM plays
            WHERE video_game_id = ?""";
        return selectMany(PlaysDao::grabPlays, sql, id);
    }

    public Optional<Plays> get(Long id) throws DaoException {
        String sql = """
            SELECT * FROM plays
            WHERE session_id = ?""";
        return selectSingle(PlaysDao::grabPlays, sql, id);
    }

    public Collection<Plays> getByUser(Long id) throws DaoException {
        String sql = """
            SELECT * FROM plays
            WHERE user_id = ?""";
        return selectMany(PlaysDao::grabPlays, sql, id);
    }

    public Collection<Plays> getAll() throws DaoException {
        return selectMany(PlaysDao::grabPlays, "SELECT * FROM plays");
    }

    public Optional<PGInterval> getPlayTime(long userId, long videoGameId) throws DaoException {
        String sql = """
            SELECT SUM(finish - start) AS playtime
            FROM plays
            WHERE user_id = ?
            AND video_game_id = ?""";
        // return selectSingle(s -> s.getString("playtime"), sql, userId, videoGameId);
        return selectSingle(
            s -> s.getObject("playtime", PGInterval.class),
            sql, userId, videoGameId);
    }
    
    public boolean update(Plays play) throws DaoException {
        String message = "The play to be updated should not be null";
        Plays nonNullPlay = Objects.requireNonNull(play, message);
        String sql = """
            UPDATE plays SET
            start = ?, finish = ?
            WHERE video_game_id = ?
            AND user_id = ?
            AND session_id = ?""";
        
        return tryUpdate(sql,
            nonNullPlay.start(), nonNullPlay.finish(),
            nonNullPlay.videoGameId(), nonNullPlay.userId(), nonNullPlay.sessionId()
        );
    }

    public boolean delete(Plays play) throws DaoException {
        String message = "The play to be deleted should not be null";
        Plays nonNullPlay = Objects.requireNonNull(play, message);
        String sql = "DELETE FROM plays WHERE user_id = ? AND video_game_id = ? AND session_id = ?";
        return tryUpdate(sql, nonNullPlay.userId(), nonNullPlay.videoGameId(), nonNullPlay.sessionId());
    }

    public Optional<Plays> start(long userId, long videoGameId) throws DaoException {
        String sql = """
            INSERT INTO
            plays(user_id, video_game_id, start, finish)
            VALUES(?, ?, NOW(), null)""";
        return insertSingle(PlaysDao::grabPlays, sql, userId, videoGameId);
    }
    
}
 