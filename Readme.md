output 1:
<br/>

<img src="./output1.png"/>

output 2:

<br/>
<img src="./output2.png"/>


The program reads a JSON containing Shamir’s Secret Sharing points (x, y) and the parameters n and k. It:

Parses the points from JSON.

Generates all combinations of k points.

Uses Lagrange interpolation at x=0 to compute the secret for each combination.

Finds the most frequent secret → the true secret.

Classifies points as valid (lie on the polynomial) or outliers (don’t).

Verifies the secret using only valid points.
