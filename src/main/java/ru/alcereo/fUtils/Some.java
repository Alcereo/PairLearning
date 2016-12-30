package ru.alcereo.fUtils;


class Some<T> extends Option<T> {

    private final T value;

    public Some(T value) {
        this.value = value;
    }

    @Override
    public <R, E extends Throwable> Option<R> map(Func<T, R, E> func) throws E {
        return some(func.execute(value));
    }

    @Override
    public <R, E extends Throwable> Option<R> flatMap(Func<T, Option<R>, E> func) throws E {
        return func.execute(value);
    }

    @Override
    public <E extends Throwable> Option<T> filter(Func<T, Boolean, E> filterPredicate) throws E {
        if (filterPredicate.execute(value))
            return this;
        else
            return none();
    }

    @Override
    public T getOrElse(T valueElse) {
        return value;
    }
}

