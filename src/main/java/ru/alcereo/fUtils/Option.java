package ru.alcereo.fUtils;


/**
 * Монада для хранения значений
 * @param <T>
 */
public abstract class Option<T> {

    public static final None NONE = new None();

    public abstract <R, E extends Throwable> Option<R> map(Func<T,R,E> func) throws E;

    public abstract <R, E extends Throwable> Option<R> flatMap(Func<T,Option<R>,E> func) throws E;

    public abstract <E extends Throwable> Option<T> filter(Func<T, Boolean, E> filterPredicate) throws E;

    public abstract T getOrElse(T valueElse);

    static <R> Option<R> some(R value){
        if (value == null)
            return none();
        else
            return new Some<>(value);
    }

    static <R> Option<R> none(){
        return NONE;
    }

    public static <R> Option<R> asOption(R value){
        if (value == null)
            return NONE;
        else
            return some(value);
    }

}
