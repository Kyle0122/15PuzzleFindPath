import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class main {

    public static boolean searchClosest(PriorityQueue<FifteenPuzzle> openSet, HashMap<Long, FifteenPuzzle> closeSet,
            int maxdepth, FifteenPuzzle[] target, HashMap<Long, FifteenPuzzle> targetSet,
            FifteenPuzzle[] ResultArray) {
        var current = openSet.poll();
        var children = current.getChildren();

        //check if any child is in the target open set:
        for(FifteenPuzzle puzzle : children) {
            if(targetSet.containsKey(puzzle.board)) {
                //puzzle.printSolutionRecursive();
                FifteenPuzzle target_puzz = targetSet.get(puzzle.board);
                //target_puzz.parent.printSolutionForward();
                ResultArray[0] = puzzle;
                ResultArray[1] = target_puzz;
                System.out.println("Find Soultion in search depth:" + puzzle.depth +
                    " Total:"+ (puzzle.depth + target_puzz.depth) + " steps");
                return true;
            }
        }

        //if the current node is not in the close set or it has a lower depth value, add it to the close set.
        if(!closeSet.containsKey(current.board)) {
            closeSet.put(current.board, current);
        } else {
            FifteenPuzzle old = closeSet.get(current.board);
            if(current.depth < old.depth) {
                closeSet.put(current.board, current);
            } else {
                return false;
            }
        }

        int maxManhattanValue = maxdepth;
        maxManhattanValue = maxdepth - current.depth - target[0].depth;
        var childrenNumber = 0;
        for(int i = 0; i < children.length; i++) {
            for(FifteenPuzzle puzz : target) {
                byte a = children[i].getManhattan(puzz);
                if(a < children[i].manhattan) {
                    children[i].manhattan = a;
                }
            }
            if(children[i].manhattan < maxManhattanValue) {
                openSet.add(children[i]);
                childrenNumber++;
            }
        }

        //Dead end detection:
        //After testing, there is no dead end.
        //if(childrenNumber == 0) {
        //    closeSet.remove(current.board);
        //    System.out.printf("Dead end!");
        //}
        return false;
    }

    static Comparator<FifteenPuzzle> comp = new Comparator<FifteenPuzzle>() {
        public int compare(FifteenPuzzle a, FifteenPuzzle b){
            return (a.depth*1 + a.manhattan*1) - (b.depth*1 + b.manhattan*1);
            //return (a.depth*9 + a.manhattan*9 + a.getParity()) - (b.depth*9 + b.manhattan*9 + b.getParity());
            //return (a.depth*9 + a.manhattan*10) - (b.depth*9 + b.manhattan*10);
            //return (a.depth*60 + a.manhattan*a.manhattan) - (b.depth*60 + b.manhattan*b.manhattan);
        }
    };

    public static FifteenPuzzle[] buildArray(FifteenPuzzle start, FifteenPuzzle end,
        PriorityQueue<FifteenPuzzle> openSet, HashMap<Long, FifteenPuzzle> closeSet, int depth, int sampleDeep){
        var sample = new FifteenPuzzle[1];
        sample[0] = end;
        PriorityQueue<FifteenPuzzle> currentLevel = new PriorityQueue<FifteenPuzzle>(comp);
        currentLevel.add(start);
        if(depth <= 0){depth = 1;}

        int level = 1;
        while(level <= depth) {
            PriorityQueue<FifteenPuzzle> nextLevel = new PriorityQueue<FifteenPuzzle>(comp);
            for(FifteenPuzzle puzz : currentLevel){
                FifteenPuzzle[] children = puzz.getChildren();
                for(int i = 0; i < children.length; i++){
                    children[i].manhattan = children[i].getManhattan(end);
                    if(!closeSet.containsKey(children[i].board)){
                        nextLevel.add(children[i]);
                    }
                }
                closeSet.put(puzz.board, puzz);
            }
            currentLevel = nextLevel;
            if(level == sampleDeep){
                sample = currentLevel.toArray(sample);
            }
            level++;
        }
        openSet.addAll(currentLevel);
        return sample;
    }

    public static void main(String[] args) {
        //int[][] startBoard = {{1, 2, 3, 4},{5, 6, 0, 8},{9, 10, 7, 11},{13, 14, 15, 12}}; //3 steps
        //int[][] startBoard = {{1, 0, 2, 4},{5, 6, 3, 8},{9, 10, 7, 11},{13, 14, 15, 12}}; //5 steps
        //int[][] startBoard = {{1, 10, 2, 4},{5, 11, 3, 7},{9, 0, 6, 8},{13, 14, 15, 12}}; //11 steps
        //int[][] startBoard = {{10, 6, 12, 11},{8, 7, 0, 4},{5, 2, 3, 1},{9, 13, 14, 15}}; //43 steps
        //int[][] startBoard = {{14, 2, 15, 5},{7, 3, 0, 4},{1, 6, 10, 13},{12, 8, 9, 11}}; //49 steps
        //int[][] startBoard = {{7, 9, 11, 10},{4, 12, 14, 6},{2, 8, 13, 15},{0, 3, 5, 1}}; //59 steps
        //int[][] startBoard = {{11, 15, 8, 12},{14, 10, 13, 9},{2, 7, 4, 5},{3, 6, 1, 0}}; //64 steps
        int[][] startBoard = {{11, 9, 0, 12},{14, 15, 10, 8},{2, 6, 13, 5},{3, 7, 4, 1}}; //66 steps
        //int[][] startBoard = {{11, 15, 8, 12},{2, 14, 9, 13},{3, 10, 6, 5},{0, 7, 4, 1}}; //67 steps
        //int[][] startBoard = {{11, 15, 9, 12},{14, 10, 8, 13},{6, 2, 5, 0},{3, 7, 4, 1}}; //69 steps

        //int[][] startBoard = {{14, 13, 15, 7},{11, 12, 9, 5},{6, 0, 2, 1},{4, 8, 10, 3}}; //57 steps
        //int[][] startBoard = {{14, 10, 2, 1},{13, 9, 8, 0},{7, 3, 6, 11},{15, 5, 4, 12}}; //60 steps
        //int[][] startBoard = {{14, 10, 2, 1},{13, 9, 8, 11},{7, 3, 6, 12},{15, 5, 4, 0}}; //62 steps

        var start = new FifteenPuzzle(startBoard);
        start.depth = 0;
        int[][] endBoard = {{1, 2, 3, 4},{5, 6, 7, 8},{9, 10, 11, 12},{13, 14, 15, 0}};
        //int[][] endBoard = {{0, 1, 2, 3},{4, 5, 6, 7},{8, 9, 10, 11},{12, 13, 14, 15}};
        var end = new FifteenPuzzle(endBoard);
        end.depth = 0;

        start.manhattan = start.getManhattan(end);
        end.manhattan = end.getManhattan(start);
        System.out.printf("start: \n" + start);
        System.out.println("start parity: " + start.getParity());
        System.out.println("start ham: " + start.manhattan);
        long sysDate = System.currentTimeMillis();

        //init open set, close set & start Array
        var openSet = new PriorityQueue<FifteenPuzzle>(comp);
        var closeSet = new HashMap<Long, FifteenPuzzle>();
        buildArray(start, end, openSet, closeSet, start.manhattan/4, 0);
        System.out.println("Build Array level " + openSet.peek().depth + ", size:" + openSet.size());

        //init target Set & target Array
        var endOpenSet = new PriorityQueue<FifteenPuzzle>(comp);
        var endCloseSet = new HashMap<Long, FifteenPuzzle>();
        var endArray = buildArray(end, start, endOpenSet, endCloseSet, start.manhattan/4, Math.max(0, start.manhattan/4-3));
        System.out.println("Build Array level " + endOpenSet.peek().depth + ", size:" + endOpenSet.size());
        System.out.println("sample length   " + endArray.length);

        //start loop
        int depth_Max = 0;
        int poolsize = openSet.size();
        var ResultArray = new FifteenPuzzle[2];
        var flag = false;
        while(!flag){
            FifteenPuzzle currentState = openSet.peek();
            if(currentState.depth > depth_Max) {
                System.out.print("max Search depth: " + currentState.depth);
                System.out.println("\tclose set size: " + closeSet.size() + "\topen set size: " + openSet.size());
                depth_Max = currentState.depth;
            }
            //if((openSet.size() + closeSet.size()) - poolsize >= 200000){
            //    poolsize = openSet.size() + closeSet.size();
            //    Runtime.getRuntime().gc();
            //}
            flag = searchClosest(openSet, closeSet, 80, endArray, endCloseSet, ResultArray);
        }
        sysDate = System.currentTimeMillis() - sysDate;
        System.out.printf(ResultArray[0].printSolutionRecursive(true));
        ResultArray[1].parent.printSolutionForward();
        System.out.println("Used time: " + sysDate*0.001 + "s");
        System.out.println("Total Nodes: " + (openSet.size()+closeSet.size()+endCloseSet.size()+endOpenSet.size()));
    }

}
