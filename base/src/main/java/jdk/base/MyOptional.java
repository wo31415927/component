package jdk.base;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Administrator 2018/12/8/008
 */
public class MyOptional {
    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Person {
        private Optional<Car> car;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Car {
        private Optional<Insurance> insurance;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Insurance {
        private String name;
    }
}
