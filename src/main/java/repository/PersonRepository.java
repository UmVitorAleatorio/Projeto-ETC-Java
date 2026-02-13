package repository;

import conn.ConnectionFactory;
import domain.Review.Avaliation;
import domain.address.Address;
import domain.address.State;
import domain.document.Document;
import domain.document.TypeDocument;
import domain.person.Person;
import dto.PersonWithUserDTO;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class PersonRepository {
    public int save(Person person) {
        log.info("Saving person '{}'", person);

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementSave(conn, person)) {
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            log.error("Error while trying to saving person", e);
        }
        throw new RuntimeException("Error while saving person");
    }

    private static PreparedStatement createPreparedStatementSave(Connection conn, Person person) throws SQLException {
        String sql = """
                INSERT INTO public.person(name, document_type, document_value, rating_avg, rating_count, address_id)
                VALUES (?, ?, ?, ?, ?, ?);
                """;
        return saveUpdateBody(conn, person, sql);
    }

    public Optional<Person> findById(Integer id) {
        log.info("Finding person by id '{}'", id);

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementFindById(conn, id);
             ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) return Optional.empty();

            Person person = findBy(rs);

            return Optional.of(person);
        } catch (SQLException e) {
            log.error("Error while trying to find person by id '{}'", id, e);
        }
        return Optional.empty();
    }

    private static Person findBy(ResultSet rs) throws SQLException {
        State state = State.fromCode(rs.getInt("state_id"));

        Address address = Address.builder()
                .id(rs.getInt("address_id"))
                .state(state)
                .city(rs.getString("city"))
                .street(rs.getString("street"))
                .number(rs.getString("house_number"))
                .build();

        TypeDocument typeDocument = TypeDocument.fromCode(rs.getInt("document_type"));

        Document document = Document.builder()
                .typeDocument(typeDocument)
                .value(rs.getString("document_value"))
                .build();

        Avaliation avaliation = Avaliation.builder()
                .averageRating(rs.getDouble("rating_avg"))
                .ratingCount(rs.getInt("rating_count"))
                .build();


        return Person.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .address(address)
                .document(document)
                .avaliation(avaliation)
                .build();
    }

    private static PreparedStatement createPreparedStatementFindById(Connection conn, Integer id) throws SQLException {
        String sql = """
                SELECT p.id, p.name, a.id AS address_id, a.street, a.house_number, a.city, s.id AS state_id, p.document_type, p.document_value, p.rating_avg, p.rating_count
                FROM public.person p
                JOIN public.address a ON a.id = p.address_id
                JOIN public.state s ON s.id = a.state_id
                WHERE p.id = ?;
                """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }

    public List<PersonWithUserDTO> findByName(String name) {
        List<PersonWithUserDTO> persons = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementFindByName(conn, name);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                persons.add(mapToPersonWithUserDTO(rs));
            }
        } catch (SQLException e) {
            log.error("Error while trying to find person by name '{}'", name, e);
        }
        return persons;
    }

    private static PersonWithUserDTO mapToPersonWithUserDTO(ResultSet rs) throws SQLException {
        TypeDocument typeDocument = TypeDocument.fromCode(rs.getInt("document_type"));

        return new PersonWithUserDTO(
                rs.getInt("person_id"),
                rs.getString("name"),
                rs.getString("street"),
                rs.getString("city"),
                rs.getString("state"),
                typeDocument.name(),
                rs.getDouble("rating_avg"),
                rs.getInt("rating_count"),
                rs.getObject("user_id", Integer.class)
        );
    }

    private static PreparedStatement createPreparedStatementFindByName(Connection conn, String name) throws SQLException {
        String sql = """
                SELECT p.id AS person_id, p.name, a.street, a.city, s.uf AS state, p.document_type, p.rating_avg, p.rating_count, u.id AS user_id
                FROM public.person p
                JOIN public.address a ON a.id = p.address_id
                JOIN public.state s ON s.id = a.state_id
                LEFT JOIN public.users u ON u.person_id = p.id
                WHERE p.name ILIKE ?;
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, String.format("%%%s%%", name));
        return ps;
    }

    public void update(Person person) {
        log.info("Updating person '{}'", person);
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementUpdate(conn, person)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error while trying to update person '{}'", person.getId(), e);
        }
    }

    private static PreparedStatement createPreparedStatementUpdate(Connection conn, Person person) throws SQLException {
        String sql = """
                UPDATE public.person p
                SET name=?, document_type=?, document_value=?, rating_avg=?, rating_count=?, address_id=?
                WHERE id = ?;
                """;
        PreparedStatement ps = saveUpdateBody(conn, person, sql);
        ps.setInt(7, person.getId());
        return ps;
    }

    private static PreparedStatement saveUpdateBody(Connection conn, Person person, String sql) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, person.getName());
        ps.setInt(2, person.getDocument().getTypeDocument().getCode());
        ps.setString(3, person.getDocument().getValue());
        ps.setDouble(4, person.getAvaliation().getAverageRating());
        ps.setInt(5, person.getAvaliation().getRatingCount());
        ps.setInt(6, person.getAddress().getId());

        return ps;
    }

    public void delete(Integer id) {
        log.info("Deleting person using the id '{}'", id);
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementDelete(conn, id)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error while trying to delete person with id - '{}'", id, e);
        }
    }

    private static PreparedStatement createPreparedStatementDelete(Connection conn, Integer id) throws SQLException {
        String sql = "DELETE FROM public.person p WHERE p.id = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }

    public Optional<Person> findByDocument(String value, TypeDocument type) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementFindByDocument(conn, value, type);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                Person p = Person.builder()
                        .id(rs.getInt("id"))
                        .build();
                return Optional.of(p);
            }
        } catch (SQLException e) {
            log.error("Error while trying to find document", e);
//            throw new RuntimeException();
        }
        return Optional.empty();
    }

    private static PreparedStatement createPreparedStatementFindByDocument(Connection conn, String value, TypeDocument type) throws SQLException {
        String sql = """
                SELECT id
                FROM public.person p
                WHERE document_value = ? AND document_type = ?
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, value);
        ps.setInt(2, type.getCode());

        return ps;
    }
}
