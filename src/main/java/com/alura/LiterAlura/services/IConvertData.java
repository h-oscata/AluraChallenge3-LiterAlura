package com.alura.LiterAlura.services;

public interface IConvertData {
    <T> T getData(String json, Class<T> clase);
}
