package live.andiirham.absenkoey.Dao;

import live.andiirham.absenkoey.Model.DaftarAbsensi;
import live.andiirham.absenkoey.Model.DataSiswa;
import live.andiirham.absenkoey.Model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

public class RsExtractor {
    // mendapatkan resultset
    final static ResultSetExtractor<User> SINGLE_RS_EXTRACTOR = new ResultSetExtractor<User>()
    {
        @Override
        public User extractData(ResultSet rs) throws SQLException, DataAccessException {
            while (rs.next())
            {
                User user = new User(
                        rs.getLong("id"),
                        rs.getString("user_id"),
                        rs.getString("line_id"),
                        rs.getString("display_name")
                );
                return user;
            }
            return null;
        }
    };

    final static ResultSetExtractor<List<User>> MULTIPLE_RS_EXTRACTOR = new ResultSetExtractor<List<User>>()
    {
        @Override
        public List<User> extractData(ResultSet rs)
                throws SQLException, DataAccessException
        {
            List<User> list = new Vector<>();
            while(rs.next())
            {
                User p = new User(
                        rs.getLong("id"),
                        rs.getString("user_id"),
                        rs.getString("line_id"),
                        rs.getString("display_name")
                );
                list.add(p);
            }
            return null;
        }
    };

    final static ResultSetExtractor<DataSiswa> SINGLE_RS_EXTRACTOR_ABSEN= new ResultSetExtractor<DataSiswa>()
    {
        @Override
        public DataSiswa extractData(ResultSet rs)
                throws SQLException, DataAccessException
        {
            while (rs.next())
            {
                DataSiswa ds = new DataSiswa(
                        rs.getLong("id"),
                        rs.getString("no_absen"),
                        rs.getString("nama"),
                        rs.getString("no_bp"),
                        rs.getString("user_id")
                );
            }
            return null;
        }
    };

    final static ResultSetExtractor<List<DataSiswa>> MULTIPLE_RS_EXTRACTOR_ABSEN = new ResultSetExtractor<List<DataSiswa>>()
    {
        @Override
        public List<DataSiswa> extractData(ResultSet rs)
                throws SQLException, DataAccessException
        {
            List<DataSiswa> list = new Vector<>();
            while(rs.next())
            {
                DataSiswa ds = new DataSiswa(
                        rs.getLong("id"),
                        rs.getString("no_absen"),
                        rs.getString("nama"),
                        rs.getString("no_bp"),
                        rs.getString("user_id")
                );
                list.add(ds);
            }
            return null;
        }
    };

    final static ResultSetExtractor<DaftarAbsensi> SINGLE_RS_EXTRACTOR_ABSENSI = new ResultSetExtractor<DaftarAbsensi>()
    {
        @Override
        public DaftarAbsensi extractData(ResultSet rs) throws SQLException, DataAccessException {
            while (rs.next())
            {
                DaftarAbsensi ds = new DaftarAbsensi(
                        rs.getLong("id"),
                        rs.getString("user_id"),
                        rs.getString("no_absen"),
                        rs.getString("nama"),
                        rs.getString("no_bp"),
                        rs.getString("tanggal"),
                        rs.getString("jam")
                );
            }
            return null;
        }
    };

    final static ResultSetExtractor<List<DaftarAbsensi>> MULTIPLE_RS_EXTRACTOR_ABSENSI = new ResultSetExtractor<List<DaftarAbsensi>>()
    {
        @Override
        public List<DaftarAbsensi> extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<DaftarAbsensi> list = new Vector<>();
            while(rs.next())
            {
                DaftarAbsensi da = new DaftarAbsensi(
                        rs.getLong("id"),
                        rs.getString("user_id"),
                        rs.getString("no_absen"),
                        rs.getString("nama"),
                        rs.getString("no_bp"),
                        rs.getString("tanggal"),
                        rs.getString("jam")
                );
                list.add(da);
            }
            return null;
        }
    };
}
