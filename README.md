# 15PuzzleShortPath
# Run: (openjdk14)
java -XX:+UnlockExperimentalVMOptions -Xms8g -XX:+AlwaysPreTouch FifteenPuzzle.java
Use more ram if your starting puzzle is too hard to solve.
# About finding shorter path
Changing the comparator will affect the solving time and the path's length. The higher the weight of the depth value, the longer the solving time and the shorter the path length obtained. But if you rise the ratio of deep to hamming greater than one, the demand ram use will be really huge.