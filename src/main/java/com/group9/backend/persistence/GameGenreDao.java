package com.group9.backend.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.group9.backend.model.Tables.GameGenre;

@Repository
public class GameGenreDao extends AbstractDao {
    
    static GameGenre grabGameGenre(ResultSet resultSet) throws SQLException {
        long colID = resultSet.getLong("genre_id");
        long VGID = resultSet.getLong("video_game_id");
        
        return new GameGenre(colID, VGID);
    }

    public Optional<GameGenre> save(@NonNull GameGenre gameGenre) throws DaoException {
        String message = "The GameGenre to be added should not be null";
        GameGenre nonNullgameGenre = Objects.requireNonNull(gameGenre, message);
        
        String sql = """
                INSERT INTO game_genre(genre_id, video_game_id)
                VALUES(?,?)""";
        
        return insertSingle(GameGenreDao::grabGameGenre, sql,
            nonNullgameGenre.genreId(), nonNullgameGenre.videoGameId());
    }

    public Collection<GameGenre> getByGenre(long id) throws DaoException {
        String sql = "SELECT genre_id, video_game_id FROM game_genre WHERE genre_id = ?";
        return selectMany(GameGenreDao::grabGameGenre, sql, id);
    }

    public Collection<GameGenre> getByVideoGame(long id) throws DaoException {
        String sql = "SELECT genre_id, video_game_id FROM game_genre WHERE video_game_id = ?";
        return selectMany(GameGenreDao::grabGameGenre, sql, id);
    }
    

    public Collection<GameGenre> getAll() throws DaoException {
        String sql = "SELECT * FROM game_genre";
        return selectMany(GameGenreDao::grabGameGenre, sql);
    }

    public boolean delete(GameGenre gameGenre) throws DaoException {
        String message = "The GameGenre to be deleted should not be null";
        GameGenre nonNullFriend = Objects.requireNonNull(gameGenre, message);
        String sql = "DELETE FROM game_genre WHERE genre_id = ? AND video_game_id = ?";

        return tryUpdate(sql, nonNullFriend.genreId(), nonNullFriend.videoGameId());
    }
}
