package live.andiirham.absenkoey.Dao;

import live.andiirham.absenkoey.Model.DaftarAbsensi;
import live.andiirham.absenkoey.Model.DataSiswa;
import live.andiirham.absenkoey.Model.User;
import org.springframework.jdbc.core.JdbcTemplate;


import javax.sql.DataSource;
import java.util.List;

import static live.andiirham.absenkoey.Dao.RsExtractor.*;

public class DaoImpl implements Dao
{

    // query untuk table user
    private final static String USER_TABLE="tbl_user";
    private final static String SQL_SELECT_ALL="SELECT id, user_id, line_id, display_name FROM "+USER_TABLE;
    private final static String SQL_GET_BY_USER_ID=SQL_SELECT_ALL + " WHERE LOWER(user_id) LIKE LOWER(?);";
    private final static String SQL_REGISTER="INSERT INTO "+USER_TABLE+" (user_id, line_id, display_name) VALUES (?, ?, ?);";
    
    // query untuk table absen
    private final static String ABSEN_TABLE="tbl_absen";
    private final static String SQL_SELECT_ALL_ABSEN="SELECT id, no_absen, nama, no_bp FROM "+ABSEN_TABLE;
    private final static String SQL_INSERT_ABSEN="INSERT INTO "+ABSEN_TABLE+"(no_absen, nama, no_bp, user_id) VALUES (?,?,?,?);";
    private final static String SQL_GET_BY_ABSEN = SQL_SELECT_ALL_ABSEN + "WHERE no_absen LIKE (?);";
    private final static String SQL_ABSEN_GET_BY_USER_ID = SQL_SELECT_ALL_ABSEN + "WHERE user_id LIKE (?);";

    // query untuk table absensi
    private final static String ABSENSI_TABLE = "tbl_absensi";
    private final static String SQL_SELECT_ALL_ABSENSI = "SELECT id, user_id, no_absen, nama, no_bp, tanggal, jam FROM "+ABSENSI_TABLE;
    private final static String SQL_JOIN_ABSEN = "INSERT INTO "+ABSENSI_TABLE+" (user_id, no_absen, nama, no_bp) VALUES (?,?,?,?);";
    private final static String SQL_GET_BY_DATE = SQL_SELECT_ALL_ABSENSI + "WHERE tanggal = (?) AND user_id = (?);";
    private final static String SQL_GET_BY_JOINED_ABSEN = SQL_SELECT_ALL_ABSENSI + "WHERE user_id = ? AND no_absen = ?;";

    private JdbcTemplate mJdbc;

    // constructor
    public DaoImpl(DataSource aDataSource)
    {
        mJdbc = new JdbcTemplate(aDataSource);
    }

    // getting query
    @Override
    public List<User> get()
    {
        return mJdbc.query(SQL_SELECT_ALL, MULTIPLE_RS_EXTRACTOR);
    }

    @Override
    public List<User> getByUserId(String aUserId) {
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
    public int daftarAbsen(String no_absen, String nama, String no_bp, String userId) {
        return mJdbc.update(SQL_INSERT_ABSEN, new Object[]{no_absen, nama, no_bp, userId});
    }

    @Override
    public int ambilAbsen(String userid, String no_absen, String nama, String no_bp) {
        return mJdbc.update(SQL_JOIN_ABSEN, new Object[]{userid, no_absen, nama, no_bp});
    }

    @Override
    public List<DataSiswa> getAbsen()
    {
        return mJdbc.query(SQL_SELECT_ALL_ABSEN, MULTIPLE_RS_EXTRACTOR_ABSEN);
    }

    @Override
    public List<DataSiswa> getAbsenByUserId(String aUserId) {
        return mJdbc.query(SQL_ABSEN_GET_BY_USER_ID,new Object[]{"%"+aUserId+"%"}, MULTIPLE_RS_EXTRACTOR_ABSEN);
    }

    @Override
    public List<DataSiswa> getByNoAbsen(String no_absen) {
        return mJdbc.query(SQL_GET_BY_ABSEN, new Object[]{"%"+no_absen+"%"}, MULTIPLE_RS_EXTRACTOR_ABSEN);
    }

    @Override
    public List<DaftarAbsensi> getByJoinedAbsen(String no_absen, String aUserId) {
        return mJdbc.query(SQL_GET_BY_JOINED_ABSEN, new Object[]{aUserId, no_absen}, MULTIPLE_RS_EXTRACTOR_ABSENSI);
    }

    @Override
    public List<DaftarAbsensi> getAbsensi() {return mJdbc.query(SQL_SELECT_ALL_ABSENSI, MULTIPLE_RS_EXTRACTOR_ABSENSI);}

    @Override
    public List<DaftarAbsensi> getByTanggal(String tanggal, String userId) {
        return mJdbc.query(SQL_GET_BY_DATE, new Object[]{tanggal, userId}, MULTIPLE_RS_EXTRACTOR_ABSENSI);
    }
}
