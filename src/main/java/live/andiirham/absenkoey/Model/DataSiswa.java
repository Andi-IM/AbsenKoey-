package live.andiirham.absenkoey.Model;

public class DataSiswa {
    public long id;
    public int no_abs;
    public String nama;
    public String no_bp;
    public String jam;

    public DataSiswa() { }

    public DataSiswa(long id, int no_abs, String nama, String no_bp, String jam) {
        this.id = id;
        this.no_abs = no_abs;
        this.nama = nama;
        this.no_bp = no_bp;
        this.jam = jam;
    }
}
