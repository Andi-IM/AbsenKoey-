package live.andiirham.absenkoey.Model;

public class DaftarAbsensi {
    public long id;
    public String user_id;
    public String no_absen;
    public String nama;
    public String no_bp;
    public String tanggal;
    public String jam;

    public DaftarAbsensi() {}
    public DaftarAbsensi(long id, String user_id, String no_absen, String nama, String no_bp, String tanggal, String jam) {
        this.id = id;
        this.user_id = user_id;
        this.no_absen = no_absen;
        this.nama = nama;
        this.no_bp = no_bp;
        this.tanggal = tanggal;
        this.jam = jam;
    }
}
