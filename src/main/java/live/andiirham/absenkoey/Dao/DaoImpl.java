package live.andiirham.absenkoey.Dao;

import live.andiirham.absenkoey.Model.DataSiswa;
import live.andiirham.absenkoey.Model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class DaoImpl implements Dao
{
    // get time
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    String timestamp = formatter.format(date);

    // query untuk table user
    private final static String USER_TABLE="tbl_user";
    private final static String SQL_SELECT_ALL="SELECT id, user_id, line_id, display_name FROM "+USER_TABLE;
    private final static String SQL_GET_BY_USER_ID=SQL_SELECT_ALL + " WHERE LOWER(user_id) LIKE LOWER(?);";
    private final static String SQL_REGISTER="INSERT INTO "+USER_TABLE+" (user_id, line_id, display_name) VALUES (?, ?, ?);";
    
    // query untuk table absen
    private final static String ABSEN_TABLE="tbl_absen";
    private final static String SQL_SELECT_ALL_ABSEN="SELECT id, no_absen, nama, no_bp, jam FROM "+ABSEN_TABLE;
    private final static String SQL_JOIN_EVENT="INSERT INTO"+ABSEN_TABLE+"(no_absen, nama, no_bp, jam) VALUES (?,?,?,?);";
    private static final String SQL_GET_BY_ABSEN = SQL_SELECT_ALL_ABSEN + "WHERE LOWER (no_absen) LIKE LOWER(?);";
    private final static String SQL_GET_BY_JOIN=SQL_SELECT_ALL_ABSEN + " WHERE no_absen = ? AND user_id = ?;";

    private JdbcTemplate mJdbc;

    // mendapatkan resultset
    private final static ResultSetExtractor<User> SINGLE_RS_EXTRACTOR = new ResultSetExtractor<User>()
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

    private final static ResultSetExtractor<List<User>> MULTIPLE_RS_EXTRACTOR = new ResultSetExtractor<List<User>>()
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

    private final static ResultSetExtractor<DataSiswa> SINGLE_RS_EXTRACTOR_ABSEN= new ResultSetExtractor<DataSiswa>()
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
                        rs.getString("jam")
                );
            }
            return null;
        }
    };

    private final static ResultSetExtractor<List<DataSiswa>> MULTIPLE_RS_EXTRACTOR_ABSEN = new ResultSetExtractor<List<DataSiswa>>()
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
                        rs.getString("jam")
                );
                list.add(ds);
            }
            return null;
        }
    };

    public DaoImpl(DataSource aDataSource)
    {
        mJdbc = new JdbcTemplate(aDataSource);
    }

    @Override
    public List<User> get()
    {
        return mJdbc.query(SQL_SELECT_ALL, MULTIPLE_RS_EXTRACTOR);
    }

    @Override
    public List<User> getByUserId(String aUserId)
    {
        return mJdbc.query(SQL_GET_BY_USER_ID,
                new Object[]{"%"+aUserId+"%"},
                MULTIPLE_RS_EXTRACTOR);
    }

    @Override
    public int registerLineId(String no_absen, String nama, String aDisplayName) {
        return mJdbc.update(SQL_REGISTER,
                new Object[]{no_absen, nama, aDisplayName, aDisplayName});
    }

    @Override
    public int joinAbsen(String no_absen, String nama, String no_bp) {
        return mJdbc.update(SQL_JOIN_EVENT, new Object[]{no_absen, nama, no_bp, timestamp});
    }

    @Override
    public List<DataSiswa> getAbsen()
    {
        return mJdbc.query(SQL_SELECT_ALL_ABSEN, MULTIPLE_RS_EXTRACTOR_ABSEN);
    }

    @Override
    public List<DataSiswa> getByNoAbsen(String no_absen) {
        return mJdbc.query(SQL_GET_BY_ABSEN, new Object[]{"%"+no_absen+"%"}, MULTIPLE_RS_EXTRACTOR_ABSEN);
    }

    @Override
    public List<DataSiswa> getByJoin(String no_absen, String aUserId) {
        return mJdbc.query(SQL_GET_BY_JOIN, new Object[]{no_absen, aUserId}, MULTIPLE_RS_EXTRACTOR_ABSEN);
    }
}
