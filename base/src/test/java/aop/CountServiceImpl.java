package aop;

/** @author chenxiang 2019/8/12/012 */
public class CountServiceImpl implements CountService {

  private int count = 0;

  public int count() {
    return count++;
  }

  public void clear(){
    count = 0;
  }
}
