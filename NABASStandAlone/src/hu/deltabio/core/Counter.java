/*                                                                                                                                                  
NNNNNNNN        NNNNNNNN               AAA               BBBBBBBBBBBBBBBBB               AAA                 SSSSSSSSSSSSSSS                      
N:::::::N       N::::::N              A:::A              B::::::::::::::::B             A:::A              SS:::::::::::::::S                     
N::::::::N      N::::::N             A:::::A             B::::::BBBBBB:::::B           A:::::A            S:::::SSSSSS::::::S                     
N:::::::::N     N::::::N            A:::::::A            BB:::::B     B:::::B         A:::::::A           S:::::S     SSSSSSS       +++++++       
N::::::::::N    N::::::N           A:::::::::A             B::::B     B:::::B        A:::::::::A          S:::::S                   +:::::+       
N:::::::::::N   N::::::N          A:::::A:::::A            B::::B     B:::::B       A:::::A:::::A         S:::::S                   +:::::+       
N:::::::N::::N  N::::::N         A:::::A A:::::A           B::::BBBBBB:::::B       A:::::A A:::::A         S::::SSSS          +++++++:::::+++++++ 
N::::::N N::::N N::::::N        A:::::A   A:::::A          B:::::::::::::BB       A:::::A   A:::::A         SS::::::SSSSS     +:::::::::::::::::+ 
N::::::N  N::::N:::::::N       A:::::A     A:::::A         B::::BBBBBB:::::B     A:::::A     A:::::A          SSS::::::::SS   +:::::::::::::::::+ 
N::::::N   N:::::::::::N      A:::::AAAAAAAAA:::::A        B::::B     B:::::B   A:::::AAAAAAAAA:::::A            SSSSSS::::S  +++++++:::::+++++++ 
N::::::N    N::::::::::N     A:::::::::::::::::::::A       B::::B     B:::::B  A:::::::::::::::::::::A                S:::::S       +:::::+       
N::::::N     N:::::::::N    A:::::AAAAAAAAAAAAA:::::A      B::::B     B:::::B A:::::AAAAAAAAAAAAA:::::A               S:::::S       +:::::+       
N::::::N      N::::::::N   A:::::A             A:::::A   BB:::::BBBBBB::::::BA:::::A             A:::::A  SSSSSSS     S:::::S       +++++++       
N::::::N       N:::::::N  A:::::A               A:::::A  B:::::::::::::::::BA:::::A               A:::::A S::::::SSSSSS:::::S                     
N::::::N        N::::::N A:::::A                 A:::::A B::::::::::::::::BA:::::A                 A:::::AS:::::::::::::::SS                      
NNNNNNNN         NNNNNNNAAAAAAA                   AAAAAAABBBBBBBBBBBBBBBBBAAAAAAA                   AAAAAAASSSSSSSSSSSSSSS                        
                                             developed by Gábor Jaksa and Bertalan Takács
 */
package hu.deltabio.core;

import hu.deltabio.core.Counter.MutableInteger;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author Gábor Jaksa
 * @param <T> Type of keys
 */
public class Counter<T> extends HashMap<T, MutableInteger> implements Comparator<T> {

    public void increment(T key) {
        if (containsKey(key)) {
            MutableInteger oldValue = get(key);
            oldValue.set(oldValue.get() + 1);
        } else {
            put(key, new MutableInteger(1));
        }
    }

    public void increment(T key, int count) {
        if (containsKey(key)) {
            MutableInteger oldValue = get(key);
            oldValue.set(oldValue.get() + count);
        } else {
            put(key, new MutableInteger(count));
        }
    }

    public int value(T key) {
        return get(key).get();
    }

    public int value0(T key) {
        if (!containsKey(key)) {
            return 0;
        }
        return get(key).get();
    }

    @Override
    public int compare(T o1, T o2) {
        return Integer.compare(value0(o2), value0(o1));
    }

    public ArrayList<T> getSortedKeys() {
        ArrayList<T> keys = new ArrayList<>(keySet());
        Collections.sort(keys, this);
        return keys;
    }
    
    public ArrayList<T> getSortedKeys(int top, boolean reverse) {
        ArrayList<T> keys = new ArrayList<>(keySet());
        Collections.sort(keys, this);
        if(reverse) Collections.reverse(keys);
        return new ArrayList<>(keys.subList(0, Math.min(top,keys.size())));
    }
    
    public long sum() {
        long sum = 0;
        for(MutableInteger i: this.values()) {
            sum+= i.get();
        }
        return sum;
    }
    


    class MutableInteger {

        private int val;

        public MutableInteger(int val) {
            this.val = val;
        }

        public int get() {
            return val;
        }

        public void set(int val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return Integer.toString(val);
        }
    }
}
