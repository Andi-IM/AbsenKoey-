package live.andiirham.absenkoey.Model;

public class DaftarAbsen {
    public long id;
    public String no_absen;
    public String nama;
    public String no_bp;
    public String jam;


    public DaftarAbsen() {}

    public DaftarAbsen(long id, String no_absen, String nama, String no_bp, String jam) {
        this.id = id;
        this.no_absen = no_absen;
        this.nama = nama;
        this.no_bp = no_bp;
        this.jam = jam;
    }
}
