package com.westboy.common.requests;

import java.nio.ByteBuffer;

public abstract class Type {

    public abstract void write(ByteBuffer buffer, Object o);

    public abstract Object read(ByteBuffer buffer);

    public abstract int sizeOf(Object o);

    public abstract Object validate(Object o);

    public static final Type BOOLEAN = new Type() {
        @Override
        public void write(ByteBuffer buffer, Object o) {
            if ((Boolean) o)
                buffer.put((byte) 1);
            else
                buffer.put((byte) 0);
        }

        @Override
        public Object read(ByteBuffer buffer) {
            byte value = buffer.get();
            return value != 0;
        }

        @Override
        public int sizeOf(Object o) {
            return 1;
        }

        @Override
        public Boolean validate(Object item) {
            if (item instanceof Boolean) {
                return (Boolean) item;
            } else {
                throw new RuntimeException(item + " is not a Boolean.");
            }
        }

        @Override
        public String toString() {
            return "BOOLEAN";
        }
    };

    public static final Type INT8 = new Type() {
        @Override
        public void write(ByteBuffer buffer, Object o) {
            buffer.put((Byte) o);
        }

        @Override
        public Object read(ByteBuffer buffer) {
            return buffer.get();
        }

        @Override
        public int sizeOf(Object o) {
            return 1;
        }

        @Override
        public String toString() {
            return "INT8";
        }

        @Override
        public Byte validate(Object item) {
            if (item instanceof Byte) {
                return (Byte) item;
            }
            else {
                throw new RuntimeException(item + " is not a Byte.");
            }
        }
    };

    public static final Type FILE = new Type() {
        @Override
        public void write(ByteBuffer buffer, Object o) {
            ByteBuffer arg = (ByteBuffer) o;
            int pos = arg.position();
            buffer.putInt(arg.remaining());
            buffer.put(arg);
            arg.position(pos);
        }

        @Override
        public Object read(ByteBuffer buffer) {
            return null;
        }

        @Override
        public int sizeOf(Object o) {
            return 0;
        }

        @Override
        public Object validate(Object o) {
            return null;
        }
    };

}
