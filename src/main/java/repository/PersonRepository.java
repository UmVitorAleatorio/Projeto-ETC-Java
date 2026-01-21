package repository;

import conn.ConnectionFactory;
import domain.Review.Avaliation;
import domain.document.Document;
import domain.document.TypeDocument;
import domain.person.Person;
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
    public static void save(Person person) {
        log.info("Saving person '{}'", person);

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementSave(conn, person)) {
            ps.execute();
        } catch (SQLException e) {
            log.error("Error while trying to saving person", e);
        }
    }

    private static PreparedStatement createPreparedStatementSave(Connection conn, Person person) throws SQLException {
        String sql = """
                INSERT INTO public.person(name, address, document_type, document_value, rating_avg, rating_count)
                VALUES (?, ?, ?, ?, ?, ?);
                """;
        return saveUpdateBody(conn, person, sql);
    }

    public static Optional<Person> findById(Integer id) {
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
        TypeDocument typeDocument = TypeDocument.fromCode(rs.getInt("document_type"));

        Document document = Document.builder()
                .typeDocument(typeDocument)
                .build();

        Avaliation avaliation = Avaliation.builder()
                .averageRating(rs.getDouble("rating_avg"))
                .ratingCount(rs.getInt("rating_count"))
                .build();


        return Person.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .address(rs.getString("address"))
                .document(document)
                .avaliation(avaliation)
                .build();
    }

    private static PreparedStatement createPreparedStatementFindById(Connection conn, Integer id) throws SQLException {
        String sql = """
                SELECT p.id, p.name, p.address, p.document_type, p.rating_avg, p.rating_count
                FROM public.person p
                WHERE p.id = ?;
                """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }

    public static List<Person> findByName(String name) {
        List<Person> persons = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementFindByName(conn, name);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                persons.add(findBy(rs));
            }
        } catch (SQLException e) {
            log.error("Error while trying to find person by name '{}'", name, e);
        }
        return persons;
    }

    private static PreparedStatement createPreparedStatementFindByName(Connection conn, String name) throws SQLException {
        String sql = """
                SELECT p.id, p.name, p.address, p.document_type, p.rating_avg, p.rating_count
                FROM public.person p
                WHERE p.name ILIKE ?;
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, String.format("%%%s%%", name));
        return ps;
    }

    public static void update(Person person) {
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
                SET name=?, address=?, document_type=?, document_value=?, rating_avg=?, rating_count=?
                WHERE id = ?;
                """;
        PreparedStatement ps = saveUpdateBody(conn, person, sql);
        ps.setInt(7, person.getId());
        return ps;
    }

    private static PreparedStatement saveUpdateBody(Connection conn, Person person, String sql) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, person.getName());
        ps.setString(2, person.getAddress());
        ps.setInt(3, person.getDocument().getTypeDocument().getCode());
        ps.setString(4, person.getDocument().getValue());
        ps.setDouble(5, person.getAvaliation().getAverageRating());
        ps.setInt(6, person.getAvaliation().getRatingCount());

        return ps;
    }

    public static void delete(Integer id) {
        log.info("Deleting person using the id '{}'", id);
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementDelete(conn, id)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error while trying to delete person '{}'", id, e);
        }
    }

    private static PreparedStatement createPreparedStatementDelete(Connection conn, Integer id) throws SQLException {
        String sql = "DELETE FROM public.person p WHERE p.id = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }
}
