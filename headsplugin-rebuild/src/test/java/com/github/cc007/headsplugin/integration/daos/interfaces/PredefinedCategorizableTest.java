package com.github.cc007.headsplugin.integration.daos.interfaces;

import com.github.cc007.headsplugin.api.business.domain.Head;

import lombok.NonNull;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PredefinedCategorizableTest {

    @Spy
    PredefinedCategorizable predefinedCategorizable = new DummyPredefinedCatagorizable();

    @Test
    void getSource() {
        // prepare
        final var expected = "DatabaseName";

        when(predefinedCategorizable.getDatabaseName())
                .thenReturn(expected);

        // execute
        val actual = predefinedCategorizable.getSource();

        // verify
        assertThat(actual, is(expected));
    }
}

class DummyPredefinedCatagorizable implements PredefinedCategorizable {

    @Override
    public List<Head> getCategoryHeads(@NonNull String categoryName) {
        return null;
    }

    @Override
    public List<String> getCategoryNames() {
        return null;
    }

    @Override
    public String getDatabaseName() {
        return null;
    }
}