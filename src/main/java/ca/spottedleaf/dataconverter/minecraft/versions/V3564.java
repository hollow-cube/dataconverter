package ca.spottedleaf.dataconverter.minecraft.versions;

import ca.spottedleaf.dataconverter.converters.DataConverter;
import ca.spottedleaf.dataconverter.minecraft.MCVersions;
import ca.spottedleaf.dataconverter.minecraft.datatypes.MCTypeRegistry;
import ca.spottedleaf.dataconverter.types.ListType;
import ca.spottedleaf.dataconverter.types.MapType;
import ca.spottedleaf.dataconverter.types.ObjectType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class V3564 {

    private static final int VERSION = MCVersions.V1_20_1 + 99;

    public static void register() {
        final DataConverter<MapType<String>, MapType<String>> converter = new DataConverter<>(VERSION) {

            private static final String[] LEGACY_FIELDS = new String[] {
                    "Text1",
                    "Text2",
                    "Text3",
                    "Text4",

                    "FilteredText1",
                    "FilteredText2",
                    "FilteredText3",
                    "FilteredText4",

                    "Color",

                    "GlowingText"
            };

            private static final String EMPTY = Component.Serializer.toJson(CommonComponents.EMPTY);

            private static void updateText(final MapType<String> text) {
                if (text == null) {
                    return;
                }

                if (text.getBoolean("_filtered_correct", false)) {
                    text.remove("_filtered_correct");
                    return;
                }

                final ListType filteredMessages = text.getList("filtered_messages", ObjectType.STRING);

                if (filteredMessages == null) {
                    return;
                }

                // should treat null here as empty list
                final ListType messages = text.getList("messages", ObjectType.STRING);

                final ListType newFilteredList = filteredMessages.getTypeUtil().createEmptyList();
                boolean newFilteredIsEmpty = true;

                for (int i = 0, len = filteredMessages.size(); i < len; ++i) {
                    final String filtered = filteredMessages.getString(i);
                    final String message = messages != null && i < messages.size() ? messages.getString(i) : EMPTY;

                    final String newFiltered = EMPTY.equals(filtered) ? message : filtered;

                    newFilteredList.addString(newFiltered);

                    newFilteredIsEmpty = newFilteredIsEmpty && EMPTY.equals(newFiltered);
                }

                if (newFilteredIsEmpty) {
                    text.remove("filtered_messages");
                } else {
                    text.setList("filtered_messages", newFilteredList);
                }
            }

            @Override
            public MapType<String> convert(final MapType<String> data, final long sourceVersion, final long toVersion) {
                updateText(data.getMap("front_text"));
                updateText(data.getMap("back_text"));

                for (final String toRemove : LEGACY_FIELDS) {
                    data.remove(toRemove);
                }

                return null;
            }
        };

        MCTypeRegistry.TILE_ENTITY.addConverterForId("minecraft:sign", converter);
        MCTypeRegistry.TILE_ENTITY.addConverterForId("minecraft:hanging_sign", converter);
    }
}
