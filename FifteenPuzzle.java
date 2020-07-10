import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

class FifteenPuzzle {
    static int boardLength = 4;
    long board;
    byte x,y;
    byte hamming;
    byte deep;
    FifteenPuzzle father;

    FifteenPuzzle(int[][] data){
        father = this;
        hamming = (byte) 127;
        for(byte i = 0; i < boardLength; i++){
            for(byte j = 0; j < boardLength; j++){
                if(data[i][j] == 0){
                    x = i; y = j;
                }else {
                    setXY(i, j, data[i][j]);
                }
            }
        }
    }

    FifteenPuzzle(int[][] data, FifteenPuzzle father){
        this(data);
        this.father = father;
    }

    public void setXY(int x, int y, int value){
        int shift = (4*x + y) *4; // calc the shift of bits
        long mask = (long)value << (60 - shift);
        board = board & ~((long)0xf << (60 - shift)); // clear the bits to be write
        board = board | mask; // wirte the bit
    }

    public int getXY(int x, int y){
        int shift = (4*x + y) *4; // calc the shift of bits
        return (int)((board >>> (60 - shift)) & 0xf);
    }

    public byte getHamming(FifteenPuzzle target){
        int hamming = 0;
        int[] targetBoardX = new int[16];
        int[] targetBoardY = new int[16];
        for(int i = 0; i < boardLength; i++){
            for(int j = 0; j < boardLength; j++){
                int index = target.getXY(i, j);
                targetBoardX[index] = i;
                targetBoardY[index] = j;
            }
        }
        for(int i = 0; i < boardLength; i++){
            for(int j = 0; j < boardLength; j++){
                int index = getXY(i, j);
                if(index == 0)continue;
                int xx = targetBoardX[index];
                int yy = targetBoardY[index];
                hamming +=  Math.abs(yy-j) + Math.abs(xx-i);
            }
        }
        return (byte)hamming;
    }

    private void moveZero(int rx, int ry){
        rx += x;
        ry += y;
        setXY(x, y, getXY(rx, ry));
        setXY(rx, ry, 0);
        x = (byte) rx;
        y = (byte) ry;
    }

    public FifteenPuzzle[] getChildrens(){
        FifteenPuzzle[] childs = new FifteenPuzzle[4];
        int index = 0;
        int[][] newboard = new int[4][4];
        for(int i = 0; i < boardLength; i++){
            for(int j = 0; j < boardLength; j++){
                newboard[i][j] = getXY(i, j);
            }
        }

        if(x!=0 && father.x!=x-1){
            childs[index] = new FifteenPuzzle(newboard, this);
            childs[index].moveZero(-1,0);
            childs[index].deep = (byte) (this.deep + 1);
            index++;
        }
        if(x!=boardLength-1 && father.x!=x+1){
            childs[index] = new FifteenPuzzle(newboard, this);
            childs[index].moveZero(1,0);
            childs[index].deep = (byte) (this.deep + 1);
            index++;
        }
        if(y!=0 && father.y!=y-1){
            childs[index] = new FifteenPuzzle(newboard, this);
            childs[index].moveZero(0,-1);
            childs[index].deep = (byte) (this.deep + 1);
            index++;
        }
        if(y!=boardLength-1 && father.y!=y+1){
            childs[index] = new FifteenPuzzle(newboard, this);
            childs[index].moveZero(0,1);
            childs[index].deep = (byte) (this.deep + 1);
            index++;
        }

        return Arrays.copyOf(childs, index);
    }

    public void printSolutionRecursive(){
        if(this.father != this){
            this.father.printSolutionRecursive();
        }
        System.out.println(this + "\t" + this.deep);
    }

    public void printSolutionForward(){
        System.out.println(this + "\t" + this.deep);
        if(this.father != this){
            this.father.printSolutionForward();
        }
    }

    public static boolean searchClosest(PriorityQueue<FifteenPuzzle> openSet, HashMap<Long, FifteenPuzzle> closeSet,
        FifteenPuzzle[] target, HashMap<Long, FifteenPuzzle> targetSet){
        FifteenPuzzle current = openSet.poll();
        FifteenPuzzle[] childs = current.getChildrens();
        //check if child is in the target open set:
        for(FifteenPuzzle puzzle : childs){
            if(targetSet.containsKey(Long.valueOf(puzzle.board))){
                puzzle.printSolutionRecursive();
                System.out.println("*");
                FifteenPuzzle puzz = targetSet.get(Long.valueOf(puzzle.board));
                puzz.printSolutionForward();
                System.out.println("Find Soultion in " + (puzzle.deep + puzz.deep) + " steps");
                return true;
            }
        }
        //if the current node is not in the close set or it has a lower deep value, add it to the close set.
        if(!closeSet.containsKey(Long.valueOf(current.board))){
            closeSet.put(Long.valueOf(current.board), current);
        }else{
            FifteenPuzzle old = closeSet.get(Long.valueOf(current.board));
            if(current.deep < old.deep){
                closeSet.put(Long.valueOf(current.board), current);
            }else {
                return false;
            }
        }
        // set the value to discard a node, the node with deep under 30 is protected
        int maxHammingValue = 60;
        if(current.deep >= 30){
            //maxHammingValue = 72 - current.deep;// the maximum level is set to
            maxHammingValue = 45;
        }
        for(int i = 0; i < childs.length; i++) {
            for(FifteenPuzzle puzz : target) {
                byte a = childs[i].getHamming(puzz);
                if(a < childs[i].hamming){
                    childs[i].hamming = a;
                }
            }
            if(childs[i].hamming < maxHammingValue){
                openSet.add(childs[i]);
            }
        }
        return false;
    }

    public static FifteenPuzzle[] buildArray(FifteenPuzzle start, FifteenPuzzle end, 
        PriorityQueue<FifteenPuzzle> openSet, HashMap<Long, FifteenPuzzle> closeSet, int deep, int sampleDeep){
        int level = 0;
        FifteenPuzzle[] sample = new FifteenPuzzle[0];
        PriorityQueue<FifteenPuzzle> currentLevel = new PriorityQueue<FifteenPuzzle>(comp);
        currentLevel.add(start);
        if(deep <= 0){deep = 1;}
        while(level < deep) {
            PriorityQueue<FifteenPuzzle> nextLevel = new PriorityQueue<FifteenPuzzle>(comp);
            for(FifteenPuzzle puzz : currentLevel){
                FifteenPuzzle[] childs = puzz.getChildrens();
                for(int i = 0; i < childs.length; i++){
                    childs[i].hamming = childs[i].getHamming(end);
                    if(!closeSet.containsKey(Long.valueOf(childs[i].board))){
                        nextLevel.add(childs[i]);
                    }
                }
                closeSet.put(Long.valueOf(puzz.board), puzz);
            }
            currentLevel = nextLevel;
            if(level == sampleDeep){
                sample = currentLevel.toArray(sample);
            }
            level++;
        }
        openSet.addAll(currentLevel);
        System.out.println("Build Array level " + level + ", size:" + openSet.size() + "   " + openSet.peek().deep);
        return sample;
    }

    public static void main(String[] args) {
        //int[][] startBoard = {{1, 0, 2, 4},{5, 6, 3, 8},{9,10,7,11},{13,14,15,12}};
        //int[][] startBoard = {{1, 10, 2, 4},{5, 11, 3, 7},{9, 0, 6, 8},{13,14,15,12}}; //11 steps
        //int[][] startBoard = {{10, 6, 12, 11},{8, 7, 0, 4},{5, 2, 3, 1},{9,13,14,15}}; //43 steps
        //int[][] startBoard = {{11, 9, 0, 12},{14, 15, 10, 8},{2,6,13,5},{3,7,4,1}}; //66 steps, uses .5gigs of memory
        //int[][] startBoard = {{11, 15, 9, 12},{14, 10, 8, 13},{6, 2, 5, 0},{3,7,4,1}}; //69 steps, uses 1.8gigs of memory
        int[][] startBoard = {{11, 15, 9, 12},{14, 10, 13, 8},{6, 2, 4, 7},{3, 5, 0, 1}}; //67 steps
        FifteenPuzzle start = new FifteenPuzzle(startBoard);
        start.deep = 0;
        int[][] endBoard = {{1, 2, 3, 4},{5, 6, 7, 8},{9,10,11,12},{13,14,15, 0}};
        FifteenPuzzle end = new FifteenPuzzle(endBoard);
        end.deep = 0;

        start.hamming = start.getHamming(end);
        end.hamming = end.getHamming(start);
        System.out.printf("start: \n" + start);
        System.out.println("start ham: " + start.hamming);
        long sysDate = System.currentTimeMillis();

        //init open set, close set & start Array
        PriorityQueue<FifteenPuzzle> openSet = new PriorityQueue<FifteenPuzzle>(comp);
        HashMap<Long, FifteenPuzzle> closeSet = new HashMap<Long, FifteenPuzzle>();
        buildArray(start, end, openSet, closeSet, start.hamming/6, 0);

        //init target Set & target Array
        PriorityQueue<FifteenPuzzle> endOpenSet = new PriorityQueue<FifteenPuzzle>(comp);
        HashMap<Long, FifteenPuzzle> endCloseSet = new HashMap<Long, FifteenPuzzle>();
        FifteenPuzzle[] endArray = buildArray(end, start, endOpenSet, endCloseSet, start.hamming/5 + 1, Math.min(3, start.hamming/5));
        System.out.println("sample length   " + endArray.length);

        //start loop
        int deepMax = 0;
        int poolsize = openSet.size();
        boolean flag = false;
        while(!flag){
            FifteenPuzzle currentState = openSet.peek();
            if(currentState.deep > deepMax) {
                System.out.print("max Search deep: " + currentState.deep);
                System.out.println("\tclose set size: " + closeSet.size() + "\topen set size: " + openSet.size());
                deepMax = currentState.deep;
            }
            if((openSet.size() + closeSet.size()) - poolsize >= 200000){
                poolsize = openSet.size() + closeSet.size();
                Runtime.getRuntime().gc();
            }
            flag = searchClosest(openSet, closeSet, endArray, endCloseSet);
        }
        sysDate = System.currentTimeMillis() - sysDate;
        System.out.println("Used time: " + sysDate*0.001 + "s");
    }
    
    static Comparator<FifteenPuzzle> comp = new Comparator<FifteenPuzzle>() {
        public int compare(FifteenPuzzle a, FifteenPuzzle b){
            return (a.deep*9 + a.hamming*11) - (b.deep*9 + b.hamming*11);
        }
    };

    @Override
    public boolean equals(Object obj) {
        FifteenPuzzle puzz = (FifteenPuzzle)obj;
        return this.board == puzz.board;
    }
    
    @Override
    public String toString() {
        String ans = "";
        for(int i = 0; i < boardLength; i++){
            for(int j = 0; j < boardLength; j++){
                ans += String.format("%x", getXY(i, j));
                ans += ' ';
            }
            ans += '\n';
        }
        return ans;
    }
}