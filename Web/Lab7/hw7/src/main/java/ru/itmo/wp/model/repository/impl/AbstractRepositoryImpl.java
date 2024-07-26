package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.database.DatabaseUtils;
import ru.itmo.wp.model.domain.Base;
import ru.itmo.wp.model.exception.RepositoryException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractRepositoryImpl<T extends Base> {

    protected final DataSource DATA_SOURCE = DatabaseUtils.getDataSource();

    private final String tableName;

    protected AbstractRepositoryImpl(String tableName) {
        this.tableName = tableName;
    }

    protected T find(long id) {
        try (Connection connection = DATA_SOURCE.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE id=?")) {
                statement.setLong(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return toBaseImpl(statement.getMetaData(), resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Can't find " + tableName + ".", e);
        }
    }

    protected T findBy(String query, String... params) {
        try (Connection connection = DATA_SOURCE.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                for (int i = 0; i < params.length; i++) {
                    statement.setString(i + 1, params[i]);
                }
                try (ResultSet resultSet = statement.executeQuery()) {
                    return toBaseImpl(statement.getMetaData(), resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Can't find " + tableName + ".", e);
        }
    }

    protected List<T> findAll() {
        List<T> entities = new ArrayList<>();
        try (Connection connection = DATA_SOURCE.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName + " ORDER BY id DESC")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    T entity;
                    while ((entity = toBaseImpl(statement.getMetaData(), resultSet)) != null) {
                        entities.add(entity);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Can't find " + tableName + ".", e);
        }
        return entities;
    }

    protected List<T> findAllBy(Map<String, String> args) {
        if (args.isEmpty()) {
            return null;
        }

        List<String> st = new ArrayList<>();
        List<String> values = new ArrayList<>();
        for (var entry : args.entrySet()) {
            st.add(entry.getKey() + "=?");
            values.add(entry.getValue());
        }

        String keysStatement = String.join(" AND ", st);

        List<T> entities = new ArrayList<>();
        try (Connection connection = DATA_SOURCE.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE " + keysStatement + " ORDER BY id DESC")) {
                for (int i = 0; i < values.size(); i++) {
                    statement.setString(i + 1, values.get(i));
                }
                try (ResultSet resultSet = statement.executeQuery()) {
                    T entity;
                    while ((entity = toBaseImpl(statement.getMetaData(), resultSet)) != null) {
                        entities.add(entity);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Can't find " + tableName + ".", e);
        }
        return entities;
    }

    protected void save(T entity, Map<String, String> args) {
        try (Connection connection = DATA_SOURCE.getConnection()) {
            List<String> keys = new ArrayList<>();
            List<String> values = new ArrayList<>();
            for (Map.Entry<String, String> kv : args.entrySet()) {
                keys.add("`" + kv.getKey() + "`");
                values.add(kv.getValue());
            }
            String keysStatement = String.join(", ", keys);
            String valuesStatement = String.join(", ", Collections.nCopies(keys.size(), "?"));
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + tableName + " (" + keysStatement + ", `creationTime`) VALUES (" + valuesStatement + ", NOW())", Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < keys.size(); i++) {
                    statement.setString(i + 1, values.get(i));
                }

                if (statement.executeUpdate() != 1) {
                    throw new RepositoryException("Can't save " + tableName + ".");
                } else {
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getLong(1));
                        entity.setCreationTime(find(entity.getId()).getCreationTime());
                    } else {
                        throw new RepositoryException("Can't save " + tableName + " [no autogenerated fields].");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Can't save " + tableName + ".", e);
        }
    }

    protected void updateFields(T entity, Map<String, String> args) {
        if (args.isEmpty()) {
            return;
        }

        List<String> st = new ArrayList<>();
        List<String> values = new ArrayList<>();
        for (var entry : args.entrySet()) {
            st.add(entry.getKey() + "=?");
            values.add(entry.getValue());
        }

        String keysStatement = String.join(", ", st);
        try (Connection connection = DATA_SOURCE.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(String.format("UPDATE " + tableName + " SET " + keysStatement + " WHERE id=%d", entity.getId()))) {
                for (int i = 0; i < values.size(); i++) {
                    statement.setString(i + 1, values.get(i));
                }
                if (statement.executeUpdate() != 1) {
                    throw new RepositoryException(String.format("Can't update %s, id = ", tableName) + entity.getId());
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Can't update " + tableName + ", entity: " + entity.getId(), e);
        }
    }

    protected abstract T toBase(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException;

    private T toBaseImpl(ResultSetMetaData metaData, ResultSet resultSet) {
        try {
            return toBase(metaData, resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Can't convert object, sql exception", e);
        }
    }
}