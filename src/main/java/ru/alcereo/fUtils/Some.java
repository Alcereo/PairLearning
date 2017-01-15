package ru.alcereo.fUtils;


class Some<T, Es extends Exception> extends Option<T,Es> {

    private final T value;

    public Some(T value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R, E extends Exception> Option<R,E> map(Func<T, R, E> func){
        try {
            return some(func.execute(value));
        } catch (Throwable e) {
            return new ExcOpt<>((E)e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R, E extends Exception> Option<R,E> flatMap(Func<T,Option<R,E>,E> func){
        try {
            return func.execute(value);
        } catch (Throwable e) {
            return new ExcOpt<>((E)e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Exception> Option<T,E> filter(Func<T, Boolean, E> filterPredicate){
        try {
            if (filterPredicate.execute(value))
                return (Option<T, E>) this;
            else
                return none();
        } catch (Throwable e) {
            return new ExcOpt<>((E)e);
        }
    }

    @Override
    public T getOrElse(T valueElse) {
        return value;
    }

    @Override
    public Option throwException(){
        return this;
    }

    @Override
    public Option wrapNoneWithException(Exceptioned exceptioned) {
        return this;
    }

    @Override
    public boolean isException() {
        return false;
    }

    @Override
    public boolean isNone() {
        return false;
    }

    @Override
    public String getExceptionMessage() {
        return "";
    }

    @Override
    public Option wrapAndTrowException(Exceptioned exceptioned){
        return this;
    }

    @Override
    public Option wrapException(Exceptioned exceptioned) {
        return this;
    }
}

