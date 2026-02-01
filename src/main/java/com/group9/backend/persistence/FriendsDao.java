package com.group9.backend.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Repository;

import com.group9.backend.model.Tables.Friends;

@Repository
public class FriendsDao extends AbstractDao {
    
    public record FriendsInfo(
        long frienderUserId,
        String frienderUsername,
        long friendeeUserId,
        String friendeeUsername
    ){}

    static Friends grabFriends(ResultSet resultSet) throws SQLException {
        long FERid = resultSet.getLong("friender_user_id");
        long FEEid = resultSet.getLong("friendee_user_id");
        
        return new Friends(FERid, FEEid);
    }

    static FriendsInfo grabFriendsInfo(ResultSet resultSet) throws SQLException {
        long FERid = resultSet.getLong("friender_user_id");
        String FERUsername = resultSet.getString("friender_username");
        long FEEid = resultSet.getLong("friendee_user_id");
        String FEEUsername = resultSet.getString("friendee_username");
        
        return new FriendsInfo(FERid,FERUsername, FEEid,FEEUsername);
    }

    public Optional<Friends> save(Friends friend) throws DaoException {
        
        String message = "The Friend to be added should not be null";
        Friends nonNullfriend = Objects.requireNonNull(friend, message);
        
        String sql = """
            INSERT INTO friends(friender_user_id, friendee_user_id)
            SELECT ?,? WHERE NOT EXISTS(
              SELECT 1 FROM friends WHERE friendee_user_id=? AND friender_user_id=?
            )""";
        
        return insertSingle(FriendsDao::grabFriends, sql,
            nonNullfriend.frienderUserId(), nonNullfriend.friendeeUserId(),
            nonNullfriend.frienderUserId(), nonNullfriend.friendeeUserId());
    }

    public Collection<Friends> getByID(long id) throws DaoException {
        String sql = """
            (SELECT * FROM friends WHERE friender_user_id = ?)
            UNION (SELECT * FROM friends WHERE friendee_user_id = ?)""";
        
        return selectMany(FriendsDao::grabFriends, sql, id, id);
    }

    public Collection<FriendsInfo> getByIDWithName(long id) throws DaoException {
        String sql = """
            SELECT user_id, username FROM users RIGHT JOIN (
                (SELECT friendee_user_id AS id FROM friends WHERE friender_user_id = ?)
                    UNION
                (SELECT friender_user_id AS id FROM friends WHERE friendee_user_id = ?)
            ) AS f ON user_id = id;""";
        
        return selectMany(FriendsDao::grabFriendsInfo, sql, id, id);
    }

    public Collection<Friends> getAll() throws DaoException {
        return selectMany(FriendsDao::grabFriends, "SELECT * FROM friends");
    }

    public boolean delete(Friends friend) throws DaoException {
        String message = "The friend to be deleted should not be null";
        Friends nonNullFriend = Objects.requireNonNull(friend, message);
        String sql = "DELETE FROM friends WHERE friender_user_id = ? AND friendee_user_id = ?";

        return tryUpdate(sql, nonNullFriend.frienderUserId(), nonNullFriend.friendeeUserId());
    }
}
