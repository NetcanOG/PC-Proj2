package pc.set;

import java.util.Random;

import pc.util.Benchmark;

/**
 * Benchmark program for stack implementations.
 */
public class SetBenchmark {

  private static final int DURATION = 10;
  private static final int MAX_THREADS = 8;
  private static final int N = 256;
  
  @SuppressWarnings("unchecked")
  public static void main(String[] args) throws Exception {
    Class<?> setImplementation;
    if (args.length == 0)
      setImplementation = LHashSet0.class;
    else 
      setImplementation = Class.forName(args[0]);
    for (int n = 1; n <= MAX_THREADS; n = n * 2) {
      ISet<Integer> set = (ISet<Integer>) setImplementation.newInstance();
      runBenchmark(n, set);
    }
  }

  private static void runBenchmark(int threads, ISet<Integer> s) {
    for (int i = 0; i < N; i++) { 
      s.add(i); 
    }
    Benchmark b = new Benchmark(threads, DURATION, new SetOperation(s));
    System.out.printf("%d threads using %s ... ", threads, s.getClass().getSimpleName());
    System.out.printf("%.2f Mops/s%n", b.run());
  }

  private static class SetOperation implements Runnable {
    private final Random rng;
    private final ISet<Integer> set;

    SetOperation(ISet<Integer> s) {
      this.set = s;
      rng = new Random();
    }

    @Override
    public void run() {
      int op = rng.nextInt(10);
      int v = rng.nextInt(N);
      switch (op) {
        case 0:
          set.add(v);
          break;
        case 1:
          set.remove(v);
          break;
        default:
          set.contains(v);
          break;
      }
    }
  }
}


