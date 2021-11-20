package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private final ResultSetExtractor<List<User>> resultSetExtractor = rs -> {
        Map<Integer, User> usersMap = new LinkedHashMap<>();
        User currentUser;
        int i = 0;
        while (rs.next()) {
            i++;
            currentUser = ROW_MAPPER.mapRow(rs, i);
            User oldUser = usersMap.putIfAbsent(currentUser.getId(), currentUser);
            String roleName = rs.getString("role");
            Role role = roleName != null ? Role.valueOf(roleName) : null;
            if (oldUser == null) {
                currentUser.addRole(role);
            } else {
                oldUser.addRole(role);
            }
        }
        return usersMap.values().stream().collect(Collectors.toList());
    };

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        ValidationUtil.validate(user);

        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else {
            if (namedParameterJdbcTemplate.update("""
                       UPDATE users SET name=:name, email=:email, password=:password, 
                       registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                    """, parameterSource) == 0) {
                return null;
            } else {
                jdbcTemplate.update("DELETE FROM user_roles WHERE user_id =?", user.getId());
            }
        }
        if (!user.getRoles().isEmpty()) {
            var list = user.getRoles()
                    .stream()
                    .map(role -> {
                        Object[] objects = new Object[2];
                        objects[0] = user.getId();
                        objects[1] = role.toString();
                        return objects;
                    }).
                    collect(Collectors.toList());
            jdbcTemplate.batchUpdate("INSERT INTO user_roles (user_id, role) VALUES (?,?)", list);
        }
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT u.*, ur.role FROM users u" +
                " LEFT OUTER JOIN user_roles ur on u.id = ur.user_id WHERE id=?", resultSetExtractor, id);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
        List<User> users = jdbcTemplate.query("SELECT u.*, ur.role FROM users u" +
                " LEFT OUTER JOIN user_roles ur on u.id = ur.user_id WHERE email=?", resultSetExtractor, email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("SELECT u.*,ur.role" +
                " FROM users u LEFT OUTER JOIN user_roles ur on u.id = ur.user_id ORDER BY name, email", resultSetExtractor);
    }
}
