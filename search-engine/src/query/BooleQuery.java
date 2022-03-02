package query;

import index.InvertedIndex;
import index.Posting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public final class BooleQuery {
    private BooleQuery() { }

    public static ArrayList<Posting> and(InvertedIndex index, String ...termsArgs) {
        ArrayList<String> terms = new ArrayList<>();

        for(String ta : termsArgs) {
            terms.add(ta);
        }

        if(terms.size() < 2) {
            return null;
        }

        ArrayList<Posting> result = index.getTermMapper().get(terms.get(0));

        for(int i = 1; i < terms.size(); i++) {
            result = BooleQuery.and(result, index.getTermMapper().get(terms.get(i)));
        }

        return result;
    }

    public static ArrayList<Posting> and(ArrayList<Posting> a, ArrayList<Posting> b) {
        ArrayList<Posting> result = new ArrayList<>();

        int ai = 0;
        int bi = 0;

        while(ai < a.size() && bi < b.size()) {
            int ax = a.get(ai).getDocumentID();
            int bx = b.get(bi).getDocumentID();

            if(ax == bx) {
                result.add(new Posting(ax, 0L));
                ai++;
                bi++;
            } else if(ax > bx) {
                bi++;
            } else {
                ai++;
            }
        }

        return result;
    }

    public static ArrayList<Posting> or(InvertedIndex index, String ...termsArgs) {
        ArrayList<String> terms = new ArrayList<>();

        for(String ta : termsArgs) {
            terms.add(ta);
        }

        if(terms.size() < 2) {
            return null;
        }

        ArrayList<Posting> result = index.getTermMapper().get(terms.get(0));

        for(int i = 1; i < terms.size(); i++) {
            result = BooleQuery.or(result, index.getTermMapper().get(terms.get(i)));
        }

        return result;
    }

    public static ArrayList<Posting> or(ArrayList<Posting> a, ArrayList<Posting> b) {
        ArrayList<Posting> result = new ArrayList<>();

        int ai = 0;
        int bi = 0;

        while(ai < a.size() && bi < b.size()) {
            int ax = a.get(ai).getDocumentID();
            int bx = b.get(bi).getDocumentID();

            if(ax == bx) {
                result.add(new Posting(ax, 0L));
                ai++;
                bi++;
            } else if(ax > bx) {
                result.add(new Posting(bx, 0L));
                bi++;
            } else {
                result.add(new Posting(ax, 0L));
                ai++;
            }
        }

        while(ai < a.size()) {
            result.add(new Posting(a.get(ai).getDocumentID(), 0L));
            ai++;
        }

        while(bi < b.size()) {
            result.add(new Posting(b.get(bi).getDocumentID(), 0L));
            bi++;
        }

        return result;
    }

    public static ArrayList<Posting> not(InvertedIndex index, String term) {
        ArrayList<Posting> postings = index.getTermMapper().get(term);
        return not(index, postings);
    }

    public static ArrayList<Posting> not(InvertedIndex index, ArrayList<Posting> postings) {
        ArrayList<Posting> result = new ArrayList<>();

        int i = 0;

        for(Posting p : postings) {
            int id = p.getDocumentID();

            while(i < id) {
                result.add(new Posting(id, 0L));
                i++;
            }

            i++;
        }

        for(; i < index.getDocumentMapper().getSize(); i++) {
            result.add(new Posting(i, 0L));
        }

        return result;
    }
}
