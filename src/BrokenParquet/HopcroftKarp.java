/**
 * KartMatch: HopcroftKarp.java
 *
 *   Implementation of the Hopcroft-Karp algorithm to find a maximum matching on a bipartite graph.
 *
 *   This implementation has the option to randomize the output matching (i.e. it is selected
 *   randomly among all the possible solutions to the maximum matching problem).
 *
 *   Useful information regarding the Hopcroft-Karp algorithm can be found there:
 *      - http://en.wikipedia.org/wiki/Matching_(graph_theory)#Maximum_matchings_in_bipartite_graphs
 *      - http://en.wikipedia.org/wiki/Hopcroft-Karp_algorithm
 *      - http://code.activestate.com/recipes/123641/
 *
 *   The last link in particular deserves some credits as it is an implementation of the algorithm in
 *   Python done by David Eppstein, which was useful to understand the algorithm and served as a
 *   source of inspiration for the implementation in this file.
 *
 *   Input:
 *      - The input graph (U, V, E) is described as an HashMap<Integer, ArrayList<Integer>>, mapping each 
 *        vertex in U to a list of vertices in V. All vertexes are integers, and a non-connected vertex
 *        from U must be associated with the empty list. Sets U and V can be of different sizes.
 *      - The set V, as an ArrayList<Integer>. (Its only purpose is to compute the unmatched output).
 *      - A boolean is also passed as an argument to specify whether the output should be randomized or not.
 *
 *   Output: an object of type HopcroftKarp.Result containing
 *      - A boolean, true if the matching was perfect, false otherwise.
 *      - A maximum matching for that graph, returned as a SparseIntArray, mapping a subset of U to a
 *        subset of V.
 *      - A mapping of the remaining, unmatched, vertices of U to the remaining vertices of V.
 *        This mapping is of course disjoint from the edges of the graph. 
 *
 *
 * Copyright (C) 2013, Pierre DEJOUE
 * All rights reserved.
 * This software may be modified and distributed under the terms of the BSD license. See the LICENSE file for details. 
 */
package BrokenParquet;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.lang.reflect.Array;

//import android.annotation.SuppressLint;
//import android.util.Log;
//import android.util.SparseIntArray;


public class HopcroftKarp
{
    public static class Result
    {
        public boolean         perfect_matching;
        public SparseIntArray  matching   = new SparseIntArray();
        public SparseIntArray  unmatched  = new SparseIntArray();

        // Strangely enough, the SparseIntArray.clone() method was not supported on my Motorola Milestone (Android API 7)
        private static SparseIntArray clone(SparseIntArray arr)
        {
            SparseIntArray out = new SparseIntArray();
            for(int idx = 0; idx < arr.size(); idx++)
            {
                out.put(arr.keyAt(idx), arr.valueAt(idx));
            }
            return out;
        }

        public Result clone()
        {
            Result copy = new Result();

            copy.perfect_matching = perfect_matching;
            copy.matching         = clone(matching);
            copy.unmatched        = clone(unmatched);

            return copy;
        }
    };

    // Utility function used to manipulate a hash map of type HashMap<Integer, ArrayList<Integer>>
    // This function will return the ArrayList matching the input key, initializing that array in the
    // case the key was not present in the associative array.
    private static ArrayList<Integer> getValueOrDefault(HashMap<Integer, ArrayList<Integer>> map, Integer key)
    {
        ArrayList<Integer> val = map.get(key);
        if(val == null)
        {
            // Key is not present in the map, create it with the empty list as the default value
            map.put(key, new ArrayList<Integer>());
        }

        return map.get(key);
    }

    //
    // The Hopcroft-Karp algorithm
    //
    public  static HashMap findMaximumMatching(HashMap<Integer, ArrayList<Integer>> graph,
                                              ArrayList<Integer>                   in_vertices_v,
                                              boolean                              randomize)
    {
        // Local variables:
        // The first step of the Hopcroft-Karp algorithm consists in building a list alternating
        // U-layers and V-layers. The current U/V-layer being processed by the algorithm is stored in
        // hash maps current_layer_u and current_layer_v. All U-layers (respectively V-layers) shall 
        // be disjoint from each other. Yet there is no need to store all the layers as they are built,
        // so the algorithm only keeps track of the union of the previous U-layers and V-layers in hash
        // maps all_layers_u and all_layers_v.
        // Finally, hash map matched_v contains the temporary matching built by the algorithm. Upon
        // completion of the algorithm, it is a maximum matching.
        HashMap<Integer, Integer>            current_layer_u     = new HashMap<Integer, Integer>();                 // u --> v
        HashMap<Integer, ArrayList<Integer>> current_layer_v     = new HashMap<Integer, ArrayList<Integer>>();      // v --> list of u
        HashMap<Integer, Integer>            all_layers_u        = new HashMap<Integer, Integer>();                 // u --> v    
        HashMap<Integer, ArrayList<Integer>> all_layers_v        = new HashMap<Integer, ArrayList<Integer>>();      // v --> list of u
//        HashMap<Integer, Integer>            matched_v           = new HashMap<Integer, Integer>();                 // v --> u
        HashMap<Integer, Integer>            matched_v           = new HashMap<Integer, Integer>();                 // v --> u
        ArrayList<Integer>                   unmatched_v         = new ArrayList<Integer>();                        // list of v

        //Log.d("HopcroftKarp.Algo", "graph: " +          graph.toString());
        //Log.d("HopcroftKarp.Algo", "in_vertices_v: " +  in_vertices_v.toString());

        // Loop as long as we can find at least one minimal augmenting path
        while(true)
        {
            int k = 0;  // U-layers have indexes n = 2*k ; V-layers have indexes n = 2*k+1.

            //Log.d("HopcroftKarp.Algo", "matched_v: " +  matched_v.toString());

            // The initial layer of vertices of U is equal to the set of u not in the current matching
            all_layers_u.clear();
            current_layer_u.clear();
            for(Integer u : graph.keySet())
            {
                if(!matched_v.containsValue(u))
                {
                    current_layer_u.put(u, 0);
                    all_layers_u.put(u, 0);
                }
            }

            all_layers_v.clear();
            unmatched_v.clear();

            // Use BFS to build alternating U and V layers, in which:
            //  - The edges between U-layer 2*k   and V-layer 2*k+1 are unmatched ones.
            //  - The edges between V-layer 2*k+1 and U-layer 2*k+2 are matched ones.

            // While the current layer U is not empty and no unmatched V is encountered
            while(!current_layer_u.isEmpty() && unmatched_v.isEmpty())
            {
                //Log.d("HopcroftKarp.Algo", "current_layer_u: " + current_layer_u.toString());

                // Build the layer of vertices of V with index n = 2*k+1                
                current_layer_v.clear();
                for(Integer u : current_layer_u.keySet())
                {
                    for(Integer v : graph.get(u))
                    {
                        if(!all_layers_v.containsKey(v))     // If not already in the previous partitions for V
                        {
                            getValueOrDefault(current_layer_v, v).add(u);
                            // Expand of all_layers_v is done in the next step, building the U-layer
                        }
                    }
                }

                //Log.d("HopcroftKarp.Algo", "current_layer_v: " + current_layer_v.toString());

                k++;
                // Build the layer of vertices of U with index n = 2*k
                current_layer_u.clear();
                for(Integer v : current_layer_v.keySet())
                {
                    all_layers_v.put(v, current_layer_v.get(v));  // Expand the union of all V-layers to include current_v_layer

                    // Is it a matched vertex in V?
                    if(matched_v.containsKey(v))
                    {
                        Integer u = matched_v.get(v);
                        current_layer_u.put(u, v);
                        all_layers_u.put(u, v);                   // Expand the union of all U-layers to include current_u_layer
                    }
                    else
                    {
                        // Found one unmatched vertex v. The algorithm will finish the current layer,
                        // then exit the while loop since it has found at least one augmenting path.
                        unmatched_v.add(v);
                    }
                }
            }

            // After the inner while loop has completed, either we found at least one augmenting path...
            if(!unmatched_v.isEmpty())
            {
                if(randomize)
                {
                    Collections.shuffle(unmatched_v);       // Important to randomize the list here
                    // especially in the case where |V| > |U|
                }
                for(Integer v : unmatched_v)
                {
                    // Use DFS to find one augmenting path ending with vertex V. The vertices from that path, if it 
                    // exists, are removed from the all_layers_u and all_layers_v maps.
                    if(k >= 1)
                    {
                        recFindAugmentingPath(v, all_layers_u, all_layers_v, matched_v, randomize, (k-1));       // Ignore return status
                    }
                    else
                    {
                        throw new ArithmeticException("k should not be equal to zero here.");
                    }
                }
            }
            // ... or we didn't, in which case we already got a maximum matching for that graph
            else
            {
                break;
            }
        } // end while(true)


        // Create output class
//        Result result = new Result();
//
//        result.perfect_matching = (graph.size() == in_vertices_v.size() && graph.size() == matched_v.size());
//        result.matching         = get_reverse_mapping(matched_v);
//        result.unmatched        = build_unmatched_set(graph, matched_v, in_vertices_v, randomize);

        return matched_v;
    }

    // Recursive function used to build an augmenting path starting from the end node v. 
    // It relies on a DFS on the U and V layers built during the first phase of the algorithm.
    // This is by the way this function which is responsible for most of the randomization
    // of the output.
    // Returns true if an augmenting path is found.
    private static boolean recFindAugmentingPath(Integer v,
                                                 HashMap<Integer, Integer>            all_layers_u,
                                                 HashMap<Integer, ArrayList<Integer>> all_layers_v,
                                                 HashMap<Integer, Integer>            matched_v,
                                                 boolean randomize,
                                                 int k)
    {
        if(all_layers_v.containsKey(v))
        {
            ArrayList<Integer> list_u = all_layers_v.get(v);

            // If random output is requested
            if(randomize)
            {
                Collections.shuffle(list_u);
            }

            for(Integer u: list_u)
            {
                if(all_layers_u.containsKey(u))
                {
                    Integer prev_v = all_layers_u.get(u);

                    // If the path ending with "prev_v -> u -> v" is an augmenting path
                    if(k == 0 || recFindAugmentingPath(prev_v, all_layers_u, all_layers_v, matched_v, randomize, (k-1)))
                    {
                        matched_v.put(v, u);                        // Edge u -> v replaces the previous matched edge connected to v.
                        all_layers_v.remove(v);                     // Remove vertex v from all_layers_v
                        all_layers_u.remove(u);                     // Remove vertex u from all_layers_u
                        return true;
                    }
                }
            }
        }

        return false;   // No augmenting path found
    }

    // Given an input associative array that stores (key, value) pairs, and assuming that all values are unique,
    // the following function return the reverse mapping: i.e. the map of (value, key) pairs.
    public static SparseIntArray get_reverse_mapping(HashMap<Integer, Integer> input_map)
    {
        SparseIntArray reversed_map = new SparseIntArray();

        for(Integer v: input_map.keySet())
        {
            Integer u = input_map.get(v);
            reversed_map.put(u, v);
        }

        return reversed_map;
    }

    // Associates all unmatched vertices of U with remaining vertices of from V. Shuffle the result if required
    private static SparseIntArray build_unmatched_set(HashMap<Integer, ArrayList<Integer>> graph,
                                                      HashMap<Integer, Integer>            matched_v,
                                                      ArrayList<Integer>                   in_vertices_v,
                                                      boolean                              randomize)
    {
        ArrayList<Integer> remaining_v  = new ArrayList<Integer>();
        SparseIntArray     unmatched    = new SparseIntArray();

        for(Integer v : in_vertices_v)
        {
            if(!matched_v.containsKey(v))
            {
                remaining_v.add(v);
            }
        }

        // Randomize if requested
        if(randomize)
        {
            Collections.shuffle(remaining_v);
        }

        // Associates the unmatched vertices from U with the remaining ones from V until one of those two sets is exhausted
        for(Integer u: graph.keySet())
        {
            if(!matched_v.containsValue(u))      // If u is not a matched vertex
            {
                if(!remaining_v.isEmpty())
                {
                    unmatched.put(u, remaining_v.get(0));
                    remaining_v.remove(0);
                }
                else
                {
                    break;
                }
            }
        }

        return unmatched;
    }

    //
    // Test functions (DEBUG ONLY)
    //

    private static void GenericTest(HashMap<Integer, ArrayList<Integer>> graph,
                                    ArrayList<Integer>                   in_vertices_v,
                                    boolean                              randomize)
    {
/*
        Log.d("HopcroftKarp.Test", "graph: " + graph.toString());

        Result result = findMaximumMatching(graph, in_vertices_v, randomize);

        Log.d("HopcroftKarp.Test", "perfect_matching: " + result.perfect_matching);

        Log.d("HopcroftKarp.Test", "out_matching:");
        for(int idx = 0; idx < result.matching.size(); idx++)
        {
            Log.d("HopcroftKarp.Test", result.matching.keyAt(idx) + " -> " + result.matching.valueAt(idx));
        }

        Log.d("HopcroftKarp.Test", "out_unmatched:");
        for(int idx = 0; idx < result.unmatched.size(); idx++)
        {
            Log.d("HopcroftKarp.Test", result.unmatched.keyAt(idx) + " -> " + result.unmatched.valueAt(idx));
        }
        */
    }

    public static void main(String[] args) {

        HashMap<Integer, ArrayList<Integer>> test_graph = new HashMap<Integer, ArrayList<Integer>>();
//        SparseIntArray test_graph = new SparseIntArray();

        ArrayList<Integer>                   array_v    = new ArrayList<Integer>();

        try {
            BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
            int n=0,m=0,a=0,b=0;
            StringBuilder sb = new StringBuilder();
            String s = "";

            try {
                int c;



                while ((c = rdr.read()) != '\n') {
                    sb.append((char) c);
                    s = sb.toString();
                }
            } catch (IOException e) {
                System.exit(0);
                e.printStackTrace();
            }

            if (s == null) System.exit(0);
            String[] num = s.split("[^\\p{Digit}*]");
//        System.out.println(Arrays.toString(num));
            if (num.length != 4) System.exit(0);

            try {
                n = Integer.parseInt(num[0]);
                m = Integer.parseInt(num[1]);
                a = Integer.parseInt(num[2]);
                b = Integer.parseInt(num[3]);
            } catch(Exception e) {System.exit(0);}
            if (n<1 || m<1 || n>300 || m>300 || a>1000 || b>1000 || a<0 || b<0) System.exit(0);
            char[][] chars = new char[n][m];
            s = "";
            char x = '*';

            try {

                int c;
                for (int i = 0; i < chars.length; ++i) {
                    sb.delete(0, sb.length());
                    int l = 0;
                    while ((c = rdr.read()) != -1) {
                        if ((char) c == '\n') break;
                        if (((char) c == x || (char) c == '.') && l<m) {
                            sb.append((char) c);
                            l++;
                        }
                    }
                    if (l < m) for (int j = 0; j < m-l; j++) sb.append('.');
                    s = sb.toString();
                    chars[i] = s.toCharArray();
                }
                rdr.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (rdr != null) {
                    try {
                        rdr.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            rdr.close();
            int n1 = m*n;
            int n2 = n1;
//            List<Integer>[] g = new List[n1];
            for(int idx = 0; idx < n1; idx++)
            {
                array_v.add(idx);
                test_graph.put(idx, new ArrayList<Integer>());
            }
//            for (int i = 0; i < n1; i++) {
//                g[i] = new ArrayList<Integer>();
//            }
            int o = 0, w = 0;
            for (int i = 0; i < chars.length; ++i) {
                for (int j = 0; j < chars[i].length; ++j) {
                    char c = chars[i][j];
                    if (c == x) {
                        w++;
                        if (j > 0 && chars[i][j-1] == x) test_graph.get(o).add(o - 1);
                        if (j <= chars[i].length-2 && chars[i][j+1] == x) test_graph.get(o).add(o + 1);
                        if (i > 0 && chars[i-1][j] == x) test_graph.get(o).add(o - m);
                        if (i <= chars.length-2 && chars[i+1][j] == x) test_graph.get(o).add(o + m);
                    }
                    o++;
                }
            }
            HashMap result = findMaximumMatching(test_graph, array_v, false);
//            System.out.println(result.matching.size()/2);
//            System.out.println(result.unmatched);
//            System.out.println(test_graph.toString());
//            System.out.println(result.perfect_matching);
            int d = result.size()/2;
            if (2*b>a) System.out.print(d*a+(w-d*2)*b);
            else System.out.print(w * b);
        } catch (StackOverflowError e) {
            System.err.println("reported recursion level was "+e.getStackTrace().length);
            System.exit(0);
        }
        catch (IOException e) {
            System.exit(0);
        }

    }



    static class ContainerHelpers {
        static final boolean[] EMPTY_BOOLEANS = new boolean[0];
        static final int[] EMPTY_INTS = new int[0];
        static final long[] EMPTY_LONGS = new long[0];
        static final Object[] EMPTY_OBJECTS = new Object[0];

        // This is Arrays.binarySearch(), but doesn't do any argument validation.
        static int binarySearch(int[] array, int size, int value) {
            int lo = 0;
            int hi = size - 1;

            while (lo <= hi) {
                final int mid = (lo + hi) >>> 1;
                final int midVal = array[mid];

                if (midVal < value) {
                    lo = mid + 1;
                } else if (midVal > value) {
                    hi = mid - 1;
                } else {
                    return mid;  // value found
                }
            }
            return ~lo;  // value not present
        }

        static int binarySearch(long[] array, int size, long value) {
            int lo = 0;
            int hi = size - 1;

            while (lo <= hi) {
                final int mid = (lo + hi) >>> 1;
                final long midVal = array[mid];

                if (midVal < value) {
                    lo = mid + 1;
                } else if (midVal > value) {
                    hi = mid - 1;
                } else {
                    return mid;  // value found
                }
            }
            return ~lo;  // value not present
        }
    }

    public static class SparseIntArray implements Cloneable {
        private int[] mKeys;
        private int[] mValues;
        private int mSize;

        /**
         * Creates a new SparseIntArray containing no mappings.
         */
        public SparseIntArray() {
            this(10);
        }

        /**
         * Creates a new SparseIntArray containing no mappings that will not
         * require any additional memory allocation to store the specified
         * number of mappings.  If you supply an initial capacity of 0, the
         * sparse array will be initialized with a light-weight representation
         * not requiring any additional array allocations.
         */
        public SparseIntArray(int initialCapacity) {
            if (initialCapacity == 0) {
                mKeys = ContainerHelpers.EMPTY_INTS;
                mValues = ContainerHelpers.EMPTY_INTS;
            } else {
                initialCapacity = ArrayUtils.idealIntArraySize(initialCapacity);
                mKeys = new int[initialCapacity];
                mValues = new int[initialCapacity];
            }
            mSize = 0;
        }

        @Override
        public SparseIntArray clone() {
            SparseIntArray clone = null;
            try {
                clone = (SparseIntArray) super.clone();
                clone.mKeys = mKeys.clone();
                clone.mValues = mValues.clone();
            } catch (CloneNotSupportedException cnse) {
            /* ignore */
            }
            return clone;
        }

        /**
         * Gets the int mapped from the specified key, or <code>0</code>
         * if no such mapping has been made.
         */
        public int get(int key) {
            return get(key, 0);
        }

        /**
         * Gets the int mapped from the specified key, or the specified value
         * if no such mapping has been made.
         */
        public int get(int key, int valueIfKeyNotFound) {
            int i = ContainerHelpers.binarySearch(mKeys, mSize, key);

            if (i < 0) {
                return valueIfKeyNotFound;
            } else {
                return mValues[i];
            }
        }

        /**
         * Removes the mapping from the specified key, if there was any.
         */
        public void delete(int key) {
            int i = ContainerHelpers.binarySearch(mKeys, mSize, key);

            if (i >= 0) {
                removeAt(i);
            }
        }

        /**
         * Removes the mapping at the given index.
         */
        public void removeAt(int index) {
            System.arraycopy(mKeys, index + 1, mKeys, index, mSize - (index + 1));
            System.arraycopy(mValues, index + 1, mValues, index, mSize - (index + 1));
            mSize--;
        }

        /**
         * Adds a mapping from the specified key to the specified value,
         * replacing the previous mapping from the specified key if there
         * was one.
         */
        public void put(int key, int value) {
            int i = ContainerHelpers.binarySearch(mKeys, mSize, key);

            if (i >= 0) {
                mValues[i] = value;
            } else {
                i = ~i;

                if (mSize >= mKeys.length) {
                    int n = ArrayUtils.idealIntArraySize(mSize + 1);

                    int[] nkeys = new int[n];
                    int[] nvalues = new int[n];

                    // Log.e("SparseIntArray", "grow " + mKeys.length + " to " + n);
                    System.arraycopy(mKeys, 0, nkeys, 0, mKeys.length);
                    System.arraycopy(mValues, 0, nvalues, 0, mValues.length);

                    mKeys = nkeys;
                    mValues = nvalues;
                }

                if (mSize - i != 0) {
                    // Log.e("SparseIntArray", "move " + (mSize - i));
                    System.arraycopy(mKeys, i, mKeys, i + 1, mSize - i);
                    System.arraycopy(mValues, i, mValues, i + 1, mSize - i);
                }

                mKeys[i] = key;
                mValues[i] = value;
                mSize++;
            }
        }

        /**
         * Returns the number of key-value mappings that this SparseIntArray
         * currently stores.
         */
        public int size() {
            return mSize;
        }

        /**
         * Given an index in the range <code>0...size()-1</code>, returns
         * the key from the <code>index</code>th key-value mapping that this
         * SparseIntArray stores.
         *
         * <p>The keys corresponding to indices in ascending order are guaranteed to
         * be in ascending order, e.g., <code>keyAt(0)</code> will return the
         * smallest key and <code>keyAt(size()-1)</code> will return the largest
         * key.</p>
         */
        public int keyAt(int index) {
            return mKeys[index];
        }

        /**
         * Given an index in the range <code>0...size()-1</code>, returns
         * the value from the <code>index</code>th key-value mapping that this
         * SparseIntArray stores.
         *
         * <p>The values corresponding to indices in ascending order are guaranteed
         * to be associated with keys in ascending order, e.g.,
         * <code>valueAt(0)</code> will return the value associated with the
         * smallest key and <code>valueAt(size()-1)</code> will return the value
         * associated with the largest key.</p>
         */
        public int valueAt(int index) {
            return mValues[index];
        }

        /**
         * Returns the index for which {@link #keyAt} would return the
         * specified key, or a negative number if the specified
         * key is not mapped.
         */
        public int indexOfKey(int key) {
            return ContainerHelpers.binarySearch(mKeys, mSize, key);
        }

        /**
         * Returns an index for which {@link #valueAt} would return the
         * specified key, or a negative number if no keys map to the
         * specified value.
         * Beware that this is a linear search, unlike lookups by key,
         * and that multiple keys can map to the same value and this will
         * find only one of them.
         */
        public int indexOfValue(int value) {
            for (int i = 0; i < mSize; i++)
                if (mValues[i] == value)
                    return i;

            return -1;
        }

        /**
         * Removes all key-value mappings from this SparseIntArray.
         */
        public void clear() {
            mSize = 0;
        }

        /**
         * Puts a key/value pair into the array, optimizing for the case where
         * the key is greater than all existing keys in the array.
         */
        public void append(int key, int value) {
            if (mSize != 0 && key <= mKeys[mSize - 1]) {
                put(key, value);
                return;
            }

            int pos = mSize;
            if (pos >= mKeys.length) {
                int n = ArrayUtils.idealIntArraySize(pos + 1);

                int[] nkeys = new int[n];
                int[] nvalues = new int[n];

                // Log.e("SparseIntArray", "grow " + mKeys.length + " to " + n);
                System.arraycopy(mKeys, 0, nkeys, 0, mKeys.length);
                System.arraycopy(mValues, 0, nvalues, 0, mValues.length);

                mKeys = nkeys;
                mValues = nvalues;
            }

            mKeys[pos] = key;
            mValues[pos] = value;
            mSize = pos + 1;
        }

        /**
         * {@inheritDoc}
         *
         * <p>This implementation composes a string by iterating over its mappings.
         */
        @Override
        public String toString() {
            if (size() <= 0) {
                return "{}";
            }

            StringBuilder buffer = new StringBuilder(mSize * 28);
            buffer.append('{');
            for (int i=0; i<mSize; i++) {
                if (i > 0) {
                    buffer.append(", ");
                }
                int key = keyAt(i);
                buffer.append(key);
                buffer.append('=');
                int value = valueAt(i);
                buffer.append(value);
            }
            buffer.append('}');
            return buffer.toString();
        }
    }

    public static class ArrayUtils
    {
        private static Object[] EMPTY = new Object[0];
        private static final int CACHE_SIZE = 73;
        private static Object[] sCache = new Object[CACHE_SIZE];

        private ArrayUtils() { /* cannot be instantiated */ }

        public static int idealByteArraySize(int need) {
            for (int i = 4; i < 32; i++)
                if (need <= (1 << i) - 12)
                    return (1 << i) - 12;

            return need;
        }

        public static int idealBooleanArraySize(int need) {
            return idealByteArraySize(need);
        }

        public static int idealShortArraySize(int need) {
            return idealByteArraySize(need * 2) / 2;
        }

        public static int idealCharArraySize(int need) {
            return idealByteArraySize(need * 2) / 2;
        }

        public static int idealIntArraySize(int need) {
            return idealByteArraySize(need * 4) / 4;
        }

        public static int idealFloatArraySize(int need) {
            return idealByteArraySize(need * 4) / 4;
        }

        public static int idealObjectArraySize(int need) {
            return idealByteArraySize(need * 4) / 4;
        }

        public static int idealLongArraySize(int need) {
            return idealByteArraySize(need * 8) / 8;
        }

        /**
         * Checks if the beginnings of two byte arrays are equal.
         *
         * @param array1 the first byte array
         * @param array2 the second byte array
         * @param length the number of bytes to check
         * @return true if they're equal, false otherwise
         */
        public static boolean equals(byte[] array1, byte[] array2, int length) {
            if (length < 0) {
                throw new IllegalArgumentException();
            }

            if (array1 == array2) {
                return true;
            }
            if (array1 == null || array2 == null || array1.length < length || array2.length < length) {
                return false;
            }
            for (int i = 0; i < length; i++) {
                if (array1[i] != array2[i]) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Returns an empty array of the specified type.  The intent is that
         * it will return the same empty array every time to avoid reallocation,
         * although this is not guaranteed.
         */
        public static <T> T[] emptyArray(Class<T> kind) {
            if (kind == Object.class) {
                return (T[]) EMPTY;
            }

            int bucket = ((System.identityHashCode(kind) / 8) & 0x7FFFFFFF) % CACHE_SIZE;
            Object cache = sCache[bucket];

            if (cache == null || cache.getClass().getComponentType() != kind) {
                cache = Array.newInstance(kind, 0);
                sCache[bucket] = cache;

                // Log.e("cache", "new empty " + kind.getName() + " at " + bucket);
            }

            return (T[]) cache;
        }

        /**
         * Checks that value is present as at least one of the elements of the array.
         * @param array the array to check in
         * @param value the value to check for
         * @return true if the value is present in the array
         */
        public static <T> boolean contains(T[] array, T value) {
            return indexOf(array, value) != -1;
        }

        /**
         * Return first index of {@code value} in {@code array}, or {@code -1} if
         * not found.
         */
        public static <T> int indexOf(T[] array, T value) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == null) {
                    if (value == null) return i;
                } else {
                    if (value != null && array[i].equals(value)) return i;
                }
            }
            return -1;
        }

        /**
         * Test if all {@code check} items are contained in {@code array}.
         */
        public static <T> boolean containsAll(T[] array, T[] check) {
            for (T checkItem : check) {
                if (!contains(array, checkItem)) {
                    return false;
                }
            }
            return true;
        }

        public static boolean contains(int[] array, int value) {
            for (int element : array) {
                if (element == value) {
                    return true;
                }
            }
            return false;
        }

        public static long total(long[] array) {
            long total = 0;
            for (long value : array) {
                total += value;
            }
            return total;
        }

        /**
         * Appends an element to a copy of the array and returns the copy.
         * @param array The original array, or null to represent an empty array.
         * @param element The element to add.
         * @return A new array that contains all of the elements of the original array
         * with the specified element added at the end.
         */
        @SuppressWarnings("unchecked")
        public static <T> T[] appendElement(Class<T> kind, T[] array, T element) {
            final T[] result;
            final int end;
            if (array != null) {
                end = array.length;
                result = (T[])Array.newInstance(kind, end + 1);
                System.arraycopy(array, 0, result, 0, end);
            } else {
                end = 0;
                result = (T[])Array.newInstance(kind, 1);
            }
            result[end] = element;
            return result;
        }

        /**
         * Removes an element from a copy of the array and returns the copy.
         * If the element is not present, then the original array is returned unmodified.
         * @param array The original array, or null to represent an empty array.
         * @param element The element to remove.
         * @return A new array that contains all of the elements of the original array
         * except the first copy of the specified element removed.  If the specified element
         * was not present, then returns the original array.  Returns null if the result
         * would be an empty array.
         */
        @SuppressWarnings("unchecked")
        public static <T> T[] removeElement(Class<T> kind, T[] array, T element) {
            if (array != null) {
                final int length = array.length;
                for (int i = 0; i < length; i++) {
                    if (array[i] == element) {
                        if (length == 1) {
                            return null;
                        }
                        T[] result = (T[])Array.newInstance(kind, length - 1);
                        System.arraycopy(array, 0, result, 0, i);
                        System.arraycopy(array, i + 1, result, i, length - i - 1);
                        return result;
                    }
                }
            }
            return array;
        }

        public static int[] appendInt(int[] cur, int val) {
            if (cur == null) {
                return new int[] { val };
            }
            final int N = cur.length;
            for (int i = 0; i < N; i++) {
                if (cur[i] == val) {
                    return cur;
                }
            }
            int[] ret = new int[N + 1];
            System.arraycopy(cur, 0, ret, 0, N);
            ret[N] = val;
            return ret;
        }

        public static int[] removeInt(int[] cur, int val) {
            if (cur == null) {
                return null;
            }
            final int N = cur.length;
            for (int i = 0; i < N; i++) {
                if (cur[i] == val) {
                    int[] ret = new int[N - 1];
                    if (i > 0) {
                        System.arraycopy(cur, 0, ret, 0, i);
                    }
                    if (i < (N - 1)) {
                        System.arraycopy(cur, i + 1, ret, i, N - i - 1);
                    }
                    return ret;
                }
            }
            return cur;
        }
    }

}