package com.westboy.common.record;

import com.westboy.common.utils.Utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class DefaultRecord {

    private ByteBuffer path;
    private ByteBuffer file;

    public static int writeTo(DataOutputStream out, ByteBuffer file, ByteBuffer path) throws IOException {
        // | sizeInBytes | fileSize |   file   | pathSize |   path   |
        // |     4       |    4     | fileSize |    4     | pathSize |

        int fileSize = file.remaining();
        int pathSize = path.remaining();
        int sizeInBytes = 4 + fileSize + 4 + pathSize;

        out.writeInt(sizeInBytes);

        out.writeInt(fileSize);
        Utils.writeTo(out, file, fileSize);

        out.writeInt(pathSize);
        Utils.writeTo(out, path, pathSize);

        return 4 + sizeInBytes; // 总大小
    }

}
