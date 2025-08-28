import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;

public class Main {
    
    private static class Point {
        long x;
        BigInteger y;
        
        Point(long x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
    
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        
        try {
            // Load the JSON data
            byte[] fileBytes = Files.readAllBytes(Paths.get("input.json"));
            String jsonData = new String(fileBytes).replaceAll("\\s", "");
            
            // Parse threshold values
            int totalShares = extractNumber(jsonData, "\"n\":(\\d+)");
            int requiredShares = extractNumber(jsonData, "\"k\":(\\d+)");
            
            System.out.printf("Shares needed: %d out of %d%n", requiredShares, totalShares);
            
            // Extract coordinate data
            ArrayList<Point> coordinates = parseCoordinates(jsonData);
            
            System.out.println("\nParsed coordinates:");
            for (Point p : coordinates) {
                System.out.printf("x=%d, y=%s%n", p.x, p.y.toString());
            }
            
            // Take only the minimum required points
            List<Point> workingSet = coordinates.subList(0, requiredShares);
            
            // Reconstruct the polynomial's constant term
            BigInteger constantTerm = lagrangeInterpolation(workingSet, 0);
            System.out.printf("%nRecovered secret: %s%n", constantTerm);
            
        } catch (Exception ex) {
            System.err.println("Processing failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private static int extractNumber(String text, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }
    
    private static ArrayList<Point> parseCoordinates(String jsonText) {
        ArrayList<Point> points = new ArrayList<>();
        
        Pattern coordPattern = Pattern.compile("\"(\\d+)\":\\{\"base\":\"(\\d+)\",\"value\":\"([^\"]+)\"\\}");
        Matcher coordMatcher = coordPattern.matcher(jsonText);
        
        while (coordMatcher.find()) {
            long xCoord = Long.parseLong(coordMatcher.group(1));
            int numberBase = Integer.parseInt(coordMatcher.group(2));
            String encodedValue = coordMatcher.group(3);
            
            BigInteger yCoord = new BigInteger(encodedValue, numberBase);
            points.add(new Point(xCoord, yCoord));
        }
        
        return points;
    }
    
    private static BigInteger lagrangeInterpolation(List<Point> points, long targetX) {
        BigInteger result = BigInteger.ZERO;
        int numPoints = points.size();
        
        for (int i = 0; i < numPoints; i++) {
            Point currentPoint = points.get(i);
            BigInteger yValue = currentPoint.y;
            
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;
            
            for (int j = 0; j < numPoints; j++) {
                if (i != j) {
                    Point otherPoint = points.get(j);
                    numerator = numerator.multiply(BigInteger.valueOf(targetX - otherPoint.x));
                    denominator = denominator.multiply(BigInteger.valueOf(currentPoint.x - otherPoint.x));
                }
            }
            
            BigInteger term = yValue.multiply(numerator).divide(denominator);
            result = result.add(term);
        }
        
        return result;
    }
}