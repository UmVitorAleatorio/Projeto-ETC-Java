package repository;

import conn.ConnectionFactory;
import domain.address.Address;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Log4j2
public class AddressRepository {

    public Integer save(Address address) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementSave(conn, address);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("id");
            }
            throw new SQLException("Erro ao inserir o registro");
        } catch (SQLException e) {
            log.error("Erro ao salvar address", e);
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement createPreparedStatementSave(Connection conn, Address address) throws SQLException {
        String sql = """
                INSERT INTO public.address(state_id,city,street,house_number)
                VALUES (?, ?, ?, ?)
                RETURNING id
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, address.getState().getCode());
        ps.setString(2, address.getCity());
        ps.setString(3, address.getStreet());
        ps.setString(4, address.getNumber());
        return ps;
    }
}
