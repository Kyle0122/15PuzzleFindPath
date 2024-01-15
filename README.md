# 15PuzzleShortPath

This program uses A* search and Manhattan distance as Heuristic Function. It should return the shortest path with any 15 puzzle under 67 steps(under 20mins).
The code is originally write in java, later translated to kotlin. The kotlin version runs about 15% faster.

# Compile and Run:

kotlinc main.kt FifteenPuzzle.kt -include-runtime -d mainkt.jar

java -jar -Xms8g mainkt.jar

--java verison

javac FifteenPuzzle.java main.java

java -XX:+UnlockExperimentalVMOptions -Xms8g -XX:+AlwaysPreTouch main

or

java -XX:+UseParallelGC -Xmx16g main

Use more ram if your puzzle is too hard to solve.

# About finding shorter path

Changing the comparator will affect the solving time and the path's length. The higher the weight of the depth value, the longer the solving time and the shorter the path length obtained. But if you rise the ratio of depth to Manhattan greater than one, the ram useage will be really large.
