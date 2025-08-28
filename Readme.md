output 1:
<br/>

<img src="./output1.png"/>

output 2:

<br/>
<img src="./output2.png"/>


Key Components:

Point class
Holds a share as (x, y) where y is a BigInteger.

Main logic

Reads input.json and extracts n (total shares) and k (required shares).

Parses all shares into Point objects.

Generates all k-combinations of shares.

Computes the secret from each combination using Lagrange interpolation.

Finds the most frequent secret → considered the true secret.

Identifies valid shares (part of any combination giving true secret) and outliers.

Verifies the secret using only valid shares.

Helpers

extractInt → extracts integers from JSON via regex.

parsePoints → parses (x, y) shares from JSON.

getCombinations → generates all k-sized combinations.

lagrange → performs Lagrange interpolation at x = 0 to recover the secret.
