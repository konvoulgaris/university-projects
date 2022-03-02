package index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public final class Tokenizer {
    private Tokenizer() { }

    public static ArrayList<String> tokenize(String s) {
        ArrayList<String> result = new ArrayList<>();

        String[] tokens = s.split("\\W+");

        for(String t : tokens) {
            result.add(t.toLowerCase());
        }

        return result;
    }

    public static ArrayList<String> tokenizeFile(String path) {
        ArrayList<String> result = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = "";

            while((line = reader.readLine()) != null) {
                result.addAll(tokenize(line));
            }
        } catch(IOException ex) {
            System.err.println("Failed to tokenize file!");
            ex.printStackTrace();
        }

        return result;
    }
}
