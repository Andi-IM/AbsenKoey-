package live.andiirham.absenkoey.Dao;

import live.andiirham.absenkoey.Model.User;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class DaoImpl implements Dao{
    private JdbcTemplate mJdbc;

    public DaoImpl(DataSource aDataSource) {
        mJdbc = new JdbcTemplate(aDataSource);

    }

    @Override
    public List<User> get() {
        return null;
    }

    @Override
    public List<User> getByUserId(String aUserId) {
        return null;
    }

    @Override
    public int registerLineId(String aUserId, String aLineId, String aDisplayName) {
        return 0;
    }
}
