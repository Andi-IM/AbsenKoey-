package live.andiirham.absenkoey.Model;

public class User
{
    public Long id;
    public String user_id;
    public String line_id;
    public String display_name;

    public User() {
    }

    public User(Long id, String user_id, String line_id, String display_name) {
        this.id = id;
        this.user_id = user_id;
        this.line_id = line_id;
        this.display_name = display_name;
    }
}
