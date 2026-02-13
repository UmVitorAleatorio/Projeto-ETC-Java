package repository;

import conn.ConnectionFactory;
import domain.document.TypeDocument;
import domain.person.Employee;
import dto.AddressDTO;
import dto.EmployeePersonDTO;
import lombok.extern.log4j.Log4j2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class EmployeeRepository {
    public void save(Employee employee) {
        log.info("Saving employee '{}'", employee);

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementSave(conn, employee)) {
            ps.execute();
        } catch (SQLException e) {
            log.error("Error while trying to saving employee", e);
        }
    }

    private PreparedStatement createPreparedStatementSave(Connection conn, Employee employee) throws SQLException {
        String sql = """
                INSERT INTO public.employee(created_at, user_id, working, active)
                VALUES (?, ?, ?, ?)
                """;
        return prepareSaveStatement(conn, employee, sql);
    }

    private PreparedStatement prepareSaveStatement(Connection conn, Employee employee, String sql) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setTimestamp(1, Timestamp.valueOf(employee.getCreatedAt()));
        ps.setInt(2, employee.getUserId());
        ps.setBoolean(3, employee.isWorking());
        ps.setBoolean(4, employee.isActive());

        return ps;
    }

    public Optional<EmployeePersonDTO> findEmployeeDetailsById(Integer employeeId) {
        log.info("Finding employee by id {}", employeeId);

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementFindById(conn, employeeId);
             ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) return Optional.empty();

            EmployeePersonDTO employee = mapToDTO(rs);

            return Optional.of(employee);
        } catch (SQLException e) {
            log.error("Error while trying to find employee by id '{}'", employeeId, e);
        }
        return Optional.empty();
    }

    private PreparedStatement createPreparedStatementFindById(Connection conn, Integer id) throws SQLException {
        String sql = """
                SELECT e.id AS employee_id, e.created_at, e.working, e.active, u.id AS user_id, p.id AS person_id, p.name, p.document_type, p.document_value, a.city, s.uf AS state
                FROM employee e
                JOIN users u ON u.id = e.user_id
                JOIN person p ON p.id = u.person_id
                JOIN address a ON p.address_id = a.id
                JOIN state s ON a.state_id = s.id
                WHERE e.id = ?;
                """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }

    private static EmployeePersonDTO mapToDTO(ResultSet rs) throws SQLException {
        TypeDocument typeDocument = TypeDocument.fromCode(rs.getInt("document_type"));

        AddressDTO address = AddressDTO.builder()
                .state(rs.getString("state"))
                .city(rs.getString("city"))
                .build();

        return EmployeePersonDTO.builder()
                .employeeId(rs.getInt("employee_id"))
                .personId(rs.getInt("person_id"))
                .userId(rs.getInt("user_id"))
                .name(rs.getString("name"))
                .documentType(typeDocument.name())
                .documentValue(rs.getString("document_value"))
                .working(rs.getBoolean("working"))
                .active(rs.getBoolean("active"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .address(address)
                .build();
    }

    public List<EmployeePersonDTO> findByName(String name) {
        List<EmployeePersonDTO> employees = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementFindByName(conn, name);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                employees.add(mapToDTO(rs));
            }

        } catch (SQLException e) {
            log.error("Error while trying to find employees by name", e);
        }
        return employees;
    }

    private PreparedStatement createPreparedStatementFindByName(Connection conn, String name) throws SQLException {
        String sql = """
                SELECT e.id AS employee_id, e.created_at, e.working, e.active, u.id AS user_id, p.id AS person_id, p.name, p.document_type, p.document_value, a.city, s.uf AS state
                FROM employee e
                JOIN users u ON u.id = e.user_id
                JOIN person p ON p.id = u.person_id
                JOIN address a ON p.address_id = a.id
                JOIN state s ON a.state_id = s.id
                WHERE p.name ILIKE ?;
                """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, String.format("%%%s%%", name));
        return ps;
    }

    public List<EmployeePersonDTO> findByAddress(String state, String city) {
        List<EmployeePersonDTO> employees = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementFindByAddress(conn, state, city);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                employees.add(mapToDTO(rs));
            }

        } catch (SQLException e) {
            log.error("Error while trying to find employees by address", e);
        }
        return employees;
    }

    private PreparedStatement createPreparedStatementFindByAddress(Connection conn, String state, String city) throws SQLException {
        String sql = """
                SELECT e.id AS employee_id, e.created_at, e.working, e.active, u.id AS user_id, p.id AS person_id, p.name, p.document_type, p.document_value, a.city, s.uf AS state
                FROM public.employee e
                JOIN public.users u ON u.id = e.user_id
                JOIN public.person p ON p.id = u.person_id
                JOIN public.address a ON p.address_id = a.id
                JOIN public.state s ON a.state_id = s.id
                WHERE LOWER(s.uf) = LOWER(?) AND LOWER(a.city) LIKE LOWER(?) AND e.active = true;
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, state);
        ps.setString(2, String.format("%%%s%%", city));
        return ps;
    }

    public List<EmployeePersonDTO> findByCodeAndCity(Integer id, String city, String state) {
        List<EmployeePersonDTO> employees = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementFindByCodeAndCityWithState(conn, id, city, state);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                employees.add(mapToDTO(rs));
            }

            return employees;
        } catch (SQLException e) {
            log.error("Error while trying to find employees by code and city", e);
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement createPreparedStatementFindByCodeAndCityWithState(Connection conn, Integer id, String city, String state) throws SQLException {
        String sql = """
                SELECT DISTINCT e.id AS employee_id, e.created_at, e.working, e.active, u.id AS user_id, p.id AS person_id, p.name, p.document_type, p.document_value, a.city, s.uf AS state
                FROM employee e
                JOIN users u ON u.id = e.user_id
                JOIN person p ON p.id = u.person_id
                JOIN address a ON p.address_id = a.id
                JOIN state s ON a.state_id = s.id
                JOIN employee_tag et ON et.employee_id = e.id
                JOIN tag t ON t.id = et.tag_id
                WHERE t.id = ? AND a.city ILIKE ? AND s.uf = ?;
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.setString(2, String.format("%%%s%%",city));
        ps.setString(3, state);
        return ps;
    }

    public void deactivating(Integer id) {
        log.info("Deactivating employee using the id {}", id);

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementDeactivating(conn, id)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error while trying to deactivating employee with id - '{}'", id, e);
        }
    }

    private PreparedStatement createPreparedStatementDeactivating(Connection conn, Integer id) throws SQLException {
        String sql = """
                UPDATE public.employee
                SET active = false, working = false
                WHERE id = ?
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }

    public void activating(Integer id) {
        log.info("Activating employee using the id {}", id);

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementActivating(conn, id)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error while trying to activating employee with id - '{}'", id, e);
        }
    }

    private PreparedStatement createPreparedStatementActivating(Connection conn, Integer id) throws SQLException {
        String sql = """
                UPDATE public.employee
                SET active = true, working = false
                WHERE id = ?
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }

    public boolean existsByUserId(Integer userId) {
        String sql = "SELECT 1 FROM public.employee WHERE user_id = ? AND active = true";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
