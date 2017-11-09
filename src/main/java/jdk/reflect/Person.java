package jdk.reflect;

import lombok.Getter;
import lombok.Setter;

/**
 * zeyu
 * 2017/11/9
 */
@Getter
@Setter
public class Person implements IPerson{
    public Person(String name, int gender) {
        this.name = name;
        this.gender = gender;
    }

    protected String name;
    /**
     * 性别
     */
    protected int gender;
    /**
     * 鲜花,有人喜欢就送一支
     */
    protected int flower;
    /**
     * 鸡蛋,不喜欢就扔一个
     */
    protected int egg;
}
