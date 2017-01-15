package ru.alcereo.fUtils;


import java.util.Arrays;

public class ExcOpt<T, E extends Exception> extends Option<T,E> {

    private final E e;

    @SuppressWarnings("unchecked")
    @Override
    public Option map(Func func){
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Option flatMap(Func func){
        return this;
    }

    @Override
    public Option filter(Func filterPredicate){
        return this;
    }

    @Override
    public T getOrElse(T valueElse){
        return valueElse;
    }

    ExcOpt(E e) {
        this.e = e;
    }

    public Option throwException() throws E {
        throw e;
    }

    @Override
    public <W extends Throwable> Option<T, E> wrapAndTrowException(Exceptioned<W> exceptioned) throws W {

//        try {
        W t = exceptioned.getNewException(e);
        StackTraceElement[] stackArr = t.getStackTrace();
        t.setStackTrace(Arrays.copyOfRange(stackArr,2,stackArr.length));

        throw t;
//        } catch (InstantiationException | IllegalAccessException e) {
//            e.printStackTrace();
//        }

        //Это опасность прям
//        return null;
    }

    @Override
    public <W extends Exception> Option<T, W> wrapException(Exceptioned<W> exceptioned) {
        W t = exceptioned.getNewException(e);
        StackTraceElement[] stackArr = t.getStackTrace();
        t.setStackTrace(Arrays.copyOfRange(stackArr,2,stackArr.length));

        return new ExcOpt<>(t);
    }

    @Override
    public Option wrapNoneWithException(Exceptioned exceptioned) {
        return this;
    }

    @Override
    public boolean isException() {
        return true;
    }

    @Override
    public boolean isNone() {
        return false;
    }

    @Override
    public String getExceptionMessage() {
        return e.getMessage();
    }
}
