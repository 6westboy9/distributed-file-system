package com.westboy.common.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Utils {


    public static String utf8(ByteBuffer buffer, int length) {
        return utf8(buffer, 0, length);
    }

    public static String utf8(ByteBuffer buffer, int offset, int length) {
        if (buffer.hasArray()) {
            return new String(buffer.array(), buffer.arrayOffset() + buffer.position() + offset, length, StandardCharsets.UTF_8);
        } else {
            return utf8(toArray(buffer, offset, length));
        }
    }

    public static byte[] toArray(ByteBuffer buffer, int offset, int size) {
        byte[] dest = new byte[size];
        if (buffer.hasArray()) {
            System.arraycopy(buffer.array(), buffer.position() + buffer.arrayOffset() + offset, dest, 0, size);
        } else {
            int pos = buffer.position();
            buffer.position(pos + offset);
            buffer.get(dest);
            buffer.position(pos);
        }
        return dest;
    }

    public static String utf8(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] utf8(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public static int utf8Length(CharSequence s) {
        int count = 0;
        for (int i = 0, len = s.length(); i < len; i++) {
            char ch = s.charAt(i);
            if (ch <= 0x7F) {
                count++;
            } else if (ch <= 0x7FF) {
                count += 2;
            } else if (Character.isHighSurrogate(ch)) {
                count += 4;
                ++i;
            } else {
                count += 3;
            }
        }
        return count;
    }

    public static ByteBuffer wrap(byte[] bytes) {
        return ByteBuffer.wrap(bytes);
    }

    public static void writeTo(DataOutputStream out, ByteBuffer buffer, int length) throws IOException {
        if (buffer.hasArray()) {
            out.write(buffer.array(), buffer.position() + buffer.arrayOffset(), length);
        } else {
            int pos = buffer.position();
            for (int i = pos; i < length + pos; i++)
                out.writeByte(buffer.get(i));
        }
    }
}
