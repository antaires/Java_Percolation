/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 *      false = blocked
 *      true = open
 *
 *      valid row and col indices between 1 & n
 *      (not 0 and n-1) !
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    // Grid
    private final int n; // size of grid
    private int openSites; // total count of open sites
    private boolean[][] grid;

    // Union Find
    private WeightedQuickUnionUF qu;
    private int top;
    private int bottom;

    // creates n-by-n grid with all sites initially full / blocked
    public Percolation(int n) {
        this.n = n; // store size
        openSites = 0;
        grid = new boolean[n + 1][n + 1];

        // set all to 1 (blocked / full)
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= n; j++) {
                grid[i][j] = false;
            }
        }

        // set up union find
        qu = new WeightedQuickUnionUF((n * n) + 3);
        top = (n * n) + 1;
        bottom = (n * n) + 2;
        setUpUnionFind();
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (inBounds(row, col)) {
            if (!grid[row][col]) { // if not open already, open
                openSites++;
                grid[row][col] = true;

                // checks 4 directions & adds unions
                checkSite(row, col);

                int id = getId(row, col);

                // if top row, connect to top
                if (row == 1) {
                    qu.union(id, top);
                }

                // if bottom row AND it connects to top, connect to bottom
                if ((row == n) && qu.connected(id, top)) {
                    qu.union(id, bottom);
                }

                // if any bottom cell is connected to this one, AND it conns to
                // top connect to bottom
                if (bottomConnected(id) && qu.connected(id, top)) {
                    qu.union(id, bottom);
                }
            }
        }
    }

    private boolean bottomConnected(int id) {
        // are any sites on bottom row in same group?
        for (int i = 1; i <= n; i++) {
            if (qu.connected(id, getId(n, i))) {
                return true;
            }
        }
        return false;
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (inBounds(row, col)) {
            return (grid[row][col]);
        }
        return false;
    }

    // is the site (row, col) full? (is blue, percs to here)
    public boolean isFull(int row, int col) {
        if (!isOpen(row, col)) {
            return false;
        }
        // if top to this cell connects
        return qu.connected(getId(row, col), top);

        // prevent backwash...
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSites;
    }

    // does the system percolate?
    public boolean percolates() {
        // if top and bottom connected, system percolates
        if (qu.connected(top, bottom)) {
            return true;
        }
        return false;
    }

    private void checkSite(int row, int col) {
        // if site blocked / full, do nothing
        if (isOpen(row, col)) {
            if (dirBounds(row, col)) {
                int site = getId(row, col);
                // check 4 directions and ADD UNION if both open
                checkDir(site, row - 1, col); // up
                checkDir(site, row + 1, col); // down
                checkDir(site, row, col - 1); // left
                checkDir(site, row, col + 1); // right
            }
        }
    }

    private void checkDir(int site, int row, int col) {
        if (dirBounds(row, col) && isOpen(row, col)) {
            qu.union(site, getId(row, col));
        }
    }

    private boolean dirBounds(int row, int col) {
        if ((row < 1 || row > n) || (col < 1 || col > n)) {
            return false;
        }
        return true;
    }

    private void setUpUnionFind() {
        // connect all top row sites to top AND
        // connect all bottom row sites to bottom
        for (int j = 1; j <= n; j++) {
            // if top open, connect
            if (isOpen(1, j)) {
                qu.union(getId(1, j), top);
            }
            // if bottom open AND it connects to top, connect
            if (isOpen(n, j) && qu.connected(getId(n, j), top)) {
                qu.union(getId(n, j), bottom);
            }
        }
    }

    private int getId(int row, int col) {
        if (inBounds(row, col)) {
            return ((n * row) - n) + col;
        }
        return -1;
    }

    private boolean inBounds(int row, int col) {
        if ((row < 1 || row > n) || (col < 1 || col > n)) {
            throw new java.lang.IllegalArgumentException();
        }
        return true;
    }

    // -----------------
    //  TESTS
    // -----------------

    private void testAll() {
        testBasic();
        testPerc();
        System.out.println("percolation tests passed");
    }

    private void testBasic() {
        int n = 10;
        Percolation p = new Percolation(n);
        // assert all sites created and set to full
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                assert (!p.isOpen(i, j));
                assert (!p.isOpen(i, j));
            }
        }

        // test open and count
        assert (p.numberOfOpenSites() == 0);
        p.open(1, 1);
        assert (p.numberOfOpenSites() == 1);
        p.open(2, 2);
        p.open(3, 3);
        p.open(1, 2);
        assert (p.numberOfOpenSites() == 4);

        // test getId()
        assert (p.getId(1, 1) == 1);
        assert (p.getId(2, 2) == 12);
        assert (p.getId(2, 10) == 20);
        assert (p.getId(3, 1) == 21);
        assert (p.getId(2, 5) == 15);
    }

    private void testPerc() {
        percTest1();
        percTest2();
    }

    private void percTest1() {
        int n = 4;
        Percolation p = new Percolation(n);
        p.open(1, 2);
        p.open(1, 3);
        p.open(2, 2);
        p.open(3, 1);
        p.open(3, 2);
        p.open(4, 1);
        assert (p.percolates());
    }

    private void percTest2() {
        int n = 4;
        Percolation p = new Percolation(n);
        p.open(1, 2);
        p.open(1, 3);
        p.open(2, 2);
        p.open(3, 1);
        p.open(3, 2);
        assert (!p.percolates());
        assert (p.numberOfOpenSites() == 5);

        // can later open new sites and re-test
        p.open(4, 1);
        assert (p.percolates());
        assert (p.numberOfOpenSites() == 6);
    }

    // test client (optional)
    public static void main(String[] args) {
        Percolation p = new Percolation(10);
        p.testAll();
    }
}
