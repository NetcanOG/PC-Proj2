package pc.set;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
* 
* Concurrent(2) hash set implementation.
*
*/
public class LHashSet2<E> implements ISet<E>{
  
  private static final int NUMBER_OF_BUCKETS = 16; // should not be changed 
  private LinkedList<E>[] table;
  private int size;
  final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
  
  /**
  * Constructor.
  */
  @SuppressWarnings("unchecked")
  public LHashSet2() {
    table = (LinkedList<E>[]) new LinkedList[NUMBER_OF_BUCKETS];
    for (int i = 0; i < table.length; i++) {
      table[i] = new LinkedList<>();
    }
    size = 0;
  }
  
  @Override
  public int size() {
    lock.readLock().lock();
    try{
      return size;
    }
    finally{
      lock.readLock().unlock();
    }
  }
  
  private LinkedList<E> getEntry(E elem) {
    return table[Math.abs(elem.hashCode() % table.length)];
  }
  
  @Override
  public boolean add(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    
    lock.writeLock().lock();

    try{
      LinkedList<E> list = getEntry(elem);
      boolean r = ! list.contains(elem);
      if (r) {
        list.addFirst(elem);
        size++;
      }
      return r;
    }
    finally{
      lock.writeLock().unlock();
    }
  }
  
  @Override
  public boolean remove(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }

    lock.writeLock().lock();

    try{
      boolean r = getEntry(elem).remove(elem);
      if (r) {
        size--;
      }
      return r;
    }
    finally{
      lock.writeLock().unlock();
    }
  }
  
  @Override
  public boolean contains(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    
    lock.readLock().lock();

    try{
      return getEntry(elem).contains(elem);
    }
    finally{
      lock.readLock().unlock();
    }   
  }
}