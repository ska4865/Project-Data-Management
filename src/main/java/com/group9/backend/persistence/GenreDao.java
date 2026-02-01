package com.group9.backend.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.group9.backend.model.Tables.Genre;

@Repository
public class GenreDao extends AbstractDao {
    
    static Genre grabGenre(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("genre_id");
        String name = resultSet.getString("name");
        
        return new Genre(id, name);
    }

    
    public Optional<Genre> save(@NonNull Genre genre) throws DaoException {
        String message = "The Genre to be added should not be null";
        Genre nonNullGenre = Objects.requireNonNull(genre, message);
        String sql = """
            INSERT INTO
            genre(name)
            VALUES(?)""";
        return insertSingle(GenreDao::grabGenre, sql, nonNullGenre.name());
    }

    public Optional<Genre> get(long id) throws DaoException {
        String sql = """
            SELECT genre_id, name
            FROM genre
            WHERE genre_id = ?""";
        return selectSingle(GenreDao::grabGenre, sql, id);
        
    }

    public Collection<Genre> getByName(@NonNull String name) throws DaoException {
        String sql = "SELECT * FROM genre WHERE name LIKE CONCAT('%', ?, '%')";
        return selectMany(GenreDao::grabGenre, sql, name);
    }
    
    
    public Collection<Genre> getAll() throws DaoException {
        return selectMany(GenreDao::grabGenre, "SELECT * FROM genre");
    }

    
    public boolean update(@NonNull Genre genre) throws DaoException{
        String message = "The genre to be updated should not be null";
        Genre nonNullGenre = Objects.requireNonNull(genre, message);
        String sql = """
            UPDATE genre
            SET name = ?
            WHERE genre_id = ?""";
        return tryUpdate(sql, nonNullGenre.name(), nonNullGenre.genreId());
    }
    
    
    public boolean delete(@NonNull Genre genre) throws DaoException {
        String message = "The genre to be deleted should not be null";
        Genre nonNullGenre = Objects.requireNonNull(genre, message);
        String sql = "DELETE FROM genre WHERE genre_id = ?";
        return tryUpdate(sql, nonNullGenre.genreId());
    }
}
