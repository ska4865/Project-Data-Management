package com.group9.backend.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.group9.backend.model.Tables.Rating;

@Repository
public class RatingDao extends AbstractDao {
    
    static Rating grabRating(ResultSet resultSet) throws SQLException {
        long userId = resultSet.getLong("user_id");
        long vidId = resultSet.getLong("video_game_id");
        double rating = resultSet.getDouble("rating");
        return new Rating(userId, vidId, rating);
    }

    public Optional<Rating> save(Rating rating) throws DaoException{
        String message = "The rating to be added should not be null";
        Rating nonNullRating = Objects.requireNonNull(rating, message);
        String sql = """
            INSERT INTO
            rating(user_id, video_game_id, rating)
            VALUES(?, ?, ?)""";
        
        return insertSingle(RatingDao::grabRating, sql,
            nonNullRating.userId(), nonNullRating.videoGameId(), nonNullRating.rating());
    }

    public Collection<Rating> getByGame(long id) throws DaoException {
        String sql = """
            SELECT * FROM rating
            WHERE video_game_id = ?""";
        return selectMany(RatingDao::grabRating, sql, id);
    }
    
    public Optional<Rating> get(long userId, long videoGameId) throws DaoException {
        String sql = """
            SELECT * FROM rating
            WHERE user_id = ?
            AND video_game_id = ?""";
        return selectSingle(RatingDao::grabRating, sql, userId, videoGameId);
    }

    /**
     * Returns the average rating of a videogame, if any
     * 
     * @param videoGameId the id of the videogame
     * @return the average rating, or empty if never rated
     * @throws DaoException if the select statement failed
     */
    public Optional<Double> getGameAverage(long videoGameId) throws DaoException {
        String sql = """
            SELECT AVG(rating) as avg
            FROM rating
            WHERE video_game_id = ?""";
        return selectSingle(s -> s.getDouble("avg"), sql, videoGameId);
    }

    public Collection<Rating> getByUser(long userId) throws DaoException {
        String sql = """
            SELECT * FROM rating
            WHERE user_id = ?""";
        return selectMany(RatingDao::grabRating, sql, userId);
    }

    public Collection<Rating> getAll() throws DaoException {
        return selectMany(RatingDao::grabRating, "SELECT * FROM rating");
    }

    public boolean update(Rating rating) throws DaoException {
        String message = "The rating to be updated should not be null";
        Rating nonNullRating = Objects.requireNonNull(rating, message);
        String sql = """
            UPDATE rating
            SET rating = ?
            WHERE video_game_id = ?
            AND user_id = ?""";
        return tryUpdate(sql,
            nonNullRating.rating(),
            nonNullRating.videoGameId(), nonNullRating.userId());
    }
    
    public boolean delete(Rating rating) throws DaoException {
        String message = "The rating to be deleted should not be null";
        Rating nonNullRating = Objects.requireNonNull(rating, message);
        String sql = "DELETE FROM rating WHERE user_id = ? AND video_game_id = ?";
        return tryUpdate(sql, nonNullRating.userId(), nonNullRating.videoGameId());
    }
    
}
 