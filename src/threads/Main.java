package threads;

import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        int n = 5;
        double[] runtime = new double[n];
        double[] pi = new double[n];

        Scanner reply = new Scanner(System.in);

        System.out.print("Threads: ");
        int numThreads = reply.nextInt();
        int numTerms = 1000000000;

        for (int i = 0; i < n; i++) {
            long start = System.nanoTime();

            ApproxiPi[] threads = new ApproxiPi[numThreads];
            int numTermsPerThread = numTerms / numThreads;
            int nInitial = 0;
            for (int j = 0; j < numThreads; j++) {
                threads[j] = new ApproxiPi(nInitial, numTermsPerThread);
                threads[j].start();
                nInitial += numTermsPerThread;
            }

            for (int j = 0; j < numThreads; j++) {
                threads[j].join();
                pi[i] += threads[j].pi;
            }

            runtime[i] = (double) (System.nanoTime() - start) / (double) 100_000;
        }

        double averageRuntime = 0;
        for (int i = 0; i < n; i++) {
            averageRuntime += runtime[i] / (double) n;
        }

        double varRuntime = 0;
        for (int i = 0; i < n; i++) {
            double term = runtime[i] - averageRuntime;
            varRuntime += Math.pow(term, 2) / (double) n;
        }

        double patternDeviationRuntime = Math.sqrt(varRuntime);
        System.out.printf("\nN execuções:%d\nmédia duração:%.4fms\n", numTerms, averageRuntime);
        System.out.printf("S - Desvio padrao:%.4fms\n", patternDeviationRuntime);
        System.out.printf("C - coeficiente:%.4f%%\n", (patternDeviationRuntime / averageRuntime) * 100);
        System.out.printf("Pi-%s\n", Arrays.toString(pi));
    }

    private static class ApproxiPi extends Thread {
        double pi;
        int nInitial;
        int numTerms;

        ApproxiPi(int nInitial, int numTerms) {
            this.nInitial = nInitial;
            this.numTerms = numTerms;
        }

        @Override
        public void run() {
            this.pi = 0;
            for (int n = this.nInitial; n < this.nInitial + this.numTerms; n++) {
                pi += Math.pow(-1, n) / (2 * n + 1);
            }

            pi *= 4;
        }
    }
}
