package ru.alcereo.fUtils;


public interface Func<T,R, E extends Throwable>{
    R execute(T value) throws E;
}
