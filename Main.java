import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;

public class Main {

    // Simple data holder for (x,y)
    private static class Point {
        long x;
        BigInteger y;

        Point(long x, BigInteger y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    public static void main(String[] args) {
        try {
            // Read file content
            String json = Files.readString(Paths.get("test.json")).replaceAll("\\s", "");

            int totalShares = extractInt(json, "\"n\":(\\d+)");
            int requiredShares = extractInt(json, "\"k\":(\\d+)");
            System.out.printf("Shares needed: %d of %d%n", requiredShares, totalShares);

            // Parse all coordinates
            List<Point> points = parsePoints(json);
            System.out.println("\nParsed points:");
            points.forEach(System.out::println);

            // Compute secret for every k-combination
            List<List<Point>> combos = getCombinations(points, requiredShares);
            Map<BigInteger, Integer> freq = new HashMap<>();

            for (List<Point> combo : combos) {
                BigInteger secret = lagrange(combo, 0);
                freq.put(secret, freq.getOrDefault(secret, 0) + 1);
            }

            // Find most frequent secret
            BigInteger trueSecret = freq.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey).orElse(null);

            System.out.println("\nMost frequent secret: " + trueSecret);

            // Identify valid vs outlier points
            Set<Point> valid = new HashSet<>();
            Set<Point> outliers = new HashSet<>(points);

            for (List<Point> combo : combos) {
                if (lagrange(combo, 0).equals(trueSecret)) {
                    valid.addAll(combo);
                }
            }
            outliers.removeAll(valid);

            System.out.println("\nValid points:");
            valid.forEach(p -> System.out.println("  " + p));

            System.out.println("\nOutlier points:");
            outliers.forEach(p -> System.out.println("  " + p));

            // Verification with valid points
            if (valid.size() >= requiredShares) {
                List<Point> minimal = new ArrayList<>(valid).subList(0, requiredShares);
                BigInteger verify = lagrange(minimal, 0);
                System.out.println("\nVerification using only valid points: " + verify);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Helpers ---
    private static int extractInt(String text, String regex) {
        Matcher m = Pattern.compile(regex).matcher(text);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    private static List<Point> parsePoints(String json) {
        List<Point> points = new ArrayList<>();
        Matcher m = Pattern.compile("\"(\\d+)\":\\{\"base\":\"(\\d+)\",\"value\":\"([^\"]+)\"\\}").matcher(json);
        while (m.find()) {
            long x = Long.parseLong(m.group(1));
            int base = Integer.parseInt(m.group(2));
            BigInteger y = new BigInteger(m.group(3), base);
            points.add(new Point(x, y));
        }
        return points;
    }

    private static List<List<Point>> getCombinations(List<Point> points, int k) {
        List<List<Point>> res = new ArrayList<>();
        buildCombos(points, k, 0, new ArrayList<>(), res);
        return res;
    }

    private static void buildCombos(List<Point> points, int k, int start,
                                    List<Point> curr, List<List<Point>> res) {
        if (curr.size() == k) {
            res.add(new ArrayList<>(curr));
            return;
        }
        for (int i = start; i < points.size(); i++) {
            curr.add(points.get(i));
            buildCombos(points, k, i + 1, curr, res);
            curr.remove(curr.size() - 1);
        }
    }

    private static BigInteger lagrange(List<Point> pts, long targetX) {
        BigInteger sum = BigInteger.ZERO;
        for (int i = 0; i < pts.size(); i++) {
            Point pi = pts.get(i);
            BigInteger num = BigInteger.ONE, den = BigInteger.ONE;
            for (int j = 0; j < pts.size(); j++) {
                if (i != j) {
                    Point pj = pts.get(j);
                    num = num.multiply(BigInteger.valueOf(targetX - pj.x));
                    den = den.multiply(BigInteger.valueOf(pi.x - pj.x));
                }
            }
            sum = sum.add(pi.y.multiply(num).divide(den));
        }
        return sum;
    }
}
