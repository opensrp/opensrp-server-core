package org.opensrp.util;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.mockito.ArgumentMatcher;

import static org.mockito.ArgumentMatchers.argThat;

public class Matcher {

    public static <T> T objectWithSameFieldsAs(final T other) {
        return argThat(hasSameFieldsAs(other));
    }

    public static <T> ArgumentMatcher<T> hasSameFieldsAs(final T other) {
        return new ArgumentMatcher<T>() {

            @Override
            public boolean matches(Object o) {
                return EqualsBuilder.reflectionEquals(other, o);
            }

        };
    }
}
