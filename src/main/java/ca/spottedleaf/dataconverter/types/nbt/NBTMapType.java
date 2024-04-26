package ca.spottedleaf.dataconverter.types.nbt;

import ca.spottedleaf.dataconverter.types.*;
import net.kyori.adventure.nbt.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class NBTMapType implements MapType<String> {

    private Map<String, Object> map; // Can contain BinaryTag, NBTMapType, NBTListType

    public NBTMapType() {
        this.map = new HashMap<>();
    }

    public NBTMapType(final CompoundBinaryTag tag) {
        this.map = new HashMap<>();
        for (var entry : tag) {
            this.map.put(entry.getKey(), entry.getValue());
        }
    }

    public NBTMapType(@NotNull Map<String, Object> copy) {
        this.map = new HashMap<>(copy.size());
        for (var entry : copy.entrySet()) {
            this.map.put(entry.getKey(), switch (entry.getValue()) {
                case NBTListType l -> l.copy();
                case NBTMapType m -> m.copy();
                default -> entry.getValue();
            });
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != NBTMapType.class) {
            return false;
        }

        return this.map.equals(((NBTMapType) obj).map);
    }

    @Override
    public TypeUtil getTypeUtil() {
        return Types.NBT;
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }

    @Override
    public String toString() {
        return "NBTMapType{" +
                "map=" + this.map +
                '}';
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<String> keys() {
        return this.map.keySet();
    }

    public BinaryTag getTag() {
        CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder();
        for (var entry : this.map.entrySet()) {
            builder.put(entry.getKey(), switch (entry.getValue()) {
                case BinaryTag t -> t;
                case NBTListType l -> l.getTag();
                case NBTMapType m -> m.getTag();
                default -> throw new IllegalStateException("Unrecognized type " + entry.getValue());
            });
        }
        return builder.build();
    }

    @Override
    public MapType<String> copy() {
        return new NBTMapType(this.map);
    }

    @Override
    public boolean hasKey(final String key) {
        return this.map.get(key) != null;
    }

    @Override
    public boolean hasKey(final String key, final ObjectType type) {
        final Object tag = this.map.get(key);
        if (tag == null) {
            return false;
        }

        if (tag instanceof BinaryTag t) {
            final ObjectType valueType = NBTListType.getType(t.type().id());
            return valueType == type || (type == ObjectType.NUMBER && valueType.isNumber());
        } else if (tag instanceof NBTListType) {
            return type == ObjectType.LIST;
        } else if (tag instanceof NBTMapType) {
            return type == ObjectType.MAP;
        }

        return false;
    }

    @Override
    public void remove(final String key) {
        this.map.remove(key);
    }

    @Override
    public Object getGeneric(final String key) {
        final Object tag = this.map.get(key);
        if (tag == null) {
            return null;
        }

        if (tag instanceof BinaryTag t) {
            switch (NBTListType.getType(t.type().id())) {
                case BYTE:
                case SHORT:
                case INT:
                case LONG:
                case FLOAT:
                case DOUBLE:
                    return numberValue((NumberBinaryTag) tag);
                case MAP:
                    return new NBTMapType((CompoundBinaryTag) tag);
                case LIST:
                    return new NBTListType((ListBinaryTag) tag);
                case STRING:
                    return ((StringBinaryTag) tag).value();
                case BYTE_ARRAY:
                    return ((ByteArrayBinaryTag) tag).value();
                // Note: No short array tag!
                case INT_ARRAY:
                    return ((IntArrayBinaryTag) tag).value();
                case LONG_ARRAY:
                    return ((LongArrayBinaryTag) tag).value();
            }
        } else if (tag instanceof NBTListType l) {
            return l;
        } else if (tag instanceof NBTMapType m) {
            return m;
        }

        throw new IllegalStateException("Unrecognized type " + tag);
    }

    @Override
    public Number getNumber(final String key) {
        return this.getNumber(key, null);
    }

    @Override
    public Number getNumber(final String key, final Number dfl) {
        final Object tag = this.map.get(key);
        if (tag instanceof NumberBinaryTag) {
            return numberValue((NumberBinaryTag) tag);
        }
        return dfl;
    }

    @Override
    public boolean getBoolean(final String key) {
        return this.getByte(key) != 0;
    }

    @Override
    public boolean getBoolean(final String key, final boolean dfl) {
        return this.getByte(key, dfl ? (byte) 1 : (byte) 0) != 0;
    }

    @Override
    public void setBoolean(final String key, final boolean val) {
        this.setByte(key, val ? (byte) 1 : (byte) 0);
    }

    @Override
    public byte getByte(final String key) {
        final Object tag = this.map.get(key);
        if (tag instanceof NumberBinaryTag) {
            return ((NumberBinaryTag) tag).byteValue();
        }
        return 0;
    }

    @Override
    public byte getByte(final String key, final byte dfl) {
        final Object tag = this.map.get(key);
        if (tag instanceof NumberBinaryTag) {
            return ((NumberBinaryTag) tag).byteValue();
        }
        return dfl;
    }

    @Override
    public void setByte(final String key, final byte val) {
        this.map.put(key, ByteBinaryTag.byteBinaryTag(val));
    }

    @Override
    public short getShort(final String key) {
        final Object tag = this.map.get(key);
        if (tag instanceof NumberBinaryTag) {
            return ((NumberBinaryTag) tag).shortValue();
        }
        return 0;
    }

    @Override
    public short getShort(final String key, final short dfl) {
        final Object tag = this.map.get(key);
        if (tag instanceof NumberBinaryTag) {
            return ((NumberBinaryTag) tag).shortValue();
        }
        return dfl;
    }

    @Override
    public void setShort(final String key, final short val) {
        this.map.put(key, ShortBinaryTag.shortBinaryTag(val));
    }

    @Override
    public int getInt(final String key) {
        final Object tag = this.map.get(key);
        if (tag instanceof NumberBinaryTag) {
            return ((NumberBinaryTag) tag).intValue();
        }
        return 0;
    }

    @Override
    public int getInt(final String key, final int dfl) {
        final Object tag = this.map.get(key);
        if (tag instanceof NumberBinaryTag) {
            return ((NumberBinaryTag) tag).intValue();
        }
        return dfl;
    }

    @Override
    public void setInt(final String key, final int val) {
        this.map.put(key, IntBinaryTag.intBinaryTag(val));
    }

    @Override
    public long getLong(final String key) {
        final Object tag = this.map.get(key);
        if (tag instanceof NumberBinaryTag) {
            return ((NumberBinaryTag) tag).longValue();
        }
        return 0;
    }

    @Override
    public long getLong(final String key, final long dfl) {
        final Object tag = this.map.get(key);
        if (tag instanceof NumberBinaryTag) {
            return ((NumberBinaryTag) tag).longValue();
        }
        return dfl;
    }

    @Override
    public void setLong(final String key, final long val) {
        this.map.put(key, LongBinaryTag.longBinaryTag(val));
    }

    @Override
    public float getFloat(final String key) {
        final Object tag = this.map.get(key);
        if (tag instanceof NumberBinaryTag) {
            return ((NumberBinaryTag) tag).floatValue();
        }
        return 0;
    }

    @Override
    public float getFloat(final String key, final float dfl) {
        final Object tag = this.map.get(key);
        if (tag instanceof NumberBinaryTag) {
            return ((NumberBinaryTag) tag).floatValue();
        }
        return dfl;
    }

    @Override
    public void setFloat(final String key, final float val) {
        this.map.put(key, FloatBinaryTag.floatBinaryTag(val));
    }

    @Override
    public double getDouble(final String key) {
        final Object tag = this.map.get(key);
        if (tag instanceof NumberBinaryTag) {
            return ((NumberBinaryTag) tag).doubleValue();
        }
        return 0;
    }

    @Override
    public double getDouble(final String key, final double dfl) {
        final Object tag = this.map.get(key);
        if (tag instanceof NumberBinaryTag) {
            return ((NumberBinaryTag) tag).doubleValue();
        }
        return dfl;
    }

    @Override
    public void setDouble(final String key, final double val) {
        this.map.put(key, DoubleBinaryTag.doubleBinaryTag(val));
    }

    @Override
    public byte[] getBytes(final String key) {
        return this.getBytes(key, null);
    }

    @Override
    public byte[] getBytes(final String key, final byte[] dfl) {
        final Object tag = this.map.get(key);
        if (tag instanceof ByteArrayBinaryTag) {
            return ((ByteArrayBinaryTag) tag).value();
        }
        return dfl;
    }

    @Override
    public void setBytes(final String key, final byte[] val) {
        this.map.put(key, ByteArrayBinaryTag.byteArrayBinaryTag(val));
    }

    @Override
    public short[] getShorts(final String key) {
        return this.getShorts(key, null);
    }

    @Override
    public short[] getShorts(final String key, final short[] dfl) {
        // NBT does not support short array
        return dfl;
    }

    @Override
    public void setShorts(final String key, final short[] val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] getInts(final String key) {
        return this.getInts(key, null);
    }

    @Override
    public int[] getInts(final String key, final int[] dfl) {
        final Object tag = this.map.get(key);
        if (tag instanceof IntArrayBinaryTag) {
            return ((IntArrayBinaryTag) tag).value();
        }
        return dfl;
    }

    @Override
    public void setInts(final String key, final int[] val) {
        this.map.put(key, IntArrayBinaryTag.intArrayBinaryTag(val));
    }

    @Override
    public long[] getLongs(final String key) {
        return this.getLongs(key, null);
    }

    @Override
    public long[] getLongs(final String key, final long[] dfl) {
        final Object tag = this.map.get(key);
        if (tag instanceof LongArrayBinaryTag) {
            return ((LongArrayBinaryTag) tag).value();
        }
        return dfl;
    }

    @Override
    public void setLongs(final String key, final long[] val) {
        this.map.put(key, LongArrayBinaryTag.longArrayBinaryTag(val));
    }

    @Override
    public ListType getListUnchecked(final String key) {
        return this.getListUnchecked(key, null);
    }

    @Override
    public ListType getListUnchecked(final String key, final ListType dfl) {
        final Object tag = this.map.get(key);
        if (tag instanceof NBTListType l)
            return l;
        if (tag instanceof ListBinaryTag)
            return new NBTListType((ListBinaryTag) tag);
        return dfl;
    }

    @Override
    public void setList(final String key, final ListType val) {
        this.map.put(key, val);
    }

    @Override
    public MapType<String> getMap(final String key) {
        return this.getMap(key, null);
    }

    @Override
    public MapType<String> getMap(final String key, final MapType dfl) {
        final Object tag = this.map.get(key);
        if (tag instanceof NBTMapType m)
            return m;
        if (tag instanceof CompoundBinaryTag)
            return new NBTMapType((CompoundBinaryTag) tag);
        return dfl;
    }

    @Override
    public void setMap(final String key, final MapType<?> val) {
        this.map.put(key, val);
    }

    @Override
    public String getString(final String key) {
        return this.getString(key, null);
    }

    @Override
    public String getString(final String key, final String dfl) {
        final Object tag = this.map.get(key);
        if (tag instanceof StringBinaryTag) {
            return ((StringBinaryTag) tag).value();
        }
        return dfl;
    }

    @Override
    public String getForcedString(final String key) {
        return this.getForcedString(key, null);
    }

    @Override
    public String getForcedString(final String key, final String dfl) {
        final Object tag = this.map.get(key);
        if (tag != null) {
            return ((StringBinaryTag) tag).value();
        }
        return dfl;
    }

    @Override
    public void setString(final String key, final String val) {
        this.map.put(key, StringBinaryTag.stringBinaryTag(val));
    }

    private Number numberValue(@NotNull NumberBinaryTag tag) {
        return switch (tag) {
            case ByteBinaryTag byteTag -> byteTag.value();
            case ShortBinaryTag shortTag -> shortTag.value();
            case IntBinaryTag intTag -> intTag.value();
            case LongBinaryTag longTag -> longTag.value();
            case FloatBinaryTag floatTag -> floatTag.value();
            case DoubleBinaryTag doubleTag -> doubleTag.value();
            default -> throw new IllegalStateException("Unrecognized type " + tag);
        };
    }
}
