package com.group9.backend.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.group9.backend.model.Tables.User;

@Repository
public class UserDao extends AbstractDao {
    
    static User grabUser(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("user_id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String email = resultSet.getString("email");
        Timestamp dob = resultSet.getTimestamp("dob");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        Timestamp lastAccessDate = resultSet.getTimestamp("last_access_date");
        Timestamp creationDate = resultSet.getTimestamp("creation_date");
        
        return new User(id, username, password, email, dob, firstName, lastName, lastAccessDate, creationDate);
    }
    
    public record GameRankInfo(
        long id,
        String title,
        String playtime,
        double rating
    ){};

    static GameRankInfo grabGameRankInfo(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("video_game_id");
        String title = resultSet.getString("title");
        String date = resultSet.getString("playtime");
        double rating = resultSet.getDouble("rating");
        return new GameRankInfo(id, title, date,rating);
    }

    
    public Optional<User> save(@NonNull User user) throws DaoException {
        String message = "The user to be added should not be null";
        User nonNullUser = Objects.requireNonNull(user, message);
        String sql = """
            INSERT INTO
            users(username, password, email, first_name, last_name, dob)
            VALUES(?, ?, ?, ?, ?, ?)""";
        
        return insertSingle(UserDao::grabUser, sql,
            nonNullUser.username(),
            nonNullUser.password(),
            nonNullUser.email(),
            nonNullUser.firstName(),
            nonNullUser.lastName(),
            nonNullUser.dob()
        );
    }

    public Optional<User> get(@NonNull Long id) throws DaoException {
        return selectSingle(UserDao::grabUser, "SELECT * FROM users WHERE user_id = ?", id);
    }

    public Collection<GameRankInfo> getFavorites(long id, String sort) throws DaoException {
        
        String sql = "";
        if (sort.equals("rating")){
            sql = """
                select rating, rating.video_game_id, playtime, video_game.title
                from rating
                left join (select sum(plays.finish - plays.start) as playtime,video_game_id from plays where plays.user_id = ? group by video_game_id) as playtime
                on (rating.video_game_id= playtime.video_game_id)
                inner join video_game
                on (rating.video_game_id = video_game.video_game_id)
                where rating.user_id = ?
                order by rating desc;
                    """;
        }
        else if (sort.equals("playtime")){
            sql = """
                select rating, rating.video_game_id, playtime, video_game.title
                from rating
                right join (select sum(plays.finish - plays.start) as playtime,video_game_id,user_id from plays where plays.user_id = ? group by video_game_id,user_id) as playtime
                on (rating.video_game_id= playtime.video_game_id and rating.user_id=playtime.user_id)
                inner join video_game
                on (playtime.video_game_id = video_game.video_game_id)
                where playtime.user_id = ?
                order by playtime desc ;
                    """;
        }
        else{
            sql = """
                select rating, playtime.video_game_id, playtime, video_game.title, (playtime*rating) as score
                from (select sum(plays.finish - plays.start) as playtime, plays.video_game_id,user_id
                from plays
                where user_id = ?
                group by plays.video_game_id,user_id) as playtime
                inner join
                (select rating, rating.video_game_id,user_id
                from rating) as rating
                on (playtime.video_game_id= rating.video_game_id and rating.user_id = playtime.user_id)
                inner join video_game
                on (rating.video_game_id = video_game.video_game_id)
                where playtime.user_id = ?
                order by score desc;
                    """;
        }




        return selectMany(UserDao::grabGameRankInfo, sql, id,id);
    }

    
    public Collection<User> getAll() throws DaoException {
        return selectMany(UserDao::grabUser, "SELECT * from users");
    }

    /**
     * NOTE: this method does not allow updating the `creation_date` field.
     */
    public boolean update(@NonNull User user) throws DaoException {
        String sql = """
            UPDATE users
            SET username = ?, password = ?, email = ?,
                first_name = ?, last_name = ?, dob = ?
            WHERE user_id = ?""";
        String message = "The user to be updated should not be null";
        User nonNullUser = Objects.requireNonNull(user, message);
        
        return tryUpdate(sql,
            nonNullUser.username(), nonNullUser.password(), nonNullUser.email(),
            nonNullUser.firstName(), nonNullUser.lastName(), nonNullUser.dob(),
            nonNullUser.id()
        );
    }

    
    public boolean delete(@NonNull User user) throws DaoException {
        return tryUpdate("DELETE FROM users WHERE user_id = ?", user.id());
    }

    public Collection<User> getByEmail(@NonNull String email) throws DaoException {
        return selectMany(UserDao::grabUser,
            "SELECT * FROM users WHERE email LIKE CONCAT('%', ?, '%')", email);
    }

    public Optional<User> login(String username, String password) throws DaoException {
        return selectSingle(UserDao::grabUser,
            "UPDATE users SET last_access_date=now() WHERE username = ? AND password = ? RETURNING *",
            username, password);
    }
    
}
