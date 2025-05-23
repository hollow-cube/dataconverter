package ca.spottedleaf.dataconverter.minecraft.versions;

import ca.spottedleaf.dataconverter.converters.DataConverter;
import ca.spottedleaf.dataconverter.minecraft.MCVersions;
import ca.spottedleaf.dataconverter.minecraft.datatypes.MCTypeRegistry;
import ca.spottedleaf.dataconverter.types.MapType;
import ca.spottedleaf.dataconverter.types.TypeUtil;

public final class V4068 {
    // Used to be Escapers.builder().addEscape('"', "\\\"").addEscape('\\', "\\\\").build();
    // This probably doesnt quite do the same things as guava, but it should be good enough.
    public static String escape(String input) {
        if (input == null) return null;
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static final int VERSION = MCVersions.V24W38A + 2;

    private static void convertLock(final MapType<String> root, final String srcPath, final String dstPath) {
        if (root == null) {
            return;
        }

        final Object lockGeneric = root.getGeneric(srcPath);
        if (lockGeneric == null) {
            return;
        }

        final TypeUtil typeUtil = root.getTypeUtil();

        root.remove(srcPath);

        if (lockGeneric instanceof String lock && !lock.isEmpty()) {
            final MapType<String> newLock = typeUtil.createEmptyMap();
            root.setMap(dstPath, newLock);

            final MapType<String> lockComponents = typeUtil.createEmptyMap();
            newLock.setMap("components", lockComponents);

            lockComponents.setString("minecraft:custom_name", escape(lock));
        }
    }

    public static void register() {
        MCTypeRegistry.ITEM_STACK.addStructureConverter(new DataConverter<>(VERSION) {
            @Override
            public MapType<String> convert(final MapType<String> data, final long sourceVersion, final long toVersion) {
                final MapType<String> components = data.getMap("components");
                if (components == null) {
                    return null;
                }

                convertLock(components, "minecraft:lock", "minecraft:lock");

                return null;
            }
        });
        MCTypeRegistry.TILE_ENTITY.addStructureConverter(new DataConverter<>(VERSION) {
            @Override
            public MapType<String> convert(final MapType<String> data, final long sourceVersion, final long toVersion) {
                convertLock(data, "Lock", "lock");
                return null;
            }
        });
    }

    private V4068() {
    }
}
