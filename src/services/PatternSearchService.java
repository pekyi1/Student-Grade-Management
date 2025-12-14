package services;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import models.Student;

public class PatternSearchService {

    public static class SearchResult {
        private final Student student;
        private final String highlightedText;

        public SearchResult(Student student, String highlightedText) {
            this.student = student;
            this.highlightedText = highlightedText;
        }

        public Student getStudent() {
            return student;
        }

        public String getHighlightedText() {
            return highlightedText;
        }
    }

    public static class searchStats {
        public int totalScanned;
        public int matchesFound;
        public long searchTimeMs;
        public String regexComplexityHint;

        public searchStats(int total, int matches, long time, String hint) {
            this.totalScanned = total;
            this.matchesFound = matches;
            this.searchTimeMs = time;
            this.regexComplexityHint = hint;
        }
    }

    public static class SearchResponse {
        public List<SearchResult> results;
        public searchStats stats;

        public SearchResponse(List<SearchResult> results, searchStats stats) {
            this.results = results;
            this.stats = stats;
        }
    }

    // ANSI Escape Codes for Highlighting (Yellow Background, Black Text)
    // Note: Console support varies. Standard terminals support this.
    private static final String HIGHLIGHT_START = "\u001B[43m\u001B[30m";
    private static final String HIGHLIGHT_END = "\u001B[0m";

    public SearchResponse searchByPattern(List<Student> students, String regex,
            Function<Student, String> fieldExtractor) throws PatternSyntaxException {
        long startTime = System.currentTimeMillis();
        List<SearchResult> results = new ArrayList<>();
        int scanned = 0;

        Pattern pattern = Pattern.compile(regex); // Case sensitivity can be handled in regex string (?i)

        for (Student s : students) {
            scanned++;
            String fieldValue = fieldExtractor.apply(s);
            if (fieldValue == null)
                continue;

            Matcher matcher = pattern.matcher(fieldValue);
            if (matcher.find()) {
                // We perform a "find" to check match
                // To highlight, we need to replace ALL occurrences or just the first?
                // Usually search highlighting highlights all matches in the string.

                StringBuffer sb = new StringBuffer();
                matcher.reset(); // Reset to start
                while (matcher.find()) {
                    matcher.appendReplacement(sb, HIGHLIGHT_START + matcher.group() + HIGHLIGHT_END);
                }
                matcher.appendTail(sb);

                results.add(new SearchResult(s, sb.toString()));
            }
        }

        long endTime = System.currentTimeMillis();
        String complexity = estimateComplexity(regex);

        return new SearchResponse(results, new searchStats(scanned, results.size(), endTime - startTime, complexity));
    }

    private String estimateComplexity(String regex) {
        if (regex.contains(".*") || regex.contains(".+")) {
            return "O(N) - Linear scan, but '.*' wildcards can be slow on long strings.";
        }
        return "O(N) - Linear scan.";
    }
}
