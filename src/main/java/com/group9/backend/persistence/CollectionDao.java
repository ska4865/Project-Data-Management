package com.group9.backend.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.group9.backend.model.Tables.Collection;

@Repository
public class CollectionDao extends AbstractDao {

    static Collection grabCollection(ResultSet resultSet) throws SQLException {
        long userid = resultSet.getLong("user_id");
        long colid = resultSet.getLong("collection_id");
        String name = resultSet.getString("name");
        
        return new Collection(userid, colid,name);
    }

    public Optional<Collection> save(Collection collection) throws DaoException {
        String message = "The Collection to be added should not be null";
        Collection nonNullCollection = Objects.requireNonNull(collection, message);
        String sql = """
                INSERT INTO Collection(user_id, name)
                VALUES(?,?)""";
        return insertSingle(CollectionDao::grabCollection, sql, nonNullCollection.userId(), nonNullCollection.name());
    }

    public Optional<Collection> get(Long uID,Long cID) throws DaoException {
        String sql = """
                SELECT user_id, collection_id, name
                FROM collection
                WHERE user_id = ? AND collection_id = ?""";
        return selectSingle(CollectionDao::grabCollection, sql, uID, cID);
    }


    public java.util.Collection<Collection> getByUserAndName(Long uID,String name) throws DaoException {
        String sql = "SELECT * FROM collection WHERE user_id = ? AND name LIKE CONCAT('%', ?, '%')";
        return selectMany(CollectionDao::grabCollection, sql, uID, name);
    }


    

    public java.util.Collection<Collection> getAll() throws DaoException {
        return selectMany(CollectionDao::grabCollection, "SELECT * FROM collection");
    }

    public boolean update(Collection collection) throws DaoException {
        String sql = """
                UPDATE collection SET name = ?
                WHERE user_id = ? AND collection_id = ?""";
        return tryUpdate(sql, collection.name(), collection.userId(), collection.collectionId());
    }

    public boolean delete(Collection collection) throws DaoException {
        String sql = "DELETE FROM collection WHERE user_id = ? AND collection_id = ?";
        return tryUpdate(sql, collection.userId(), collection.collectionId());
    }

	public java.util.Collection<Collection> getByUser(long userId) throws DaoException {
        return selectMany(CollectionDao::grabCollection, "SELECT * FROM collection WHERE user_id = ?", userId);
	}
}
