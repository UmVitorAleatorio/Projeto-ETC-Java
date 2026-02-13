package repository;

import conn.ConnectionFactory;
import domain.profession.Profession;
import dto.ProfessionDTO;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ProfessionRepository {
    public Profession save(Profession profession) {

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementSave(conn, profession);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                profession.setId(rs.getInt("id"));
            }
            return profession;
        } catch (SQLException e) {
            log.error("Error while trying to save profession", e);
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement createPreparedStatementSave(Connection conn, Profession profession) throws SQLException {
        String sql = """
                INSERT INTO public.tag (code, name)
                VALUES (?,?)
                RETURNING id;
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, profession.getCode());
        ps.setString(2, profession.getName());
        return ps;
    }

    public List<ProfessionDTO> findByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<ProfessionDTO> professions = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementFindByIds(conn, ids);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                professions.add(mapToDTO(rs));
            }

            return professions;
        } catch (SQLException e) {
            log.error("Error while trying to find profession", e);
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement createPreparedStatementFindByIds(Connection conn, List<Integer> ids) throws SQLException {
        String placeholders = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));

        String sql = """
                SELECT id, code, name
                FROM public.tag
                WHERE id IN (%s)
                """.formatted(placeholders);

        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < ids.size(); i++) {
            ps.setInt(i + 1, ids.get(i));
        }
        return ps;
    }

    private ProfessionDTO mapToDTO(ResultSet rs) throws SQLException {
        return ProfessionDTO.builder()
                .id(rs.getInt("id"))
                .code(rs.getString("code"))
                .name(rs.getString("name"))
                .build();
    }

    public boolean existsById(Integer id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementExistsById(conn, id)) {
            return ps.executeQuery().next();
        } catch (SQLException e) {
            log.error("Error while trying to check if profession exists", e);
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement createPreparedStatementExistsById(Connection conn, Integer id) throws SQLException {
        String sql = """
                SELECT 1
                FROM public.tag
                WHERE id = ?
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }

    public List<ProfessionDTO> findAll() {
        List<ProfessionDTO> professions = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementFindAll(conn);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                professions.add(mapToDTO(rs));
            }
            return professions;

        } catch (SQLException e) {
            log.error("Error while trying to find professions", e);
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement createPreparedStatementFindAll(Connection conn) throws SQLException {
        String sql = """
                SELECT id, code, name
                FROM public.tag
                ORDER BY id
                """;
        return conn.prepareStatement(sql);
    }

    public List<ProfessionDTO> findByEmployeeId(Integer employeeId) {
        List<ProfessionDTO> professions = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementFindByEmployeeId(conn, employeeId);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                professions.add(mapToDTO(rs));
            }
            return professions;
        } catch (SQLException e) {
            log.error("Error while trying to find professions", e);
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement createPreparedStatementFindByEmployeeId(Connection conn, Integer employeeId) throws SQLException {
        String sql = """
                SELECT t.id, t.code, t.name
                FROM employee_tag et
                JOIN tag t ON t.id = et.tag_id
                WHERE et.employee_id = ?;
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, employeeId);
        return ps;
    }
}
