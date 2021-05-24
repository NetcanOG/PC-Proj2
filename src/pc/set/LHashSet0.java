package pc.set;

import java.util.LinkedList;

/**
 * 
 * Non-concurrent hash set implementation.
 *
 */
public class LHashSet0<E> implements ISet<E>{

  private static final int NUMBER_OF_BUCKETS = 16; // should not be changed 
  private LinkedList<E>[] table;
  private int size;

  /**
   * Constructor.
   */
  @SuppressWarnings("unchecked")
  public LHashSet0() {
    table = (LinkedList<E>[]) new LinkedList[NUMBER_OF_BUCKETS];
    for (int i = 0; i < table.length; i++) {
      table[i] = new LinkedList<>();
    }
    size = 0;
  }

  @Override
  public int size() {
    return size;
  }

  private LinkedList<E> getEntry(E elem) {
    return table[Math.abs(elem.hashCode() % table.length)];
  }

  @Override
  public synchronized boolean add(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    LinkedList<E> list = getEntry(elem);
    boolean r = ! list.contains(elem);
    if (r) {
      list.addFirst(elem);
      size++;
    }
    return r;
  }

  @Override
  public synchronized boolean remove(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    boolean r = getEntry(elem).remove(elem);
    if (r) {
      size--;
    }
    return r;
  }

  @Override
  public synchronized boolean contains(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    return getEntry(elem).contains(elem);
  }
}
