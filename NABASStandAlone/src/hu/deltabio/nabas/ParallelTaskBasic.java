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
package hu.deltabio.nabas;


/**
 *
 * @author Gábor Jaksa
 */
public abstract class ParallelTaskBasic {

    int maxThread;
    int process = 0;
    public final Object lock = new Object();
    public String mainThreadName;
    public volatile boolean paused = false;

    public int processPercent = 0;

    Thread threads[];
    public boolean interrupted = false;
    public boolean wait = true;

    public ParallelTaskBasic() {
        maxThread = Runtime.getRuntime().availableProcessors();
        startProcess();
    }

    public ParallelTaskBasic(int threads) {
        maxThread = threads;
        startProcess();
    }

    public ParallelTaskBasic(int threads, boolean wait) {
        maxThread = threads;
        this.wait = wait;
        startProcess();
    }

    public ParallelTaskBasic(int threads, boolean wait, boolean start) {
        maxThread = threads;
        this.wait = wait;
        if (start) {
            startProcess();
        }
    }

    public boolean inMainThread() {
        return Thread.currentThread().getName().equals(mainThreadName);
    }

    public final void startProcess() {
        if (!interrupted) {

            before();
            runAction();
            try {
                done();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void pause(boolean paused) {
        this.paused = paused;
    }

    public void pause() {
        if (paused) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public final void runAction() {
        threads = new Thread[maxThread];
        for (int i = 0; i < maxThread; i++) {
            threads[i] = new Thread(workingThread());
            mainThreadName = threads[0].getName();
            threads[i].start();
        }
        if (wait) {
            for (Thread s : threads) {
                try {
                    s.join();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                done();
            }
        }
        allDone();

    }

    public void interruptThreads() {
        interrupted = true;
        for (Thread t : threads) {
            t.interrupt();
        }
    }

    public void before() {
    }

    public abstract Runnable workingThread();

    public void done() {
    }

    public void allDone() {
    }

}
