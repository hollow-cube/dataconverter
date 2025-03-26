package ca.spottedleaf.dataconverter.minecraft.converters.blockname;

import net.kyori.adventure.nbt.CompoundBinaryTag;

public final class BlockStateData {

    // This is the very long map of all the old block state id to new block state mappings.
    // I believe I can fake this using the worldedit legacy.json file to get all the IDs, but need to look into it.
    // For now, just an error.

    public static CompoundBinaryTag getTag(int id) {
        throw new UnsupportedOperationException("no legacy block states present");
    }
}
