package ru.alcereo.fUtils;

/**
 * Created by alcereo on 08.01.17.
 */
@FunctionalInterface
public interface Exceptioned<E extends Throwable> {
        E getNewException(Throwable cause);
}
