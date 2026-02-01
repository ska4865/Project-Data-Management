package com.group9.backend.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.group9.backend.model.Tables.Contributor;

@Repository
public class ContributorDao extends AbstractDao {

    static Contributor grabContributor(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("contributor_id");
        String name = resultSet.getString("name");
        
        return new Contributor(id, name);
    }

    
    public Optional<Contributor> save(@NonNull Contributor contributor) throws DaoException {
        String message = "The Contributor to be added should not be null";
        Contributor nonNullContributor = Objects.requireNonNull(contributor, message);
        String sql = """
            INSERT INTO
            contributor(name)
            VALUES(?)""";
        return insertSingle(ContributorDao::grabContributor, sql, nonNullContributor.name());
    }
    
    public Optional<Contributor> get(long id) throws DaoException {
        String sql = """
            SELECT contributor_id, name
            FROM contributor
            WHERE contributor_id = ?""";
        return selectSingle(ContributorDao::grabContributor, sql, id);
    }

    public Collection<Contributor> getByName(@NonNull String name) throws DaoException {
        String sql = "SELECT * FROM contributor WHERE name LIKE CONCAT('%', ?, '%')";
        return selectMany(ContributorDao::grabContributor, sql, name);
    }
    
    
    public Collection<Contributor> getAll() throws DaoException {
        return selectMany(ContributorDao::grabContributor, "SELECT * FROM contributor");
    }

    
    public boolean update(@NonNull Contributor contributor) throws DaoException {
        String message = "The contributor to be updated should not be null";
        Contributor nonNullContributor = Objects.requireNonNull(contributor, message);
        String sql = """
            UPDATE contributor
            SET name = ?
            WHERE contributor_id = ?""";
        return tryUpdate(sql, nonNullContributor.name(), nonNullContributor.contributorId());
    }

    
    public boolean delete(@NonNull Contributor contributor) throws DaoException {
        String message = "The contributor to be deleted should not be null";
        Contributor nonNullContributor = Objects.requireNonNull(contributor, message);
        String sql = "DELETE FROM contributor WHERE contributor_id = ?";
        return tryUpdate(sql, nonNullContributor.contributorId());
    }
}
