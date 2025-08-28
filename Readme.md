output 1:
<br/>

<img src="./output1.png"/>

output 2:

<br/>
<img src="./output2.png"/>


Main Steps

Read JSON & extract parameters

int totalShares = extractInt(json, "\"n\":(\\d+)");
int requiredShares = extractInt(json, "\"k\":(\\d+)");


Reads n (total shares) and k (minimum shares needed to recover secret).

Parse points

List<Point> points = parsePoints(json);


Extracts (x, y) coordinates from JSON, converting y from its base to BigInteger.

Generate all k-point combinations

List<List<Point>> combos = getCombinations(points, requiredShares);


All possible sets of k points are prepared for interpolation.

Compute secrets and find the true one

BigInteger secret = lagrange(combo, 0);


Applies Lagrange interpolation at x=0 to find the secret for each combination.

Keeps track of which secret appears most frequently → the true secret.

Identify valid and outlier points

Points in combinations that produce the true secret are valid.

Points that never appear in a valid combination are outliers.

Verification

Reconstructs the secret using only valid points to ensure correctness.

Key Methods

lagrange(List<Point> pts, long targetX)

Implements Lagrange interpolation formula to evaluate the polynomial at targetX.

getCombinations(...)

Generates all possible combinations of points of size k.

parsePoints(String json)

Reads (x, y) from JSON, converts y to BigInteger.

extractInt(String text, String regex)

Extracts an integer from JSON using a regex.





