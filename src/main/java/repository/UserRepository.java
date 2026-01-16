package repository;

import conn.ConnectionFactory;
import domain.user.Role;
import dto.UserPersonDTO;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Log4j2
public class UserRepository {
    public static Optional<UserPersonDTO> findById(Integer id) {
        log.info("Find user by id: {}", id);

        Map<Integer, UserPersonDTO.UserPersonDTOBuilder> map = new HashMap<>();
        Map<Integer, Set<Role>> rolesMap = new HashMap<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementFindById(conn, id);
             ResultSet rs = ps.executeQuery()) {

            findBy(map, rolesMap, rs);

        } catch (SQLException e) {
            log.error("Error finding user by id: {}", id, e);
            return Optional.empty();
        }

        return map.entrySet().stream()
                .findFirst()
                .map(e -> e.getValue().roles(rolesMap.get(e.getKey())).build());
    }
    public static PreparedStatement createPreparedStatementFindById(Connection conn, Integer id) throws SQLException {

        String sql = """
                SELECT u.id AS user_id, u.email, p.name, p.address, p.rating_avg, p.rating_count, r.name AS role_name
                FROM public.users u
                JOIN public.person p ON u.person_id = p.id
                JOIN public.user_role ur ON ur.user_id = u.id
                JOIN public.role r ON r.id = ur.role_id
                WHERE u.id = ?;
                """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }

    private static void findBy(Map<Integer, UserPersonDTO.UserPersonDTOBuilder> map, Map<Integer, Set<Role>> rolesMap, ResultSet rs) throws SQLException {
        while (rs.next()) {
            int userId = rs.getInt("user_id");

            var builder = map.get(userId);
            if (builder == null) {
                builder = UserPersonDTO.builder()
                        .id(userId)
                        .email(rs.getString("email"))
                        .name(rs.getString("name"))
                        .address(rs.getString("address"))
                        .ratingAvg(rs.getDouble("rating_avg"))
                        .ratingCount(rs.getInt("rating_count"));
                map.put(userId, builder);
            }

            String roleStr = rs.getString("role_name");
            if (roleStr != null) {
                rolesMap.computeIfAbsent(userId, k -> new HashSet<>())
                        .add(Role.valueOf(roleStr));
            }
        }
    }

    public static List<UserPersonDTO> findByName(String name) {
        Map<Integer, UserPersonDTO.UserPersonDTOBuilder> map = new HashMap<>();
        Map<Integer, Set<Role>> rolesMap = new HashMap<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementFindByName(conn, name);
             ResultSet rs = ps.executeQuery()) {

            findBy(map, rolesMap, rs);

        } catch (SQLException e) {
            log.error("Error while trying to find users by name", e);
        }

        return map.entrySet().stream()
                .map(e -> e.getValue().roles(rolesMap.get(e.getKey())).build())
                .toList();
    }

    private static PreparedStatement createPreparedStatementFindByName(Connection conn, String name) throws SQLException {
        String sql = """ 
                SELECT u.id AS user_id, u.email, p.name, p.address, p.rating_avg, p.rating_count, r.name AS role_name
                FROM public.users u
                JOIN public.person p ON u.person_id = p.id
                JOIN public.user_role ur ON ur.user_id = u.id
                JOIN public.role r ON r.id = ur.role_id
                WHERE p.name ILIKE ?;
                """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, String.format("%%%s%%", name));
        return ps;
    }
}