import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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
        System.out.println(this + "\t" + this.hamming);
    }

    public void printSolutionForward(){
        System.out.println(this + "\t" + this.hamming);
        if(this.father != this){
            this.father.printSolutionForward();
        }
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

    @Override
    public int hashCode() {
        return Long.valueOf(this.board).hashCode();
    }

    static Comparator<FifteenPuzzle> comp = new Comparator<FifteenPuzzle>() {
        public int compare(FifteenPuzzle a, FifteenPuzzle b){
            return (a.deep*8 + a.hamming*10) - (b.deep*8 + b.hamming*10);
        }
    };

    public static boolean searchClosest(PriorityQueue<FifteenPuzzle> openSet, HashMap<Long, FifteenPuzzle> closeSet, FifteenPuzzle[] target){
        FifteenPuzzle current = openSet.poll();
        FifteenPuzzle[] childs = current.getChildrens();
        int discardHammingValue = 70;
        if(current.deep >= 40){
            discardHammingValue = openSet.peek().hamming + (int)((120 - current.deep)*0.2); // set the value to discard a node
        }
        for(int i = 0; i < childs.length; i++){
            childs[i].hamming = (byte) 127;
            for(int j = 0;j < target.length; j++){
                byte a = childs[i].getHamming(target[j]);
                if(a < childs[i].hamming){
                    childs[i].hamming = a;
                }
                if(a == 0){
                    childs[i].printSolutionRecursive();
                    System.out.println("*");
                    target[j].printSolutionForward();
                    return true;
                }
            }
            if(childs[i].hamming < discardHammingValue){ //protect early nodes & trim bad nodes
                openSet.add(childs[i]);
            }
        }

        if(closeSet.containsKey(Long.valueOf(current.board))){
            FifteenPuzzle old = closeSet.get(Long.valueOf(current.board));
            if(current.deep < old.deep){
                closeSet.put(Long.valueOf(current.board), current);
            }
        }else{
            closeSet.put(Long.valueOf(current.board), current);
        }

        return false;
    }

    public static void main(String[] args) {
        //int[][] start = {{1, 0, 2, 4},{5, 6, 3, 8},{9,10,7,11},{13,14,15,12}};
        //int[][] start = {{1, 10, 2, 4},{5, 11, 3, 7},{9, 0, 6, 8},{13,14,15,12}}; //11 steps
        //int[][] start = {{10, 6, 12, 11},{8, 7, 0, 4},{5, 2, 3, 1},{9,13,14,15}}; //43 steps
        int[][] start = {{11, 9, 0, 12},{14, 15, 10, 8},{2,6,13,5},{3,7,4,1}}; //66 steps, uses 2gigs of memory
        //int[][] start = {{11, 15, 9, 12},{14, 10, 8, 13},{6, 2, 5, 0},{3,7,4,1}};
        FifteenPuzzle puzzleStart = new FifteenPuzzle(start);
        puzzleStart.deep = 0;
        int[][] end = {{1, 2, 3, 4},{5, 6, 7, 8},{9,10,11,12},{13,14,15, 0}};
        FifteenPuzzle puzzleEnd = new FifteenPuzzle(end);
        puzzleEnd.deep = 0;

        puzzleStart.hamming = puzzleStart.getHamming(puzzleEnd);
        puzzleEnd.hamming = puzzleEnd.getHamming(puzzleEnd);
        System.out.printf("start: \n" + puzzleStart);
        System.out.println("start ham: " + puzzleStart.hamming);

        //init open set and close set
        PriorityQueue<FifteenPuzzle> openSet = new PriorityQueue<FifteenPuzzle>(comp);
        HashMap<Long, FifteenPuzzle> closeSet = new HashMap<Long, FifteenPuzzle>();
        closeSet.put(Long.valueOf(puzzleStart.board), puzzleStart);
        openSet.add(puzzleStart);

        //init target Set
        FifteenPuzzle[] targetSet = puzzleEnd.getChildrens();
        //searchClosest(openSet, closeSet, targetSet);

        //start loop
        int deepMax = 0;
        boolean flag = false;
        while(!flag){
            FifteenPuzzle currentState = openSet.peek();
            if(currentState.deep > deepMax){
                System.out.print("max Search deep: " + currentState.deep);
                System.out.println("\tclose set size: " + closeSet.size() + "\topen set size: " + openSet.size());
                deepMax = currentState.deep;
                Runtime.getRuntime().gc();
            }
            flag = searchClosest(openSet, closeSet, targetSet);
        }
    }
    /*Print all nodes in the openSet. This is for debug */
    public static void printKeySet(PriorityQueue<FifteenPuzzle> openSet){
        Iterator<FifteenPuzzle> itr = openSet.iterator();
        while(itr.hasNext()){
            FifteenPuzzle puzz = itr.next();
            System.out.println(String.format("%016x",puzz.board));
        }
    }
}