package ru.alcereo.fUtils;


/**
 * Монада для хранения значений
 * @param <T>
 */
public abstract class Option<T, Es extends Exception> {

    public static final None NONE = new None();

    public abstract <R, E extends Exception> Option<R,E>    map(Func<T,R,E> func);

    public abstract <R, E extends Exception> Option<R,E>    flatMap(Func<T,Option<R,E>,E> func);

    public abstract <E extends Exception> Option<T,E>       filter(Func<T, Boolean, E> filterPredicate);

    public abstract T getOrElse(T valueElse);

    public abstract Option<T,Es> throwException() throws Es;

    public abstract <W extends Throwable> Option<T,Es> wrapAndTrowException(Exceptioned<W> exceptioned) throws W;

    public abstract <W extends Exception> Option<T,W> wrapException(Exceptioned<W> exceptioned);

    public abstract <W extends Exception> Option<T,W> wrapNoneWithException(Exceptioned<W> exceptioned);

    public abstract boolean isException();

    public abstract boolean isNone();

    public abstract String getExceptionMessage();


    static <R,E extends Exception> Option<R,E> some(R value){
        if (value == null)
            return none();
        else
            return new Some<>(value);
    }

    public static <R,E extends Exception> Option<R,E> asException(E e){
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

    @SuppressWarnings("unchecked")
    public static <R, E extends Exception> Option<R,E> asOption(Supplier<R,E> func){
        try{
            return some(func.get());
        } catch (Exception e) {
            return new ExcOpt<R, E>((E)e);
        }
    }

    public static <R> Option<R, NullPointerException> asNotNullWithExceptionOption(R value){
        if (value == null)
            return new ExcOpt<R, NullPointerException>(new NullPointerException());
        else
            return some(value);
    }

}
