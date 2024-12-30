package com.payby.pos.ecr.ble;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.ByteBuffer;

import no.nordicsemi.android.ble.data.DataMerger;
import no.nordicsemi.android.ble.data.DataStream;

public class PacketMerger implements DataMerger {

    private int expectedSize = 0;
    private int receivedDataSize = 0;

    /**
     * A method that merges the last packet into the output message.
     * If its a fist packet, the first two bytes of the packet contain a header which includes the expected size
     * of the message to be received. The remaining bytes of the packet contain the message, which is copied to the output stream.
     * For all subsequent packets, the message bytes are simply copied to the output stream
     * until the size of the packet received matches the expected size of the message delivered through header file.
     *
     * @param output     the stream for the output message, initially empty.
     * @param lastPacket the data received in the last read/notify/indicate operation.
     * @param index      an index of the packet, 0-based.
     * @return True, if the message is complete, false if more data are expected.
     */
    @Override
    public boolean merge(@NonNull DataStream output, @Nullable byte[] lastPacket, int index) {
        if (lastPacket == null) {
            return false;
        }

        if (index == 0) {
            receivedDataSize = 0;
        }

        ByteBuffer buffer = ByteBuffer.wrap(lastPacket);
        receivedDataSize += buffer.remaining();

        if (index == 0) {
            expectedSize = buffer.getShort();
        }

        int remaining = buffer.remaining();
        byte[] remainingBytes = new byte[remaining];
        buffer.get(remainingBytes);
        output.write(remainingBytes);

        return receivedDataSize - 2 >= expectedSize;
    }

}
