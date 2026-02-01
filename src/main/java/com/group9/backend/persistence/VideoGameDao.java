package com.group9.backend.persistence;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.postgresql.util.PGInterval;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.group9.backend.model.Tables.VideoGame;

@Repository
public class VideoGameDao extends AbstractDao {
    
    public record GameInfo(
        long id,
        String title,
        String platform,
        String developer,
        String publisher,
        String releaseDate,
        String genre,
        String esrb,
        double price
    ) { };
    
    static GameInfo grabGameInfo(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("video_game_id");
        String title = resultSet.getString("title");
        double price = resultSet.getDouble("price");
        String esrb = resultSet.getString("esrb");
        String genre = resultSet.getString("genre_name");
        String releaseDate = resultSet.getString("release_date");
        String pubName = resultSet.getString("publisher_name");
        String devName = resultSet.getString("developer_name");
        String platName = resultSet.getString("platform_name");
        
        return new GameInfo(id, title, platName, devName, pubName, releaseDate, genre, esrb, price);
    }
    
    static VideoGame grabGame(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("video_game_id");
        String title = resultSet.getString("title");
        String esrb = resultSet.getString("esrb");
        return new VideoGame(id, title, esrb);
    }

    
    public Optional<VideoGame> save(@NonNull VideoGame game) throws DaoException {
        String message = "The game to be added should not be null";
        VideoGame nonNullGame = Objects.requireNonNull(game, message);
        String sql = """
            INSERT INTO
            video_game(title, esrb)
            VALUES(?, ?)""";
        return insertSingle(VideoGameDao::grabGame, sql,
            nonNullGame.title(), nonNullGame.ersb());
    }

    
    public Optional<VideoGame> get(long id) throws DaoException {
        String sql = """
            SELECT * FROM video_game
            WHERE video_game_id = ?""";
        return selectSingle(VideoGameDao::grabGame, sql, id);
    }

    
    public Collection<VideoGame> getAll() throws DaoException {
        return selectMany(VideoGameDao::grabGame, "SELECT * FROM video_game");
    }

    
    public boolean update(@NonNull VideoGame game) throws DaoException {
        String message = "The game to be updated should not be null";
        VideoGame nonNullGame = Objects.requireNonNull(game, message);
        String sql = """
            UPDATE video_game
            SET title = ?, esrb = ?
            WHERE video_game_id = ?""";
        return tryUpdate(sql, nonNullGame.title(), nonNullGame.ersb(),
            nonNullGame.id());
    }

    
    public boolean delete(@NonNull VideoGame game) throws DaoException {
        String message = "The game to be deleted should not be null";
        VideoGame nonNullGame = Objects.requireNonNull(game, message);
        String sql = "DELETE FROM video_game WHERE video_game_id = ?";
        return tryUpdate(sql, nonNullGame.id());
    }


    public Collection<GameInfo> getBy(String by, String term, String sort, String order) throws DaoException {
        String orderBy;
        if(sort.equals("def")) {
            orderBy = " ORDER BY title, release_date";
        } else {
            orderBy = " ORDER BY " + sort + " " + order;
        }
        String sql;
        if(!by.equals("release_date")){
            sql = """
                SELECT vg.video_game_id AS video_game_id, title, price, esrb, CAST(release_date AS DATE), plat.name AS platform_name,
                dev.name AS developer_name, pub.name AS publisher_name, gen.name AS genre_name
                FROM video_game AS vg
                LEFT JOIN
                release AS r
                ON vg.video_game_id=r.video_game_id
                LEFT JOIN
                platform AS plat
                ON r.platform_id=plat.platform_id
                LEFT JOIN
                video_game_developed AS devs
                ON vg.video_game_id=devs.video_game_id
                LEFT JOIN
                contributor AS dev
                ON devs.contributor_id=dev.contributor_id
                LEFT JOIN
                video_game_published AS pubs
                ON vg.video_game_id=pubs.video_game_id
                LEFT JOIN
                contributor AS pub
                ON pubs.contributor_id=pub.contributor_id
                LEFT JOIN
                game_genre AS gg
                ON vg.video_game_id=gg.video_game_id
                LEFT JOIN
                genre AS gen
                ON gg.genre_id=gen.genre_id
                WHERE\040""" + by + " = ? "
                + orderBy;
        } else {
            sql = """
                SELECT vg.video_game_id AS video_game_id, title, price, esrb, CAST(release_date AS DATE), plat.name AS platform_name,
                dev.name AS developer_name, pub.name AS publisher_name, gen.name AS genre_name
                FROM video_game AS vg
                LEFT JOIN
                release AS r
                ON vg.video_game_id=r.video_game_id
                LEFT JOIN
                platform AS plat
                ON r.platform_id=plat.platform_id
                LEFT JOIN
                video_game_developed AS devs
                ON vg.video_game_id=devs.video_game_id
                LEFT JOIN
                contributor AS dev
                ON devs.contributor_id=dev.contributor_id
                LEFT JOIN
                video_game_published AS pubs
                ON vg.video_game_id=pubs.video_game_id
                LEFT JOIN
                contributor AS pub
                ON pubs.contributor_id=pub.contributor_id
                LEFT JOIN
                game_genre AS gg
                ON vg.video_game_id=gg.video_game_id
                LEFT JOIN
                genre AS gen
                ON gg.genre_id=gen.genre_id
                WHERE CAST(release_date AS DATE) = CAST(? AS DATE) """
                + orderBy;
        }
        
        Object argument;
        if(by.equals("price")){
            argument = Float.parseFloat(term);
        } else if (by.equals("vg.video_game_id")){
            argument = Long.parseLong(term);
        } else if(!by.equals("release_date")){
            argument = term;
        } else {
            argument = Timestamp.valueOf(term + " 00:00:00");
        }
        
        return selectMany(VideoGameDao::grabGameInfo, sql, argument);
    }
    
    public record GameWithPlayInfo(VideoGame videoGame, int playCount, PGInterval playtime) {}
    
    static GameWithPlayInfo grabGameWithPlayInfo(ResultSet resultSet) throws SQLException {
        VideoGame videoGame = grabGame(resultSet);
        int playCount = resultSet.getInt("play_count");
        PGInterval playtime = resultSet.getObject("playtime", PGInterval.class);
        return new GameWithPlayInfo(videoGame, playCount, playtime);
    }
    
    public Collection<GameWithPlayInfo> top20In90Days() throws DaoException {
        return selectMany(VideoGameDao::grabGameWithPlayInfo,
        """
            SELECT vg.*, COUNT(DISTINCT p.user_id) AS play_count, SUM(p.finish - p.start) AS playtime FROM video_game vg
            LEFT JOIN (
                SELECT * FROM plays
                  WHERE finish IS NOT NULL
                  AND start >= now()::date - 90
              ) p ON vg.video_game_id = p.video_game_id
            GROUP BY vg.video_game_id
            ORDER BY (COUNT(DISTINCT p.user_id), SUM(p.finish - p.start)) DESC
            LIMIT 20
        """);
    }
    
    public Collection<GameWithPlayInfo> top5NewReleases() throws DaoException {
        return selectMany(VideoGameDao::grabGameWithPlayInfo,
        """
            SELECT vg.*, COUNT(DISTINCT p.user_id) AS play_count, SUM(p.finish - p.start) AS playtime FROM video_game vg
            LEFT JOIN (
                SELECT * FROM plays
                  WHERE finish IS NOT NULL
              ) p ON vg.video_game_id = p.video_game_id
            WHERE vg.video_game_id IN
                    (SELECT DISTINCT(video_game_id)
                    FROM release
                    WHERE DATE_TRUNC('month', release_date) = DATE_TRUNC('month', NOW()))
            GROUP BY vg.video_game_id
            ORDER BY (COUNT(DISTINCT p.user_id), SUM(p.finish - p.start)) DESC
            LIMIT 5;
        """);
    }
    
    public Collection<GameWithPlayInfo> top20AmongFriends(long userId) throws DaoException {
        return selectMany(VideoGameDao::grabGameWithPlayInfo,
        """
            SELECT vg.*, COUNT(DISTINCT p.user_id) AS play_count, SUM(p.finish - p.start) AS playtime FROM video_game vg
            RIGHT JOIN -- discard all video games with no plays
                (SELECT * FROM plays
                WHERE finish IS NOT NULL -- discard all plays with no finish
                    AND user_id IN
                    ((SELECT friender_user_id FROM friends WHERE friendee_user_id = ?)
                        UNION
                    (SELECT friendee_user_id FROM friends WHERE friender_user_id = ?))
                ) p ON vg.video_game_id = p.video_game_id
            GROUP BY vg.video_game_id
            ORDER BY (SUM(p.finish - p.start), COUNT(DISTINCT p.user_id)) DESC
            LIMIT 20;
        """, userId, userId);
    }
    
}
 