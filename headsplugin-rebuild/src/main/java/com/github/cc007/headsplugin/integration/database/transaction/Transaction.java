package com.github.cc007.headsplugin.integration.database.transaction;

public interface Transaction {
    void begin();

    void commit(boolean clearCache);
}
