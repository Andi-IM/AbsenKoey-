package live.andiirham.absenkoey.Model;

public class DataSiswa {
    public long id;
    public String no_abs;
    public String nama;
    public String no_bp;
    public String user_id;

    public DataSiswa() { }
    public DataSiswa(long id, String no_abs, String nama, String no_bp, String user_id) {
        this.id = id;
        this.no_abs = no_abs;
        this.nama = nama;
        this.no_bp = no_bp;
        this.user_id = user_id;
    }

    public String getNo_abs() {
        return no_abs;
    }

    public String getNama() {
        return nama;
    }

    public String getNo_bp() {
        return no_bp;
    }
}
