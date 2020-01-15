package com.github.cc007.headsplugin.business.services.chat;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class PrettyPrinter {

    public <K, V> String toString(Map<K, V> map) {
        return toString(map, "");
    }
    private <K, V> String toString(Map<K, V> map, String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<K, V> entry = iter.next();
            sb.append(indent).append("  ").append(toString(entry.getKey(), indent + "  "));
            sb.append(indent).append('=');
            sb.append(indent).append("  ").append(toString(entry.getValue(), indent + "  "));
            if (iter.hasNext()) {
                sb.append(',').append('\n');
            }
        }
        sb.append('\n').append(indent).append("}\n");
        return sb.toString();
    }

    public <E> String toString(Collection<E> collection) {
        return toString(collection, "");
    }

    private  <E> String toString(Collection<E> collection, String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        Iterator<E> iter = collection.iterator();
        while (iter.hasNext()) {
            E elem = iter.next();
            sb.append(indent).append("  ").append(toString(elem, indent + "  "));
            if (iter.hasNext()) {
                sb.append(',').append('\n');
            }
        }
        sb.append('\n').append(indent).append("]\n");
        return sb.toString();
    }

    private  <O> String toString(O object, String indent) {
        return indent + object.toString();
    }
}
