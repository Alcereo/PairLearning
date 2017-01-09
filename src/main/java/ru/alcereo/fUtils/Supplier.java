package ru.alcereo.fUtils;

@FunctionalInterface
public interface Supplier<T,E extends Exception> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    T get() throws E;

}
