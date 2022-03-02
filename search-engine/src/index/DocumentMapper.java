package index;

import java.io.Serializable;
import java.util.HashMap;

public class DocumentMapper implements Serializable {
    private HashMap<Integer, String> map = new HashMap<>();

    public DocumentMapper() { }

    public int insert(String path) {
        int id = map.size();
        map.put(id, path);
        return id;
    }

    public String get(int id) {
        return map.get(id);
    }

    public int getSize() {
        return map.size();
    }
}
