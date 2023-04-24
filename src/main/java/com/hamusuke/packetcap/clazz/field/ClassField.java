package com.hamusuke.packetcap.clazz.field;

import com.google.common.primitives.Primitives;
import com.hamusuke.packetcap.clazz.visitor.*;
import com.hamusuke.packetcap.utils.ObjectUtil;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ClassField {
    @Nullable
    default ClassVisitor getVisitor() {
        return null;
    }

    String getDescription();

    @Nullable
    static ClassVisitor findClassVisitor(Object obj) {
        if (obj != null) {
            if (obj.getClass().isArray()) {
                return new ArrayVisitor(obj.getClass(), ObjectUtil.toArray(obj));
            } else if (obj instanceof Collection<?> collection) {
                return new CollectionVisitor(collection.getClass(), collection);
            } else if (obj instanceof Map<?, ?> map) {
                return new MapVisitor(map.getClass(), map);
            } else if (obj instanceof String || Primitives.isWrapperType(obj.getClass()) || obj.getClass().isEnum()) {
                return new StringConvertibleClassVisitor(obj.getClass(), obj);
            } else {
                return new ClassVisitor(obj.getClass(), obj);
            }
        }

        return null;
    }
}
