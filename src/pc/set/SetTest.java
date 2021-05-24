package pc.set;

import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class SetTest {
  public static void main(String[] args) throws Exception {
    new SetTest(args);
  }
  final CyclicBarrier barrier;
  final int T, N, OPS; 
  final ISet<Integer> set;
  final Object PRINT_LOCK = new Object();
  final AtomicInteger errors = new AtomicInteger();
  final AtomicInteger expectedSetSize = new AtomicInteger();

  @SuppressWarnings("unchecked")
  SetTest(String[] args) throws Exception {
    // Define the set
    Class<?> clazz = args.length < 1 ? LHashSet0.class : Class.forName(args[0]);
    set = (ISet<Integer>) clazz.newInstance();
    // Number of threads
    T = args.length < 2 ? 8 : Integer.parseInt(args[1]);
    // Number of elements in set per thread
    N = 16;
    // Number of operations per thread
    OPS = 1000;

    barrier = new CyclicBarrier(T + 1);
    for (int i = 0; i < T; i++) {
      final int id = i;
      new Thread(() -> run(id)).start();
    }
    barrier.await(); // sync on start
    barrier.await(); // sync before verification
    barrier.await(); // sync at the end
    if (expectedSetSize.get() != set.size()) {
      System.out.printf("Expected set size: %d, reported size is %d!%n", expectedSetSize.get(), set.size());
      errors.incrementAndGet();
    }
    if (errors.get() == 0) {
      System.out.println("all seems ok :)");
    } else {
      System.out.println("There were errors :(");
    }
  }
  
  void run(int id) {
    try {
      barrier.await(); 
      java.util.Set<Integer> mySet = new java.util.TreeSet<>();
      int min = id * N;
      int max = min + N;
      Random rng = new Random(id);
      for (int i = 0; i < OPS; i++) {
        int v = min + rng.nextInt(max - min);
        switch(rng.nextInt(10)) {
          case 0: 
            set.add(v); mySet.add(v); break;
          case 1: 
            set.remove(v); mySet.remove(v); break;   
          default:
            set.contains(v); break; 
        }
      }
      barrier.await(); 
      expectedSetSize.getAndAdd(mySet.size());
      synchronized(PRINT_LOCK) {
        for (int i = min; i < max; i++) {
          if (mySet.contains(i) != set.contains(i)) {
            System.out.printf("thread %d: test failed for value %d%n", id, i);
            System.out.printf("thread %d: expects %s%n", id, mySet.toString());
            errors.incrementAndGet();
            break;
          }
        }
      }
      barrier.await();
    } 
    catch(Exception e) {
      throw new RuntimeException(e);
    }
  }
}
