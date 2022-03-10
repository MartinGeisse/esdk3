package name.martingeisse.esdk.plot.variable;

import com.google.common.collect.ImmutableMap;
import name.martingeisse.esdk.core.util.vector.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public final class NamedVectorFormat implements VectorFormat {

    private final ImmutableMap<Long, String> names;

    public NamedVectorFormat(ImmutableMap<Long, String> names) {
        this.names = names;
    }

    @Override
    public String render(VariablePlotDescriptor descriptor, Vector sample) {
        String name = names.get(sample.getAsSignedLong());
        return name == null ? "<unknown>" : name;
    }

    public static NamedVectorFormat fromStaticFields(Field... fields) {
        try {
            Map<Long, String> map = new HashMap<>();
            for (Field field : fields) {
                Object value = field.get(null);
                if (value instanceof Number) {
                    map.put(((Number)value).longValue(), field.getName());
                } else if (value instanceof Vector) {
                    map.put(((Vector)value).getAsSignedLong(), field.getName());
                } else {
                    throw new IllegalArgumentException("cannot obtain value from field: " + field);
                }
            }
            return new NamedVectorFormat(ImmutableMap.copyOf(map));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static NamedVectorFormat fromStaticFieldsWithFieldFilter(Class<?> c, Predicate<Field> filter) {
        Set<Field> fields = new HashSet<>();
        while (c != null) {
            for (Field field : c.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && filter.test(field)) {
                    if (!fields.add(field)) {
                        throw new IllegalArgumentException("collision for field " + field);
                    }
                }
            }
            c = c.getSuperclass();
        }
        return fromStaticFields(fields.toArray(new Field[0]));
    }

    public static NamedVectorFormat fromStaticFieldsWithNameFilter(Class<?> c, Predicate<String> filter) {
        return fromStaticFieldsWithFieldFilter(c, (Field field) -> filter.test(field.getName()));
    }

}
