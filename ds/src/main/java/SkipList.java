import java.util.concurrent.ThreadLocalRandom;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @seehttps://www.jianshu.com/p/60d2561b821c
 * @author chenxiang 2019/1/30
 */
public class SkipList<T> {
  private SkipListNode<T> head, tail;
  /** 节点总数 */
  private int nodes;
  /** 层数 */
  private int listLevel;
  /** 向上提升一个的概率，一般是0.5或者0.25 */
  private static final double PROBABILITY = 0.5;

  public SkipList() {
    clear();
  }
  /** 清空跳跃表 */
  public void clear() {
    head = new SkipListNode<T>(SkipListNode.HEAD_KEY, null);
    tail = new SkipListNode<T>(SkipListNode.TAIL_KEY, null);
    horizontalLink(head, tail);
    listLevel = 0;
    nodes = 0;
  }

  public boolean isEmpty() {
    return nodes == 0;
  }

  public int size() {
    return nodes;
  }
  /** 在最下面一层，找到要插入的位置前面的那个key */
  private SkipListNode<T> findNode(int key) {
    SkipListNode<T> p = head;
    while (true) {
      while (p.right.key != SkipListNode.TAIL_KEY && p.right.key <= key) {
        p = p.right;
      }
      if (p.down != null) {
        p = p.down;
      } else {
        break;
      }
    }
    return p;
  }
  /** 查找是否存储key，存在则返回该节点，否则返回null */
  public SkipListNode<T> search(int key) {
    SkipListNode<T> p = findNode(key);
    if (key == p.getKey()) {
      return p;
    } else {
      return null;
    }
  }
  /** 向跳跃表中添加key-value */
  public void put(int k, T v) {
    SkipListNode<T> p = findNode(k);
    // 如果key值相同，替换原来的vaule即可结束
    if (k == p.getKey()) {
      p.value = v;
      return;
    }
    SkipListNode<T> q = new SkipListNode<T>(k, v);
    backLink(p, q);
    // 当前所在的层级是0
    int currentLevel = 0;
    // 抛硬币
    while (ThreadLocalRandom.current().nextDouble() < PROBABILITY) {
      // 如果超出了高度，需要重新建一个顶层
      if (currentLevel >= listLevel) {
        listLevel++;
        SkipListNode<T> p1 = new SkipListNode<>(SkipListNode.HEAD_KEY, null);
        SkipListNode<T> p2 = new SkipListNode<>(SkipListNode.TAIL_KEY, null);
        horizontalLink(p1, p2);
        vertiacallLink(p1, head);
        vertiacallLink(p2, tail);
        head = p1;
        tail = p2;
      }
      // 将p移动到上一层
      while (p.up == null) {
        p = p.left;
      }
      p = p.up;
      // 只保存key就ok
      SkipListNode<T> e = new SkipListNode<T>(k, null);
      // 将e插入到p的后面
      backLink(p, e);
      // 将e和q上下连接
      vertiacallLink(e, q);
      q = e;
      currentLevel++;
    }
    // 层数递增
    nodes++;
  }
  /** node1后面插入node2 */
  private void backLink(SkipListNode<T> node1, SkipListNode<T> node2) {
    node2.left = node1;
    node2.right = node1.right;
    node1.right.left = node2;
    node1.right = node2;
  }
  /** 水平双向连接 */
  private void horizontalLink(SkipListNode<T> node1, SkipListNode<T> node2) {
    node1.right = node2;
    node2.left = node1;
  }
  /** 垂直双向连接 */
  private void vertiacallLink(SkipListNode<T> node1, SkipListNode<T> node2) {
    node1.down = node2;
    node2.up = node1;
  }
  /** 打印出原始数据 */
  @Override
  public String toString() {
    if (isEmpty()) {
      return "Empty SkipList.";
    }
    StringBuilder builder = new StringBuilder();
    SkipListNode<T> start = head;
    do {
      SkipListNode<T> p = start;
      while (p != null) {
        switch (p.getKey()) {
          case SkipListNode.HEAD_KEY:
            builder.append("HEAD");
            builder.append("->");
            break;
          case SkipListNode.TAIL_KEY:
            builder.append("TAIL");
            break;
          default:
            builder.append(p);
            builder.append("->");
        }
        p = p.right;
      }
      builder.append("\n");
    } while (null != (start = start.down));
    return builder.toString();
  }

  /**
   * 跳跃表的节点,包括key-value和上下左右4个指针 created by 曹艳丰，2016-08-14
   * 参考：http://www.acmerblog.com/skip-list-impl-java-5773.html
   */
  @NoArgsConstructor
  @Getter
  @Setter
  public class SkipListNode<T> {
    public int key;
    public T value;
    // 上下左右 四个指针
    public SkipListNode<T> up, down, left, right;

    public SkipListNode(int key, T value) {
      this.key = key;
      this.value = value;
    }

    public static final int HEAD_KEY = Integer.MIN_VALUE;
    public static final int TAIL_KEY = Integer.MAX_VALUE;

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null) {
        return false;
      }
      if (!(o instanceof SkipListNode)) {
        return false;
      }
      SkipListNode<T> ent;
      try {
        ent = (SkipListNode<T>) o;
      } catch (ClassCastException ex) {
        return false;
      }
      return (ent.getKey() == key) && (ent.getValue() == value);
    }

    @Override
    public String toString() {
      if (key == HEAD_KEY) {
        return "HEAD";
      }
      if (key == TAIL_KEY) {
        return "TAIL";
      }
      return "(" + key + "," + value + ")";
    }
  }
}
