package at.mrcl.proxied_warp.core.codec;

import com.google.common.io.ByteStreams;

public record Codec(String player, String server, String warp) {

    public static Codec decode(String[] args) {
        if (args.length != 3) throw new IllegalArgumentException("Invalid message format");
        return new Codec(args[0], args[1], args[2]);
    }

    public static Codec decode(String message) {
        final var args = message.split(" ");
        return decode(args);
    }

    public static Codec decode(byte[] data) {
        return decode(ByteStreams.newDataInput(data).readUTF());
    }

    public String encode() {
        return player + " " + server + " " + warp;
    }

    public byte[] write() {
        final var stream = ByteStreams.newDataOutput();
        stream.writeUTF(encode());
        return stream.toByteArray();
    }
}
