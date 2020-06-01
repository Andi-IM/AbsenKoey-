package live.andiirham.absenkoey.Model;

public class DataSiswa {
    public long id;
    public String no_abs;
    public String nama;
    public String no_bp;

    public DataSiswa() { }
    public DataSiswa(long id, String no_abs, String nama, String no_bp) {
        this.id = id;
        this.no_abs = no_abs;
        this.nama = nama;
        this.no_bp = no_bp;
    }
}
