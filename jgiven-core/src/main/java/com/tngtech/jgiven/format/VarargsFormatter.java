package com.tngtech.jgiven.format;

/**
 * Argument formatter for varargs delegating to {@link PrintfFormatter}
 */
public class VarargsFormatter implements ArgumentFormatter<Object> {

    private final PrintfFormatter formatter = new PrintfFormatter();
    private final String delimiter;

    public VarargsFormatter() {
        this(", ");
    }

    public VarargsFormatter(final String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public String format(final Object argumentToFormat, final String... formatterArguments) {
        if (argumentToFormat instanceof Object[]) {
            final StringBuilder buffer = new StringBuilder();
            final Object[] elements = (Object[]) argumentToFormat;
            for (int i = 0; i < elements.length; i++) {
                buffer.append(formatter.format(elements[i], formatterArguments));
                if (i < elements.length - 1) {
                    buffer.append(delimiter);
                }
            }
            return buffer.toString();
        }
        return formatter.format(argumentToFormat, formatterArguments);
    }
}
