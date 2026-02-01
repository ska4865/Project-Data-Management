package com.group9.backend.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.postgresql.util.PGInterval;

import org.springframework.stereotype.Repository;

import com.group9.backend.model.Tables.CollectionGames;

@Repository
public class CollectionGamesDao extends AbstractDao {

    static CollectionGames grabCollectionGame(ResultSet resultSet) throws SQLException {
        long uID = resultSet.getLong("user_id");
        long colID = resultSet.getLong("collection_id");
        long VGID = resultSet.getLong("video_game_id");
        
        return new CollectionGames(uID,colID, VGID);
    }

    public Optional<CollectionGames> save(CollectionGames collectionGame) throws DaoException {
        String message = "The CollectionGames to be added should not be null";
        CollectionGames nonNullcollectionGame = Objects.requireNonNull(collectionGame, message);
        
        String sql = """
                INSERT INTO collection_games(user_id, collection_id, video_game_id)"
                VALUES(?,?,?)""";
        return insertSingle(CollectionGamesDao::grabCollectionGame, sql,
            nonNullcollectionGame.userId(), nonNullcollectionGame.collectionId(), nonNullcollectionGame.videoGameId());
    }

    public Collection<CollectionGames> getByCollection(long userId, long collectionId) throws DaoException {
        String sql = "SELECT user_id, collection_id, video_game_id FROM collection_games WHERE user_id = ? AND collection_id = ?";
        return selectMany(CollectionGamesDao::grabCollectionGame, sql, userId, collectionId);
    }

    public Collection<CollectionGames> getByVideoGame(long id) throws DaoException {
        String sql = "SELECT user_id, collection_id, video_game_id FROM collection_games WHERE video_game_id = ?";
        return selectMany(CollectionGamesDao::grabCollectionGame, sql, id);
    }

    public Collection<CollectionGames> getAll() throws DaoException {
        return selectMany(CollectionGamesDao::grabCollectionGame, "SELECT * FROM collection_games");
    }

    public boolean delete(CollectionGames collectionGame) throws DaoException {
        String message = "The collection to be deleted should not be null";
        CollectionGames nonNullCollectionGame = Objects.requireNonNull(collectionGame, message);
        String sql = "DELETE FROM collection_games WHERE collection_id = ? AND video_game_id = ? AND user_id = ?";
        return tryUpdate(sql, nonNullCollectionGame.collectionId(), collectionGame.videoGameId(), collectionGame.userId());
    }
    
    public Optional<PGInterval> getTotalPlaytime(long userId, long collectionId) throws DaoException {
        String sql = """
                SELECT SUM(P.finish-P.start) AS total FROM collection_games CG
                LEFT JOIN plays P ON CG.user_id = P.user_id AND CG.video_game_id = P.video_game_id
                WHERE CG.user_id = ? AND CG.collection_id = ?""";
        return selectSingle(
            s -> s.getObject("total", PGInterval.class),
            sql, userId, collectionId);
    }
}
