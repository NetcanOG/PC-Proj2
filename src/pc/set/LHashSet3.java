package pc.set;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
* 
* Concurrent(3) hash set implementation.
*
*/
public class LHashSet3<E> implements ISet<E>{
  
  private static final int NUMBER_OF_BUCKETS = 16; // should not be changed 
  private LinkedList<E>[] table;
  final ReentrantReadWriteLock[] locks;
  /**
  * Constructor.
  */
  @SuppressWarnings("unchecked")
  public LHashSet3() {
    table = (LinkedList<E>[]) new LinkedList[NUMBER_OF_BUCKETS];
    locks = new ReentrantReadWriteLock[NUMBER_OF_BUCKETS];
    for (int i = 0; i < table.length; i++) {
      table[i] = new LinkedList<>();
      locks[i] = new ReentrantReadWriteLock();
    }
  }
  
  @Override
  public int size() {
    int sizeSum = 0;

    for(int i = 0; i < NUMBER_OF_BUCKETS; i++){
      locks[i].readLock().lock();
    }

    try{
      for(int i = 0; i < NUMBER_OF_BUCKETS; i++){
        sizeSum += table[i].size();
      }
      return sizeSum;
    }

    finally{
      for(int i = 0; i < NUMBER_OF_BUCKETS; i++){
        locks[i].readLock().unlock();
      }
    }
  }
  
  private LinkedList<E> getEntry(E elem) {
    return table[getEntryMath(elem)];
  }
  
  private int getEntryMath(E elem){
    return Math.abs(elem.hashCode() % table.length);
  }

  @Override
  public boolean add(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    
    locks[getEntryMath(elem)].writeLock().lock();

    try{
      LinkedList<E> list = getEntry(elem);
      boolean r = ! list.contains(elem);
      if (r) {
        list.addFirst(elem);
      }
      return r;
    }
    finally{
      locks[getEntryMath(elem)].writeLock().unlock();
    }
  }
  
  @Override
  public boolean remove(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }

    locks[getEntryMath(elem)].writeLock().lock();

    try{
      boolean r = getEntry(elem).remove(elem);
      return r;
    }
    finally{
      locks[getEntryMath(elem)].writeLock().unlock();
    }
  }
  
  @Override
  public boolean contains(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    
    locks[getEntryMath(elem)].readLock().lock();

    try{
      return getEntry(elem).contains(elem);
    }
    finally{
      locks[getEntryMath(elem)].readLock().unlock();
    }   
  }
}