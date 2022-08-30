package org.opensrp.util;

import static org.mockito.ArgumentMatchers.argThat;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.mockito.ArgumentMatcher;

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
