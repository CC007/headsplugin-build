package com.github.cc007.headsplugin.integration.daos.interfaces;

public interface PredefinedCategorizable extends Categorizable, DatabaseClientDao {

    @Override
    default String getSource() {
        return getDatabaseName();
    }
}
