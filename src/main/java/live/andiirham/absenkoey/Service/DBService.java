package live.andiirham.absenkoey.Service;

import live.andiirham.absenkoey.Dao.Dao;
import live.andiirham.absenkoey.Model.DaftarAbsensi;
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
    public int insertAbsen(String no_absen, String nama, String noBP){
        if(isUserRegistered(no_absen) == null) {
            return mDao.daftarAbsen(no_absen,nama, noBP);
        }
        return -1;
    }

    // method untuk cek apakah sudah ada dalam database
    private DataSiswa isUserRegistered(String no_absen){
        List<DataSiswa> result = mDao.getByNoAbsen(no_absen);

        if (result.size() > 0) {
            return result.get(0);
        }

        return null;
    }

    // method untuk melihat daftar absen
    public List<DataSiswa> getAbsen(String no_absen)
    {
        return mDao.getByNoAbsen(no_absen);
    }

    // method untuk mengambil absen
    public int ambilAbsen(String user_id, String no_absen, String nama, String no_bp, String tanggal){
        DaftarAbsensi daftarAbsen =isAbsent(tanggal, user_id);

        if(daftarAbsen == null){
            return mDao.ambilAbsen(user_id, no_absen, nama, no_bp);
        }
        return -1;
    }

    // cek apakah udah ngambil absen di tanggal ini
    private DaftarAbsensi isAbsent(String tanggal, String userId){
        List<DaftarAbsensi> result = mDao.getByTanggal(userId, tanggal);

        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    // method untuk menampilkan absen
    private List<DaftarAbsensi> getAbsensi(){
        return mDao.getAbsensi();
    }
}
