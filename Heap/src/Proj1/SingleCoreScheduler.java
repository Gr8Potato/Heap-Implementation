package Proj1;
// DISCLAIMER: I DO NOT SUPPORT PEOPLE PLAGIARIZING OUR CODE. I DO NOT TAKE RESPONSIBILITY FOR THE UNLAWFUL ACTIONS OF OTHERS.
/**
 * A application to simulate a non-preemptive scheduler for a single-core CPU
 * using a heap-based implementation of a priority queue
 *
 * @author William Duncan & [REDACTED]
 * @see PQueue.java, PCB.java  <pre>
 * DATE: 21SEP22
 * File:SingleCoreScheduler.java
 * Instructor: Dr. Duncan
 * Usage: SingleCoreScheduler <number of cylces> <-R or -r> <probability of a  process being created per cycle>  or,
 *        SingleCoreScheduler <number of cylces> <-F or -f> <file name of file containing processes>,
 *        The simulator runs in either random (-R or -r) or file (-F or -f) mode
 * </pre>
 */
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

public class SingleCoreScheduler {

    /**
     * Single-core processor with non-preemptive scheduling simulator
     *
     * @param args an array of strings containing command line arguments args[0]
     * - number of cyles to run the simulation args[1] - the mode: -r or -R for
     * random mode and -f or -F for file mode args[2] - if the mode is random,
     * this entry contains the probability that a process is created per cycle
     * and if the simulator is running in file mode, this entry contains the
     * name of the file containing the the simulated jobs. In file mode, each
     * line of the input file is in this format:
     * <process ID> <priority value> <cycle of process creation>
     * <time required to execute>
     */
    public static void main(String[] args) throws PQueueException, IOException {
        if (args.length != 3) {
            System.out.println("Usage: SingleCoreScheduler <number of cylces> <-R or -r> <probability of a  process being created per cycle>  or ");
            System.out.println("       SingleCoreScheduler <number of cylces> <-F or -f> <file name of file containing processes>");
            System.out.println("The simulator runs in either random (-R or -r) or file (-F or -f) mode.");
            System.exit(1);
        }
        //Complete the implementation of this method
        int cycle = 1;
        int numCycles = Integer.parseInt(args[0]);

        Comparator<PCB> cmp = (x, y)
                -> {
            if (y.isExecuting()) {
                return 0;
            }
            if (x.getPriority() < y.getPriority()) {
                return -1;
            }
            if (x.getPriority() > y.getPriority()) {
                return 1;
            }
            if (x.getArrival() > y.getArrival()) {
                return 1;
            }
            if (x.getArrival() < y.getArrival()) {
                return -1;
            }
            return 0;
        };
        double executions = 0;
        double creations = 0;
        int waits = 0;
        PQueue<PCB> CPU = new PQueue<>(cmp);

        if (args[1].equals("-r") || args[1].equals("-R")) {

            //for random
            double prob = Double.parseDouble(args[2]);
            Random rand = new Random();
            int PID = 1;
            final int MAX = 19, MIN = -20;//for priority

            //general state report
            while (cycle <= numCycles) {
                System.out.printf("*** Cycle #: %d%n", cycle);
                if (CPU.isEmpty()) {//if empty
                    System.out.println("The CPU is idle.");

                } else {
                    if (!CPU.peek().isExecuting()) {//if not running
                        CPU.peek().execute();//execute head process
                        CPU.peek().setStart(cycle);
                        waits += cycle - CPU.peek().getArrival();
                    }
                    if (cycle - CPU.peek().getStart() == CPU.peek().getBurst()) {//if task finished
                        System.out.printf("Process #%d has just terminated.%n", CPU.peek().getPid());
                        CPU.remove();
                        executions++;
                    } else {
                        System.out.printf("Process #%d is executing.%n", CPU.peek().getPid());
                    }
                }

                //rolls spawn chance (does every cycle)
                double roll = rand.nextDouble();
                if (roll <= prob) {//if roll lands
                    PCB task = new PCB(PID++, (rand.nextInt(MAX - MIN) + MIN), 0, cycle, rand.nextInt(100 - 1) + 1);//PCB(int iD, int pVal, int run, int arr, int len)
                    CPU.insert(task);
                    System.out.printf("Adding job with pid #%d and priority %d and burst %d.%n", task.getPid(), task.getPriority(), task.getBurst());
                    creations++;
                } else {
                    System.out.println("No new job this cycle.");
                }
                cycle++;
            }
            System.out.printf("The average number of process created per cycle is %f.%n", creations / numCycles);
            System.out.printf("The throughput is %f cycles.%n", executions / numCycles);
            System.out.printf("The average wait time per process is %f.%n", waits / creations);

        } else if (args[1].equals("-f") || args[1].equals("-F")) {

            Scanner in = new Scanner(new BufferedReader(new FileReader(args[2])));
            int[] task_list = new int[4];//takes in 1 row at a time
            boolean is_ready = true;//used so rows aren't overwritten
            //general state report
            while (cycle <= numCycles) {
                System.out.printf("*** Cycle #: %d%n", cycle);
                if (CPU.isEmpty()) {//if empty
                    System.out.println("The CPU is idle.");

                } else {
                    if (!CPU.peek().isExecuting()) {//if not running
                        CPU.peek().execute();//execute head process
                        CPU.peek().setStart(cycle);
                        waits += cycle - CPU.peek().getArrival();
                    }
                    if (cycle - CPU.peek().getStart() == CPU.peek().getBurst()) {//if task finished
                        System.out.printf("Process #%d has just terminated.%n", CPU.peek().getPid());
                        CPU.remove();
                        executions++;
                    } else {
                        System.out.printf("Process #%d is executing.%n", CPU.peek().getPid());
                    }
                }

                //takes row in
                if (in.hasNextInt() && is_ready) {
                    task_list[0] = in.nextInt();//PID
                    task_list[1] = in.nextInt();//PRIORITY
                    task_list[2] = in.nextInt();//ARRIVAL#
                    task_list[3] = in.nextInt();//BURSTCOUNT
                   /**For debugg
                    System.out.printf("PID: %d%n", task_list[0]);
                    System.out.printf("PRIORITY: %d%n", task_list[1]);
                    System.out.printf("ARRIVAL#: %d%n", task_list[2]);
                    System.out.printf("BURSTCOUNT: %d%n", task_list[3]);
                    **/
                    is_ready = false;
                }
                if (cycle == task_list[2]) {
                    PCB task = new PCB(task_list[0], task_list[1], 0, task_list[2], task_list[3]);//PCB(int iD, int pVal, int run, int arr, int len)
                    CPU.insert(task);
                    System.out.printf("Adding job with pid #%d and priority %d and burst %d.%n", task.getPid(), task.getPriority(), task.getBurst());
                    creations++;
                    is_ready = true;
                } else {
                    System.out.println("No new job this cycle.");
                }
                cycle++;
            }
            System.out.printf("The average number of process created per cycle is %f.%n", creations / numCycles);
            System.out.printf("The throughput is %f cycles.%n", numCycles / executions);
            System.out.printf("The average wait time per process is %f.%n", waits / creations);

        } else {
            System.out.println("Invalid command parameters");
            throw new IOException();
        }
    }

}
