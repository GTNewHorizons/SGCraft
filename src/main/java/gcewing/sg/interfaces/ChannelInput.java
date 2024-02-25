package gcewing.sg.interfaces;

import java.io.DataInput;

public interface ChannelInput extends DataInput {

    boolean readBoolean();

    byte readByte();

    char readChar();

    double readDouble();

    float readFloat();

    void readFully(byte[] b);

    void readFully(byte[] b, int off, int len);

    int readInt();

    String readLine();

    long readLong();

    short readShort();

    int readUnsignedByte();

    int readUnsignedShort();

    String readUTF();

    int skipBytes(int n);
}
