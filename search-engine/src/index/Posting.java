package index;

import java.io.Serializable;

public class Posting implements Serializable {
    private int documentID;
    private long frequency;

    public Posting(int documentID, long frequency) {
        this.documentID = documentID;
        this.frequency = frequency;
    }

    public int getDocumentID() {
        return documentID;
    }

    public long getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return "Posting: <" + "documentID = " + documentID + ", frequency = " + frequency +  ">";
    }
}
