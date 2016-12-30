package ru.alcereo.fUtils;


/**
 * Монада для хранения значений
 * @param <T>
 */
abstract class Option<T> {

    public static final None NONE = new None();

    public abstract <R> Option<R> map(Func<T,R> func);

    public abstract <R> Option<R> flatMap(Func<T,Option<R>> func);

    public abstract Option<T> filter(Func<T, Boolean> filterPredicate);

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
