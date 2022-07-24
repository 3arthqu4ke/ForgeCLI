package net.kunmc.lab.forgecli.pre1_13;

import java.util.function.Predicate;

/**
 * Handles {@link Predicate}s or {@link com.google.common.base.Predicate}s.
 *
 * @author 3arthqu4ke
 */
public class Predicates {
    public static Object getPredicate() throws ClassNotFoundException {
        if (getPredicateClass().getName().startsWith("com")) {
            return getGooglePredicate();
        }

        return getJavaPredicate();
    }

    public static Object getGooglePredicate() {
        return (com.google.common.base.Predicate<Object>) input -> true;
    }

    public static Object getJavaPredicate() {
        return (Predicate<Object>) o -> true;
    }

    public static Class<?> getPredicateClass() throws ClassNotFoundException {
        Class<?> predicate;
        try {
            predicate = Class.forName("com.google.common.base.Predicate");
        } catch (ClassNotFoundException e) {
            predicate = Class.forName("java.util.function.Predicate");
        }

        return predicate;
    }

}
