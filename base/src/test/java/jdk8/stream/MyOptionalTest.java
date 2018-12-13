package jdk8.stream;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import java.util.Optional;

import json.JsonUtils;

/**
 * @author Administrator 2018/12/8/008
 */
public class MyOptionalTest {
    ObjectMapper objectMapper = new ObjectMapper();

    public String getCarInsuranceName(Optional<MyOptional.Person> person) {
        // Person::getCar返回的是Optional<Car>,如果使用person.map(MyOptional.Person::getCar)得到的结果是Optional<Optional<Car>>,无法进行一步getInsurance
        // 而flatMap则返回Optional<Car>
        // 如果某一个结果是Optional.empty(),则最终结果是"Unknown",当我们得到Unknown时，并不清楚那一层的对象不存在。
        // orElse()让我们避免了if(optional.isPresent())的额外判断
        return person.flatMap(MyOptional.Person::getCar)
                .flatMap(MyOptional.Car::getInsurance)
                .map(MyOptional.Insurance::getName)
                .orElse("Unknown");
    }

    @Test
    public void testOptionalJson() {
        Optional<MyOptional.Person> optionalPerson = Optional.of(new MyOptional.Person().setCar(Optional.of(new MyOptional.Car().setInsurance
                (Optional.of(new MyOptional.Insurance().setName("Hello"))))));
        // isPresent()被序列化为{present:true}，没有getValue()方法，只有get()方法，value值不会被序列化到json中
        // 所以Optional不能作为类字段
        System.out.println(JsonUtils.toJsonStr(optionalPerson, objectMapper));
    }
}