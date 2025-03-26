package ca.spottedleaf.dataconverter.util;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ServiceLoader;

public interface ExternalDataProvider {

    static @NotNull ExternalDataProvider get() {
        class Holder {
            static final ExternalDataProvider INSTANCE = ServiceLoader.load(ExternalDataProvider.class)
                    .findFirst().orElseThrow(() -> new IllegalStateException("No ExternalDataProvider found"));
        }
        return Holder.INSTANCE;
    }

    int dataVersion();

    @NotNull List<Integer> extraConverterVersions();
    @NotNull Class<?> extraVersionsClass();

}
