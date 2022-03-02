package index;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class InvertedIndex implements Serializable {
    private DocumentMapper documentMapper = new DocumentMapper();
    private TermMapper termMapper = new TermMapper();

    public InvertedIndex() { }

    public void build(String path) {
        File directory = new File(path);
        indexDirectory(directory);
    }

    public DocumentMapper getDocumentMapper() {
        return documentMapper;
    }

    public TermMapper getTermMapper() {
        return termMapper;
    }

    private void indexDirectory(File directory) {
        File[] files = directory.listFiles();

        for(File f : files) {
            if(f.isDirectory()) {
                indexDirectory(f);
            } else {
                if(!f.getName().endsWith(".txt")) {
                    continue;
                }

                try {
                    ArrayList<String> tokens = Tokenizer.tokenizeFile(f.getCanonicalPath());
                    HashMap<String, Long> termFrequency = new HashMap<>();

                    for(String t : tokens) {
                        if(termFrequency.containsKey(t)) {
                            long frequency = termFrequency.get(t) + 1;
                            termFrequency.put(t, frequency);
                        } else {
                            termFrequency.put(t, 1L);
                        }
                    }

                    int documentID = documentMapper.insert(f.getCanonicalPath());
                    termFrequency.forEach((key, value) -> termMapper.insert(key, documentID, value));
                } catch(IOException ex) {
                    System.err.println("Failed to get canonical path!");
                    ex.printStackTrace();
                }
            }
        }
    }

    public void save(InvertedIndex index, String path) {
        try {
            FileOutputStream file = new FileOutputStream(path);
            GZIPOutputStream gzip = new GZIPOutputStream(file);
            ObjectOutputStream object = new ObjectOutputStream(gzip);

            object.writeObject(index);

            object.close();
            gzip.close();
            file.close();
        } catch(IOException ex) {
            System.err.println("Failed to save inverted index!");
            ex.printStackTrace();
        }
    }

    public void load(String path) {
        try {
            FileInputStream file = new FileInputStream(path);
            GZIPInputStream gzip = new GZIPInputStream(file);
            ObjectInputStream object = new ObjectInputStream(gzip);

            InvertedIndex other = (InvertedIndex) object.readObject();
            this.documentMapper = other.documentMapper;
            this.termMapper = other.termMapper;

            object.close();
            gzip.close();
            file.close();
        } catch(Exception ex) {
            System.err.println("Failed to load inverted index!");
            ex.printStackTrace();
        }
    }
}
