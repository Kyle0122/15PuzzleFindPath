import java.util.Arrays;

class FifteenPuzzle {
    static int boardLength = 4;
    long board;
    FifteenPuzzle parent;
    byte x,y;
    byte manhattan;
    byte depth;

    FifteenPuzzle(int[][] data) {
        parent = this;
        manhattan = (byte) 127;
        for(byte i = 0; i < boardLength; i++) {
            for(byte j = 0; j < boardLength; j++) {
                if(data[i][j] == 0) {
                    x = i; y = j;
                }else {
                    setXY(i, j, data[i][j]);
                }
            }
        }
    }

    FifteenPuzzle(int[][] data, FifteenPuzzle parent) {
        this(data);
        this.parent = parent;
    }

    public void setXY(int x, int y, int value) {
        int shift = (4*x + y) *4; // calc the shift of bits
        long mask = (long)value << (60 - shift);
        board = board & ~((long)0xf << (60 - shift)); // clear the bits to be write
        board = board | mask; // wirte the bit
    }

    public int getXY(int x, int y) {
        int shift = (4*x + y) *4; // calc the shift of bits
        return (int)((board >>> (60 - shift)) & 0xf);
    }

    public byte getManhattan(FifteenPuzzle target) {
        int manhattan = 0;
        int[] targetBoardX = new int[16];
        int[] targetBoardY = new int[16];
        int index = 0; //index is the sliding puzzle number 0-15;
        for(int i = 0; i < boardLength; i++) {
            for(int j = 0; j < boardLength; j++) {
                index = target.getXY(i, j);
                targetBoardX[index] = i;
                targetBoardY[index] = j;
            }
        }
        for(int i = 0; i < boardLength; i++) {
            for(int j = 0; j < boardLength; j++) {
                index = getXY(i, j);
                if(index == 0) continue;
                int xx = targetBoardX[index];
                int yy = targetBoardY[index];
                manhattan +=  Math.abs(yy-j) + Math.abs(xx-i);
            }
        }
        return (byte)manhattan;
    }

    public byte getParity() {
        byte shift = 0;
        byte parity = 0;
        var lastnum = (int)((board >>> (60 - shift)) & 0xf);
        for(shift = 4; shift <= 60; shift += 4) {
            int num = (int)((board >>> (60 - shift)) & 0xf);
            if(num < lastnum) {
                parity++;
            }
            lastnum = num;
        }
        return parity;
    }

    private void moveZero(int rx, int ry) {
        rx += x;
        ry += y;
        setXY(x, y, getXY(rx, ry));
        setXY(rx, ry, 0);
        x = (byte) rx;
        y = (byte) ry;
    }

    public FifteenPuzzle[] getChildren() {
        var children = new FifteenPuzzle[4];
        int index = 0;
        int[][] newboard = new int[4][4];
        for(int i = 0; i < boardLength; i++) {
            for(int j = 0; j < boardLength; j++) {
                newboard[i][j] = getXY(i, j);
            }
        }

        if(x!=0 && parent.x!=x-1){
            children[index] = new FifteenPuzzle(newboard, this);
            children[index].moveZero(-1,0);
            children[index].depth = (byte) (this.depth + 1);
            index++;
        }
        if(x!=boardLength-1 && parent.x!=x+1){
            children[index] = new FifteenPuzzle(newboard, this);
            children[index].moveZero(1,0);
            children[index].depth = (byte) (this.depth + 1);
            index++;
        }
        if(y!=0 && parent.y!=y-1){
            children[index] = new FifteenPuzzle(newboard, this);
            children[index].moveZero(0,-1);
            children[index].depth = (byte) (this.depth + 1);
            index++;
        }
        if(y!=boardLength-1 && parent.y!=y+1){
            children[index] = new FifteenPuzzle(newboard, this);
            children[index].moveZero(0,1);
            children[index].depth = (byte) (this.depth + 1);
            index++;
        }

        return Arrays.copyOf(children, index);
    }

    public String printSolutionRecursive(boolean forward){
        StringBuilder mergedResult = new StringBuilder();
        if(this.parent == this){
            return this.toString();
        } else {
            String[] A = this.parent.printSolutionRecursive(forward).split("\n");
            String[] B = this.toString().split("\n");
            if (A[A.length-1].length() + 10 >= 82) {
                for (int i = 0; i < A.length; i++) {
                    mergedResult.append(A[i] + "\n");
                }
                for (int i = 0; i < B.length; i++) {
                    mergedResult.append(B[i] + "\n");
                }
            } else {
                for (int i = 0; i < A.length-B.length; i++) {
                    mergedResult.append(A[i] + "\n");
                }
                for (int i = -B.length; i < 0; i++) {
                    mergedResult.append(A[A.length+i] + " > " + B[B.length+i] + "\n");
                }
            }
        }
        return mergedResult.toString();
    }

    public void printSolutionForward(){
        System.out.println(this);
        if(this.parent != this){
            this.parent.printSolutionForward();
        }
    }

    @Override
    public boolean equals(Object obj) {
        var puzz = (FifteenPuzzle)obj;
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
        return ans + String.format("%8d", this.depth) + "\n";
    }

    @Override
    public int hashCode() {
        //return Long.valueOf(board).hashCode();
        return (int)(board + board>>32);
    }
}
