package pc.util;

public class D {

  public static void print(String message, Object... args) {
    if (DEBUG) {
      synchronized (LOCK) {
        System.out.printf("[%s] ", Thread.currentThread().getName());
        System.out.printf(message, args);
        System.out.println();
      }
    }
  }

  public static void enable() {
    DEBUG = true;
  }

  public static void disable() {
    DEBUG = true;
  }

  private static boolean      DEBUG = System.getProperty("debug") != null;
  private static final Object LOCK  = new Object();

  private D() {
  }

}
