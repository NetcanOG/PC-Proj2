package pc.cdl;

import pc.util.D;

class CountDownLatch {
  protected int value;

  CountDownLatch(int value) {
    if (value <= 0) {
      throw new IllegalArgumentException();
    }
    this.value = value;
  }

  int getCount() {
    return value;
  }

  synchronized void countDown() {
    if (value > 0 && --value == 0)
      notifyAll();
    D.print("%d", value);
  }

  synchronized void await() throws InterruptedException {
    D.print("wait %d", value);
    while (value > 0) {
      wait();
    }
    D.print("done");
  }
}

class CountDownLatch_Bug1 extends CountDownLatch {
  CountDownLatch_Bug1(int N) {
    super(N);
  }

  synchronized void countDown() {
    if (value > 0 && --value == 0)
      notify(); /* notifyAll() */
    D.print("%d", value);
  }
}

class CountDownLatch_Bug2 extends CountDownLatch {
  CountDownLatch_Bug2(int N) {
    super(N);
  }

  /* synchronized */ void countDown() {

    if (value > 0 && --value == 0)
      synchronized (this) {
        notifyAll();
      }
    D.print("%d", value);
  }
}

class CountDownLatch_Bug3 extends CountDownLatch {
  CountDownLatch_Bug3(int N) {
    super(N);
  }

  /* synchronized */ void await() throws InterruptedException {
    D.print("wait %d", value);
    while (value > 0)
      synchronized (this) {
        wait();
      }
    D.print("done");
  }
}

class CountDownLatch_Bug4 extends CountDownLatch {
  CountDownLatch_Bug4(int N) {
    super(N);
  }

  /* synchronized */ void await() throws InterruptedException {
    while (true) {
      boolean b;
      D.print("wait %d", value);
      synchronized (this) {
        b = (value > 0);
      }
      synchronized (this) {
        if (b)
          wait();
        else
          break;
      }
    }
    D.print("done");
  }
}
