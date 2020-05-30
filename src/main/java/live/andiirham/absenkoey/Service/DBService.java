package live.andiirham.absenkoey.Service;

import live.andiirham.absenkoey.Dao.Dao;
import live.andiirham.absenkoey.Model.DataSiswa;
import live.andiirham.absenkoey.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DBService {
    @Autowired
    private Dao mDao;

    // method mendaftarkan LINE ID
    public int regLineID(String aUserId, String aLineId, String aDisplayName)
    {
        if(findUser(aUserId) == null)
        {
            return mDao.registerLineId(aUserId, aLineId, aDisplayName);
        }

        return -1;
    }

    //method untuk mencari user terdaftar di database
    public String findUser(String aUserId){
        List<User> self=mDao.getByUserId("%"+aUserId+"%");

        if(self.size() > 0)
        {
            return self.get(0).line_id;
        }

        return null;
    }

    //method untuk mendaftarkan absen
    public int insertAbsen(String no_absen, String nama, String noBP, String userId){
        DataSiswa absenTerdaftar = isUserJoinedAbsen(no_absen, userId);

        if(absenTerdaftar == null) {
            return mDao.joinAbsen(no_absen,nama, noBP);
        }

        return -1;
    }

    // method untuk cek apakah sudah join event
    private DataSiswa isUserJoinedAbsen(String eventID, String userID){
        List<DataSiswa> result = mDao.getByJoin(eventID, userID);

        if (result.size() > 0) {
            return result.get(0);
        }

        return null;
    }

    // method untuk melihat daftar absen
    public List<DataSiswa> getJoinedAbsen(String no_absen)
    {
        return mDao.getByNoAbsen(no_absen);
    }
}
