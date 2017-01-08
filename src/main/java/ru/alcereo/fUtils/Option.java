package ru.alcereo.fUtils;


/**
 * Монада для хранения значений
 * @param <T>
 */
public abstract class Option<T, Es extends Exception> {

    public static final None NONE = new None();

    public abstract <R, E extends Exception> Option<R,E> map(Func<T,R,E> func);

    public abstract <R, E extends Exception> Option<R,E> flatMap(Func<T,Option<R,E>,E> func) throws E;

    public abstract <E extends Exception> Option<T,E> filter(Func<T, Boolean, E> filterPredicate) throws E;

    public abstract T getOrElse(T valueElse);

    static <R,E extends Exception> Option<R,E> some(R value){
        if (value == null)
            return none();
        else
            return new Some<>(value);
    }

    public static <R,E extends Exception> Option<R,E> exceptOpt(E e){
        return new ExcOpt<R,E>(e);
    }

    @SuppressWarnings("unchecked")
    static <R,E extends Exception> Option<R,E> none(){
        return NONE;
    }

    @SuppressWarnings("unchecked")
    public static <R, E extends Exception> Option<R,E> asOption(R value){
        if (value == null)
            return NONE;
        else
            return some(value);
    }

    public abstract Option<T,Es> _throwCausedException() throws Es;

    public abstract <W extends Throwable> Option<T,Es> _wrapAndTrowException(Exceptioned<W> exceptioned) throws W;

    public abstract <W extends Exception> Option<T,W> _wrapException(Exceptioned<W> exceptioned);

    public abstract boolean isException();

    public abstract String getExceptionMessage();

}
