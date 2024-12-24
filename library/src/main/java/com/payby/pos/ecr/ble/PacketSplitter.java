package com.payby.pos.ecr.ble;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.ByteBuffer;

import no.nordicsemi.android.ble.data.DataSplitter;

public class PacketSplitter implements DataSplitter {

    /**
     * A method that splits the message and returns a index-th byte array from given message,
     * with at most maxLength size, or null if no bytes are left to be sent.
     * The first packet contains the data packet along with a two-byte header that indicates the size
     * of the message to be sent. The remaining packets contain the message up to the MTU size or
     * packet size (whichever is smaller).
     * For instance, if the message size is 600 and the MTU-512, the first packet will have
     * 510 bytes of the message along with a 2 byte header,
     * and the following packet will contain the remaining 90 bytes.
     *
     * @param message   full message to be chunked.
     * @param index     index of a packet, 0-based.
     * @param maxLength maximum length of the returned packet.
     * @return The packet to be sent, or null, if the whole message was already split.
     */
    @Nullable
    @Override
    public byte[] chunk(@NonNull byte[] message, int index, int maxLength) {
        int messageSize = message.length;
        if (index == 0) {
            int nextSize;
            if (messageSize + 2 <= maxLength) {
                nextSize = messageSize + 2;
            } else {
                nextSize = maxLength;
            }
            byte[] bytes = new byte[nextSize];
            short value = (short) messageSize;
            ByteBuffer.wrap(bytes).putShort(value).put(message, 0, nextSize - 2);
            return bytes;
        }
        int nextSize;
        int newIndex = index * maxLength - 2;
        if (messageSize - newIndex <= maxLength) {
            nextSize = messageSize - newIndex;
        } else {
            nextSize = maxLength;
        }
        if (nextSize <= 0) return null;
        byte[] bytes = new byte[nextSize];
        ByteBuffer.wrap(bytes).put(message, newIndex, nextSize);
        return bytes;
    }

}
