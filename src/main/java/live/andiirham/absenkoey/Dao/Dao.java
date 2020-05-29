package live.andiirham.absenkoey.Dao;

import live.andiirham.absenkoey.Model.User;

import java.util.List;

public interface Dao {
    public List<User> get();
    public List<User> getByUserId(String aUserId);
    public int registerLineId(String aUserId, String aLineId, String aDisplayName);

}
