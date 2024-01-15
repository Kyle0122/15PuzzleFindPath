# 15PuzzleFindPath

This program uses A* search and Manhattan distance as a Heuristic Function. It should return the shortest path with 15 puzzles under 67 steps(under 20mins).
The code was originally written in java and later translated to Kotlin. The Kotlin version runs about 15% faster.

# Compile and Run:

```console
$ kotlinc main.kt FifteenPuzzle.kt -include-runtime -d mainkt.jar

$ java -jar -Xms8g mainkt.jar
```

java version: 

```console
$ javac FifteenPuzzle.java main.java

$ java -XX:+UnlockExperimentalVMOptions -Xms8g -XX:+AlwaysPreTouch main

or

$ java -XX:+UseParallelGC -Xmx16g main
```

You can use more RAM if your puzzle is too hard to solve.

# About finding the shortest path

Changing the comparator will affect the solving time and the path's length. The higher the weight of the depth value, the longer the solving time and the shorter the path length obtained. But if you rise the ratio of depth to Manhattan greater than one, the ram usage will be large.
