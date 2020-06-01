package live.andiirham.absenkoey.Dao;

import live.andiirham.absenkoey.Model.DaftarAbsensi;
import live.andiirham.absenkoey.Model.DataSiswa;
import live.andiirham.absenkoey.Model.User;

import java.util.List;

public interface Dao {
    public List<User> get();
    public List<User> getByUserId(String aUserId);
    public int registerLineId(String no_absen, String nama, String aDisplayName);
    public int daftarAbsen(String no_absen, String nama, String no_bp, String userId);
    public int ambilAbsen(String userid, String no_absen, String nama, String no_bp);
    public List<DataSiswa> getAbsen();
    public List<DataSiswa> getAbsenByUserId(String aUserId);
    public List<DataSiswa> getByNoAbsen(String no_absen);
    public List<DaftarAbsensi> getAbsensi();
    public List<DaftarAbsensi> getByJoinedAbsen(String no_absen, String aUserId);
    public List<DaftarAbsensi> getByTanggal(String tanggal, String userId);
}
