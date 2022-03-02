package query;

import index.InvertedIndex;
import index.Posting;

import java.util.ArrayList;

public final class BooleQueryParser {
    private BooleQueryParser() {  }

    public static ArrayList<Posting> parse(InvertedIndex index, String query) {
        if(index == null || query.isEmpty()) {
            return null;
        }

        ArrayList<Posting> result = new ArrayList<>();

        String[] words = query.split("\\W+");
        ArrayList<String> tokens = new ArrayList<>();
        ArrayList<Integer> binaryOperators = new ArrayList<>();

        boolean hasAnyOperator = false;

        for(int i = 0; i < words.length; i++) {
            String w = words[i];

            if(!isOperator(w)) {
                w = w.toLowerCase();
            } else {
                if(isBinaryOperator(w)) {
                    binaryOperators.add(i);
                }

                hasAnyOperator = true;
            }

            tokens.add(w);
        }

        if(hasAnyOperator) {
            result = evaluate(index, tokens, binaryOperators, 0);
        } else {
            result = evaluateText(index, tokens, 0, tokens.size());
        }

        return result;
    }

    private static ArrayList<Posting> evaluate(InvertedIndex index, ArrayList<String> tokens, ArrayList<Integer> binaryOperators, int start) {
        // Means only NOT operator (unary)
        if(binaryOperators.isEmpty()) {
            return evaluateText(index, tokens, 0, tokens.size());
        }

        int oi = binaryOperators.get(start);
        String ox = tokens.get(oi);

        ArrayList<Posting> left;
        ArrayList<Posting> right;

        if(start == 0) {
            left = evaluateText(index, tokens, 0, oi);
        } else {
            left = evaluateText(index, tokens, binaryOperators.get(start - 1) + 1, oi);
        }

        if(start + 1 < binaryOperators.size()) {
            right = evaluate(index, tokens, binaryOperators, start + 1);
        } else {
            right = evaluateText(index, tokens, oi + 1, tokens.size());
        }

        if(ox.equals("AND")) {
            return BooleQuery.and(left, right);
        } else {
            return BooleQuery.or(left, right);
        }
    }

    private static ArrayList<Posting> evaluateText(InvertedIndex index, ArrayList<String> tokens, int start, int end) {
        ArrayList<Posting> result = new ArrayList<>();

        boolean returnNot = false;

        for(int j = start; j < end; j++) {
            String t = tokens.get(j);

            if(j == start && isUnaryOperator(t)) {
                returnNot = true;
            } else {
                result = BooleQuery.or(result, index.getTermMapper().get(t));
            }
        }

        return returnNot ? BooleQuery.not(index, result) : result;
    }

    private static boolean isOperator(String token) {
        return token.equals("AND") || token.equals("OR") || token.equals("NOT");
    }

    private static boolean isBinaryOperator(String token) {
        return token.equals("AND") || token.equals("OR");
    }

    private static boolean isUnaryOperator(String token) {
        return token.equals("NOT");
    }
}
