package ca.spottedleaf.dataconverter.types.nbt;

import ca.spottedleaf.dataconverter.types.*;
import net.kyori.adventure.nbt.*;

import java.util.ArrayList;
import java.util.List;

public final class NBTListType implements ListType {

    private List<Object> list; // Can contain BinaryTags, NBTListType, NBTMapType

    public NBTListType() {
        this.list = new ArrayList<>();
    }

    public NBTListType(final ListBinaryTag tag) {
        this.list = new ArrayList<>(tag.stream().toList());
    }

    public NBTListType(final List<Object> entries) {
        this.list = new ArrayList<>(entries);
    }

    @Override
    public TypeUtil getTypeUtil() {
        return Types.NBT;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != NBTListType.class) {
            return false;
        }

        return this.list.equals(((NBTListType) obj).list);
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }

    @Override
    public String toString() {
        return "NBTListType{" +
                "list=" + this.list +
                '}';
    }

    public ListBinaryTag getTag() {
        final ListBinaryTag.Builder<BinaryTag> builder = ListBinaryTag.builder();
        for (Object entry : this.list) {
            if (entry instanceof BinaryTag tag) {
                builder.add(tag);
            } else if (entry instanceof NBTListType list) {
                builder.add((BinaryTag) list.getTag());
            } else if (entry instanceof NBTMapType map) {
                builder.add(map.getTag());
            } else {
                throw new IllegalStateException("Unknown type: " + entry);
            }
        }
        return builder.build();
    }

    @Override
    public ListType copy() {
        return new NBTListType(this.list);
    }

    protected static ObjectType getType(final byte id) {
        switch (id) {
            case 0: // END
                return ObjectType.NONE;
            case 1: // BYTE
                return ObjectType.BYTE;
            case 2: // SHORT
                return ObjectType.SHORT;
            case 3: // INT
                return ObjectType.INT;
            case 4: // LONG
                return ObjectType.LONG;
            case 5: // FLOAT
                return ObjectType.FLOAT;
            case 6: // DOUBLE
                return ObjectType.DOUBLE;
            case 7: // BYTE_ARRAY
                return ObjectType.BYTE_ARRAY;
            case 8: // STRING
                return ObjectType.STRING;
            case 9: // LIST
                return ObjectType.LIST;
            case 10: // COMPOUND
                return ObjectType.MAP;
            case 11: // INT_ARRAY
                return ObjectType.INT_ARRAY;
            case 12: // LONG_ARRAY
                return ObjectType.LONG_ARRAY;
            default:
                throw new IllegalStateException("Unknown type: " + id);
        }
    }

    @Override
    public ObjectType getType() {
        if (this.list.isEmpty())
            return ObjectType.NONE;
        return switch (this.list.getFirst()) {
            case BinaryTag tag -> getType(tag.type().id());
            case NBTListType l -> ObjectType.LIST;
            case NBTMapType m -> ObjectType.MAP;
            default -> ObjectType.NONE;
        };
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public void remove(final int index) {
        this.list.remove(index);
    }

    @Override
    public Number getNumber(final int index) {
        final Object tag = this.list.get(index);
        if (!(tag instanceof NumberBinaryTag)) {
            throw new IllegalStateException();
        }
        switch (tag) {
            case ByteBinaryTag byteTag:
                return byteTag.value();
            case ShortBinaryTag shortTag:
                return shortTag.value();
            case IntBinaryTag intTag:
                return intTag.value();
            case LongBinaryTag longTag:
                return longTag.value();
            case FloatBinaryTag floatTag:
                return floatTag.value();
            case DoubleBinaryTag doubleTag:
                return doubleTag.value();
            default:
                throw new IllegalStateException("Unrecognized type " + tag);
        }
    }

    @Override
    public byte getByte(final int index) {
        final Object tag = this.list.get(index);
        if (!(tag instanceof NumberBinaryTag)) {
            throw new IllegalStateException();
        }
        return ((NumberBinaryTag) tag).byteValue();
    }

    @Override
    public void setByte(final int index, final byte to) {
        this.list.set(index, ByteBinaryTag.byteBinaryTag(to));
    }

    @Override
    public short getShort(final int index) {
        final Object tag = this.list.get(index);
        if (!(tag instanceof NumberBinaryTag)) {
            throw new IllegalStateException();
        }
        return ((NumberBinaryTag) tag).shortValue();
    }

    @Override
    public void setShort(final int index, final short to) {
        this.list.set(index, ShortBinaryTag.shortBinaryTag(to));
    }

    @Override
    public int getInt(final int index) {
        final Object tag = this.list.get(index);
        if (!(tag instanceof NumberBinaryTag)) {
            throw new IllegalStateException();
        }
        return ((NumberBinaryTag) tag).intValue();
    }

    @Override
    public void setInt(final int index, final int to) {
        this.list.set(index, IntBinaryTag.intBinaryTag(to));
    }

    @Override
    public long getLong(final int index) {
        final Object tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NumberBinaryTag)) {
            throw new IllegalStateException();
        }
        return ((NumberBinaryTag) tag).longValue();
    }

    @Override
    public void setLong(final int index, final long to) {
        this.list.set(index, LongBinaryTag.longBinaryTag(to));
    }

    @Override
    public float getFloat(final int index) {
        final Object tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NumberBinaryTag)) {
            throw new IllegalStateException();
        }
        return ((NumberBinaryTag) tag).floatValue();
    }

    @Override
    public void setFloat(final int index, final float to) {
        this.list.set(index, FloatBinaryTag.floatBinaryTag(to));
    }

    @Override
    public double getDouble(final int index) {
        final Object tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NumberBinaryTag)) {
            throw new IllegalStateException();
        }
        return ((NumberBinaryTag) tag).doubleValue();
    }

    @Override
    public void setDouble(final int index, final double to) {
        this.list.set(index, DoubleBinaryTag.doubleBinaryTag(to));
    }

    @Override
    public byte[] getBytes(final int index) {
        final Object tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof ByteArrayBinaryTag)) {
            throw new IllegalStateException();
        }
        return ((ByteArrayBinaryTag) tag).value();
    }

    @Override
    public void setBytes(final int index, final byte[] to) {
        this.list.set(index, ByteArrayBinaryTag.byteArrayBinaryTag(to));
    }

    @Override
    public short[] getShorts(final int index) {
        // NBT does not support shorts
        throw new UnsupportedOperationException();
    }

    @Override
    public void setShorts(final int index, final short[] to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] getInts(final int index) {
        final Object tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof IntArrayBinaryTag)) {
            throw new IllegalStateException();
        }
        return ((IntArrayBinaryTag) tag).value();
    }

    @Override
    public void setInts(final int index, final int[] to) {
        this.list.set(index, IntArrayBinaryTag.intArrayBinaryTag(to));
    }

    @Override
    public long[] getLongs(final int index) {
        final Object tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof LongArrayBinaryTag)) {
            throw new IllegalStateException();
        }
        return ((LongArrayBinaryTag) tag).value();
    }

    @Override
    public void setLongs(final int index, final long[] to) {
        this.list.set(index, LongArrayBinaryTag.longArrayBinaryTag(to));
    }

    @Override
    public ListType getList(final int index) {
        final Object tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof ListBinaryTag)) {
            throw new IllegalStateException();
        }
        return new NBTListType((ListBinaryTag) tag);
    }

    @Override
    public void setList(final int index, final ListType list) {
        this.list.set(index, ((NBTListType) list).getTag());
    }

    @Override
    public MapType<String> getMap(final int index) {
        final Object tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof CompoundBinaryTag)) {
            throw new IllegalStateException();
        }
        return new NBTMapType((CompoundBinaryTag) tag);
    }

    @Override
    public void setMap(final int index, final MapType<?> to) {
        this.list.set(index, ((NBTMapType) to).getTag());
    }

    @Override
    public String getString(final int index) {
        final Object tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof StringBinaryTag)) {
            throw new IllegalStateException();
        }
        return ((StringBinaryTag) tag).value();
    }

    @Override
    public void setString(final int index, final String to) {
        this.list.set(index, StringBinaryTag.stringBinaryTag(to));
    }

    @Override
    public void addByte(final byte b) {
        this.list.add(ByteBinaryTag.byteBinaryTag(b));
    }

    @Override
    public void addByte(final int index, final byte b) {
        this.list.set(index, ByteBinaryTag.byteBinaryTag(b));
    }

    @Override
    public void addShort(final short s) {
        this.list.add(ShortBinaryTag.shortBinaryTag(s));
    }

    @Override
    public void addShort(final int index, final short s) {
        this.list.set(index, ShortBinaryTag.shortBinaryTag(s));
    }

    @Override
    public void addInt(final int i) {
        this.list.add(IntBinaryTag.intBinaryTag(i));
    }

    @Override
    public void addInt(final int index, final int i) {
        this.list.set(index, IntBinaryTag.intBinaryTag(i));
    }

    @Override
    public void addLong(final long l) {
        this.list.add(LongBinaryTag.longBinaryTag(l));
    }

    @Override
    public void addLong(final int index, final long l) {
        this.list.set(index, LongBinaryTag.longBinaryTag(l));
    }

    @Override
    public void addFloat(final float f) {
        this.list.add(FloatBinaryTag.floatBinaryTag(f));
    }

    @Override
    public void addFloat(final int index, final float f) {
        this.list.set(index, FloatBinaryTag.floatBinaryTag(f));
    }

    @Override
    public void addDouble(final double d) {
        this.list.add(DoubleBinaryTag.doubleBinaryTag(d));
    }

    @Override
    public void addDouble(final int index, final double d) {
        this.list.set(index, DoubleBinaryTag.doubleBinaryTag(d));
    }

    @Override
    public void addByteArray(final byte[] arr) {
        this.list.add(ByteArrayBinaryTag.byteArrayBinaryTag(arr));
    }

    @Override
    public void addByteArray(final int index, final byte[] arr) {
        this.list.set(index, ByteArrayBinaryTag.byteArrayBinaryTag(arr));
    }

    @Override
    public void addShortArray(final short[] arr) {
        // NBT does not support short[]
        throw new UnsupportedOperationException();
    }

    @Override
    public void addShortArray(final int index, final short[] arr) {
        // NBT does not support short[]
        throw new UnsupportedOperationException();
    }

    @Override
    public void addIntArray(final int[] arr) {
        this.list.add(IntArrayBinaryTag.intArrayBinaryTag(arr));
    }

    @Override
    public void addIntArray(final int index, final int[] arr) {
        this.list.set(index, IntArrayBinaryTag.intArrayBinaryTag(arr));
    }

    @Override
    public void addLongArray(final long[] arr) {
        this.list.add(LongArrayBinaryTag.longArrayBinaryTag(arr));
    }

    @Override
    public void addLongArray(final int index, final long[] arr) {
        this.list.set(index, LongArrayBinaryTag.longArrayBinaryTag(arr));
    }

    @Override
    public void addList(final ListType list) {
        this.list.add(list);
    }

    @Override
    public void addList(final int index, final ListType list) {
        this.list.set(index, list);
    }

    @Override
    public void addMap(final MapType<?> map) {
        this.list.add(map);
    }

    @Override
    public void addMap(final int index, final MapType<?> map) {
        this.list.set(index, map);
    }

    @Override
    public void addString(final String string) {
        this.list.add(StringBinaryTag.stringBinaryTag(string));
    }

    @Override
    public void addString(final int index, final String string) {
        this.list.set(index, StringBinaryTag.stringBinaryTag(string));
    }
}
