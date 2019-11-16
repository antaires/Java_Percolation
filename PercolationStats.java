/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private int n;
    private int t;
    private double[] percThresh;
    private double mean;
    private double sdev;
    private final double confidenceNum = 1.96;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {

        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("inputs n and trials must be positive");
        }

        this.n = n;
        this.t = trials;
        percThresh = new double[trials];
        initPercThresh();

        for (int i = 0; i < trials; i++) {
            Percolation perc = new Percolation(n);
            int count = 0;
            while (perc.percolates() == false) {
                int row = StdRandom.uniform(1, n + 1);
                int col = StdRandom.uniform(1, n + 1);
                if (!perc.isOpen(row, col)) {
                    perc.open(row, col);
                    count++;
                }
            }
            // perc threshold = opened / total
            percThresh[i] = ((double) perc.numberOfOpenSites() / (double) (n * n));
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        mean = StdStats.mean(percThresh);
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        sdev = StdStats.stddev(percThresh);
        return sdev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return (mean - confidenceNum * sdev / Math.sqrt(t));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return (mean + confidenceNum * sdev / Math.sqrt(t));
    }

    private void initPercThresh() {
        for (int i = 0; i < t; i++) {
            percThresh[i] = 0;
        }
    }

    public static void main(String[] args) {

        System.out.println(args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }

        if (args.length != 2) {
            System.out.println("Error, run with 2 ints, N and T");
            return;
        }

        PercolationStats ps = new PercolationStats(Integer.parseInt(args[0]),
                                                   Integer.parseInt(args[1]));
        System.out.println("mean = " + ps.mean());
        System.out.println("stddev = " + ps.stddev());
        System.out.println(
                "95% confidence interval = [" + ps.confidenceLo() + ", " + ps.confidenceHi() + "]");
    }
}
