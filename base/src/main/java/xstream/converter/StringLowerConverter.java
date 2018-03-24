package xstream.converter;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

public class StringLowerConverter extends AbstractSingleValueConverter {
    @Override
    public boolean canConvert(Class type) {
        return type.equals(String.class);
    }

    @Override
    public Object fromString(String str) {
        return str.trim().toLowerCase();
    }
}
