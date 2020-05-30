package live.andiirham.absenkoey.Dao;

import live.andiirham.absenkoey.Model.DataSiswa;
import live.andiirham.absenkoey.Model.User;

import java.util.List;

public interface Dao {
    public List<User> get();
    public List<User> getByUserId(String aUserId);
    public int registerLineId(String no_absen, String nama, String aDisplayName);
    public int joinAbsen(String no_absen, String nama, String no_bp);
    public List<DataSiswa> getAbsen();
    public List<DataSiswa> getByNoAbsen(String no_absen);
    public List<DataSiswa> getByJoin(String no_absen, String aUserId);

}
