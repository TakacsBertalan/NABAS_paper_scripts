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

import hu.deltabio.core.LongCounter.MutableLong;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author Gábor Jaksa
 * @param <T> Type of keys
 */
public class LongCounter<T> extends HashMap<T, MutableLong> implements Comparator<T>, Serializable {

    public void increment(T key) {
        if (containsKey(key)) {
            MutableLong oldValue = get(key);
            oldValue.set(oldValue.get() + 1);
        } else {
            put(key, new MutableLong(1));
        }
    }

    public void increment(T key, long count) {
        if (containsKey(key)) {
            MutableLong oldValue = get(key);
            oldValue.set(oldValue.get() + count);
        } else {
            put(key, new MutableLong(count));
        }
    }

    public ArrayList<T> getSortedKeys() {
        ArrayList<T> keys = new ArrayList<>(keySet());
        Collections.sort(keys, this);
        return keys;
    }
    
    public long sum() {
        int sum = 0;
        for(LongCounter.MutableLong i: this.values()) {
            sum+= i.get();
        }
        return sum;
    }
    

    @Override
    public int compare(T o1, T o2) {
        return Long.compare(value0(o2), value0(o1));
    }

    public long value(T key) {
        return get(key).get();
    }

    public long value0(T key) {
        if (!containsKey(key)) {
            return 0;
        }
        return get(key).get();
    }

    class MutableLong implements Serializable{

        private long val;

        public MutableLong(long val) {
            this.val = val;
        }

        public long get() {
            return val;
        }

        public void set(long val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return Long.toString(val);
        }
    }
}
