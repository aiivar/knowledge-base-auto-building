package ru.itis.aivar.kernel.service;

public interface EntityCreator<T> {

    T newInstance();
}
