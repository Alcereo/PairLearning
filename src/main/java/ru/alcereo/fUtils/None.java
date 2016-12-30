package ru.alcereo.fUtils;


class None<T> extends Option<T>{

    public None() {
    }

    @Override
    public <R,E extends Throwable> Option<R> map(Func<T, R, E> func) throws E {
        return none();
    }

    @Override
    public <R,E extends Throwable> Option<R> flatMap(Func<T, Option<R>,E> func) throws E {
        return none();
    }

    @Override
    public <E extends Throwable> Option<T> filter(Func<T, Boolean, E> filterPredicate) throws E {
        return none();
    }

    @Override
    public T getOrElse(T valueElse) {
        return valueElse;
    }

}

