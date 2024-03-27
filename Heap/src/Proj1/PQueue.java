package Proj1;

import java.util.*;
// DISCLAIMER: I DO NOT SUPPORT PEOPLE PLAGIARIZING OUR CODE. I DO NOT TAKE RESPONSIBILITY FOR THE UNLAWFUL ACTIONS OF OTHERS.
/**
 * This class models a priority min-queue that uses an array-list-based min
 * binary heap that implements the PQueueAPI interface. The array holds objects
 * that implement the parameterized Comparable interface.
 *
 * @author Duncan & [REDACTED]
 * @param <E> the priority queue element type.
 * @author William Duncan  <pre>
 * Date: 21SEP22
 * Instructor: Dr. Duncan
 * </pre>
 */
public class PQueue<E extends Comparable<E>> implements PQueueAPI<E> {

    /**
     * A complete tree stored in an array list representing the binary heap
     */
    private ArrayList<E> tree;
    /**
     * A comparator lambda function that compares two elements of this heap when
     * rebuilding it; cmp.compare(x,y) gives 1. negative when x less than y 2.
     * positive when x greater than y 3. 0 when x equal y
     */
    private Comparator<? super E> cmp;

    /**
     * Constructs an empty PQueue using the compareTo method of its data type as
     * the comparator
     */
    public PQueue() {
        tree = new ArrayList<>();
        cmp = (x,y) -> x.compareTo(y);
    }

    /**
     * A parameterized constructor that uses an externally defined comparator
     *
     * @param fn - a trichotomous integer value comparator function
     */
    public PQueue(Comparator<? super E> fn) {
        // implement this method
        tree = new ArrayList<>();
        cmp = fn;
    }

    public boolean isEmpty() {
        // implement this method
        return tree.isEmpty();
    }

    public void insert(E obj) {
        //implement this method
        tree.add(obj);
        int child = tree.size() - 1;//child "points" to new obj index

        while (child > 0) {
            int parent = (child - 1) / 2; //integer division takes care of right child

            if (cmp.compare(tree.get(child), tree.get(parent)) < 0) {
                swap(child, parent);
                child = parent; //moves up level
            } else {
                break;
            }
        }

    }

    public E remove() throws PQueueException {

        if (tree.size() <= 0) {
            throw new PQueueException();
        }
        if (tree.size() == 1) {
            return tree.remove(0);
        }

        E ret_val = tree.get(0);
        swap(0, tree.size() - 1);//min value at end of tree
        tree.remove(tree.size() - 1);//min value cutt off

        rebuild(0 ,tree.size());
        return ret_val;
    }

    public E peek() throws PQueueException {
        if (tree.size() >= 1) {
            return tree.get(0);
        } else {
            return null;
        }
    }

    public int size() {
        return tree.size();
    }

    /**
     * Swaps a parent and child elements of this heap at the specified indices
     *
     * @param place an index of the child element on this heap
     * @param parent an index of the parent element on this heap
     */
    private void swap(int place, int parent) {
        E f = tree.get(parent);
        E t = tree.get(place);

        tree.set(parent, t);
        tree.set(place, f);
    }

    /**
     * Rebuilds the heap to ensure that the heap property of the tree is
     * preserved.
     *
     * @param root the root index of the subtree to be rebuilt
     * @param eSize the size of this tree
     */
    private void rebuild(int root, int eSize) {
        int l_child = 2 * root + 1;
         while (l_child < eSize) {
            int r_child = l_child + 1;
            int min = l_child;

            if (r_child < eSize) {//if there exists a right child
                if (cmp.compare(tree.get(l_child), tree.get(r_child)) > 0) {//if right is less than left
                    min++;
                }
            }
            if (cmp.compare(tree.get(root), tree.get(min)) > 0) {
                swap(root, min);
                root = min;
                l_child = 2 * root + 1;
            } else {
                break;
            }
        }
    }
    /**
     // FOR DEBUGGING
    public static void main (String args[]) throws Exception{
        PQueue<Integer> foo = new PQueue<>();
        foo.insert(-2);
        foo.insert(0);
        foo.insert(7);
        foo.insert(-5);
        
        while (foo.size() > 0){
            System.out.println(foo.remove());
        }
    }
   **/
}
