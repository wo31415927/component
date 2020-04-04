package jdk.collection;

/**
 * @author chenxiang 2019/6/24/024
 */
public class MyArrayQueue<E> {
    protected Object[] elements;
    protected int count;
    protected int putIndex;
    protected int takeIndex;

    public MyArrayQueue(int cap){
        assert(cap>=0);
        // 为什么不直接elements = new E[cap],因为java不支持直接定义泛型数组
        // https://www.zhihu.com/question/20928981
        elements = new Object[cap];

    }

    public boolean offer(E e){
        assert(null != e);
        if(elements.length == count){
            return false;
        }
        elements[putIndex++]=e;
        if(putIndex == elements.length){
            putIndex = 0;
        }
        count++;
        return true;
    }

    public E poll(){
        if(0 == count){
            return null;
        }
        E e = (E)elements[takeIndex];
        elements[takeIndex++] = null;
        if(takeIndex == elements.length){
            takeIndex = 0;
        }
        count--;
        return e;
    }
}
