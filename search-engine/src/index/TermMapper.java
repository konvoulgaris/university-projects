package index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class TermMapper implements Serializable {
    private HashMap<String, ArrayList<Posting>> map = new HashMap<>();

    public TermMapper() { }

    public void insert(String term, int documentID, long frequency) {
        if(map.containsKey(term)) {
            ArrayList<Posting> postings = map.get(term);
            postings.add(new Posting(documentID, frequency));
        } else {
            ArrayList<Posting> postings = new ArrayList<>();
            postings.add(new Posting(documentID, frequency));
            map.put(term, postings);
        }
    }

    public ArrayList<Posting> get(String term) {
        return map.get(term);
    }

    public int getSize() {
        return map.size();
    }
}
