package com.travelGraph.helpers;

import java.util.function.Consumer;

public class UpdateValueHelper<T> {
    public static <T> void updateIfNotNull(Consumer<T> setter, T newValue) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }
}
