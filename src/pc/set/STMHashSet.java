package pc.set;

import scala.concurrent.stm.Ref;
import scala.concurrent.stm.TArray;
import scala.concurrent.stm.japi.STM;

/**
 * 
 * Non-concurrent hash set implementation.
 *
 */
public class STMHashSet<E> implements ISet<E>{

  private static class Node<T> {
    T value;
    Ref.View<Node<T>> prev = STM.newRef(null);
    Ref.View<Node<T>> next = STM.newRef(null);
  }

  private static final int NUMBER_OF_BUCKETS = 16; // should not be changed 
  private final TArray.View<Node<E>> table;
  private final Ref.View<Integer> size;

  /**
   * Constructor.
   */
  public STMHashSet() {
    table = STM.newTArray(NUMBER_OF_BUCKETS);
    size = STM.newRef(0); 
  }

  @Override
  public int size() {
    return size.get();
  }

  @Override
  public boolean add(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    // TODO
    throw new Error("not implemented");
  }

  @Override
  public boolean remove(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    // TODO
    throw new Error("not implemented");
  }

  @Override
  public boolean contains(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    // TODO
    throw new Error("not implemented");
  }
}
