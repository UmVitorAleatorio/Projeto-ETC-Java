package repository;

import conn.ConnectionFactory;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class EmployeeProfessionRepository {
    public void addProfessionToEmployee(Integer employeeId, Integer professionId) {
        log.info("Add profession to employee with id '{}'", employeeId);
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementAddProfession(conn, employeeId, professionId)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error while trying to link profession to employee", e);
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement createPreparedStatementAddProfession(Connection conn, Integer employeeId, Integer professionId) throws SQLException {
        String sql = """
                INSERT INTO public.employee_tag (employee_id, tag_id)
                VALUES (?, ?)
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, employeeId);
        ps.setInt(2, professionId);
        return ps;
    }

    public void removeProfessionFromEmployee(Integer employeeId, Integer professionId) {
        log.info("Remove profession from employee with id '{}'", employeeId);
        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement ps = createPreparedStatementRemoveProfession(conn, employeeId, professionId)){
            ps.executeUpdate();
        }catch (SQLException e) {
            log.error("Error while trying to remove profession from employee", e);
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement createPreparedStatementRemoveProfession(Connection conn, Integer employeeId, Integer professionId) throws SQLException {
        String sql = """
                DELETE FROM public.employee_tag
                WHERE employee_id = ? AND tag_id = ?
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, employeeId);
        ps.setInt(2, professionId);
        return ps;
    }

    public List<Integer> findProfessionIdsByEmployeeId(Integer employeeId) {
        List<Integer> professionIds = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementFindProfessionByEmployeeId(conn, employeeId);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                professionIds.add(rs.getInt("tag_id"));
            }
            return professionIds;
        }catch (SQLException e){
            log.error("Error while trying to get profession id from employee", e);
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement createPreparedStatementFindProfessionByEmployeeId(Connection conn, Integer employeeId) throws SQLException {
        String sql = """
                SELECT tag_id
                FROM public.employee_tag
                WHERE employee_id = ?
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, employeeId);
        return ps;
    }

    public boolean existsEmployeeIdAndProfessionId(Integer employeeId, Integer professionId) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementExistsEmployeeIdAndProfessionId(conn, employeeId, professionId)) {
            return ps.executeQuery().next();
        }catch (SQLException e){
            log.error("Error while trying to check employee profession existence", e);
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement createPreparedStatementExistsEmployeeIdAndProfessionId(Connection conn, Integer employeeId, Integer professionId) throws SQLException {
        String sql = """
                SELECT 1
                FROM public.employee_tag
                WHERE employee_id = ? AND tag_id = ?
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, employeeId);
        ps.setInt(2, professionId);
        return ps;
    }
}
