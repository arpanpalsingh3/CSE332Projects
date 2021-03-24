package paralleltasks;

import cse332.exceptions.NotYetImplementedException;

import java.util.concurrent.RecursiveAction;

/*
   1) This class is used by PopulateGridTask to merge two grids in parallel
   2) SEQUENTIAL_CUTOFF refers to the maximum number of grid cells that should be processed by a single parallel task
 */

public class MergeGridTask extends RecursiveAction {
    int[][] left, right;
    int rowLo, rowHi, colLo, colHi;
    final static int SEQUENTIAL_CUTOFF = 10;

    public MergeGridTask(int[][] left, int[][] right, int rowLo, int rowHi, int colLo, int colHi) {
        this.left = left;
        this.right = right;
        this.rowLo = rowLo;
        this.rowHi = rowHi;
        this.colLo = colLo;
        this.colHi = colHi;
    }

    protected void compute() {
        if (rowHi - rowLo <= SEQUENTIAL_CUTOFF && colHi - colLo <= SEQUENTIAL_CUTOFF) {
            sequentialMergeGird(rowLo, rowHi, colLo, colHi);
        } else {
            int rowMid = rowLo + (rowHi - rowLo) / 2;
            int colMid = colLo + (colHi - colLo) / 2;

            MergeGridTask rowLoColLo = new MergeGridTask(left, right, rowLo, rowMid, colLo, colMid);
            MergeGridTask rowLoColHi = new MergeGridTask(left, right, rowLo, rowMid, colMid, colHi);
            MergeGridTask rowHiColLo = new MergeGridTask(left, right, rowMid, rowHi, colLo, colMid);
            MergeGridTask rowHiColHi = new MergeGridTask(left, right, rowMid, rowHi, colMid, colHi);

            rowLoColLo.fork();
            rowLoColHi.fork();
            rowHiColLo.fork();
            rowHiColHi.compute();
            rowLoColLo.join();
            rowLoColHi.join();
            rowHiColLo.join();
        }
    }

    private void sequentialMergeGird(int rowLo, int rowHi, int colLo, int colHi) {
        for (int i = colLo; i < colHi; i++) {
            for (int j = rowLo; j < rowHi; j++) {
                left[i][j] = left[i][j] + right[i][j];
            }
        }
    }
}
