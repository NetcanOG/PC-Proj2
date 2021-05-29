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
  private final TArray.View<Node<E>> table; //reference to the first node
  private final Ref.View<Integer> size;
  
  /**
  * Constructor.
  */
  public STMHashSet() {
    table = STM.newTArray(NUMBER_OF_BUCKETS);
    size = STM.newRef(0); 
  }
  
  private Node<E> getEntry(E elem) {
    return table.apply(getEntryMath(elem));
  }
  
  private int getEntryMath(E elem){
    return Math.abs(elem.hashCode() % table.length());
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
    
    return STM.atomic(() ->{
      if(contains(elem)){
        return false;
      }
      Node<E> curNode = getEntry(elem);
      Node<E> newNode = new Node<E>();
      newNode.value = elem;
      if(curNode != null){
        curNode.prev.set(newNode);
      }
      newNode.next.set(curNode);
      table.update(getEntryMath(elem), newNode);
      STM.increment(size, 1);
      return true;
    });
  }
  
  @Override
  public boolean remove(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    
    return STM.atomic(() ->{
      Node<E> curNode = getEntry(elem);
      while(curNode != null){
        if(elem.equals(curNode.value)){
          Node<E> prevNode = curNode.prev.get();
          Node<E> nextNode = curNode.next.get();
          
          if(nextNode != null){
            nextNode.prev.set(prevNode);
          }
          if(prevNode != null){
            prevNode.next.set(nextNode);           
          }else{
            table.update(getEntryMath(elem), nextNode);
          }
          
          STM.increment(size, -1);
          return true;
        }
        curNode = curNode.next.get();
      }
      return false;
    });
  }
  
  @Override
  public boolean contains(E elem) {
    if (elem == null) {
      throw new IllegalArgumentException();
    }
    
    return STM.atomic(() ->{
      Node<E> curNode = getEntry(elem);
      while(curNode != null){
        if(elem.equals(curNode.value)){
          return true;
        }
        curNode = curNode.next.get();
      }
      return false;
    });
  }
}