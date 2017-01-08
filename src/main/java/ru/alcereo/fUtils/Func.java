package ru.alcereo.fUtils;


public interface Func<T,R, E extends Exception>{
    R execute(T value) throws E;
}
