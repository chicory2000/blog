package com.mrd.bitlib.model;

import com.mrd.bitlib.util.ByteWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public final class InputWitness implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final InputWitness EMPTY = new InputWitness(0);
    public static final int MAX_INITIAL_ARRAY_LENGTH = 20;
    private final int pushCount;
    private final ArrayList<byte[]> stack;

    public InputWitness(int i) {
        this.pushCount = i;
        this.stack = new ArrayList<byte[]>(Math.min(i, 20));
    }

    public final int getPushCount() {
        return this.pushCount;
    }

    public final void setStack(int i, byte[] bArr) {
        while (i >= this.stack.size()) {
            this.stack.add(new byte[0]);
        }
        this.stack.set(i, bArr);
    }

    public final void toByteWriter(ByteWriter byteWriter) {
        byteWriter.putCompactInt((long) this.stack.size());
        Iterator<byte[]> it = this.stack.iterator();
        while (it.hasNext()) {
            byte[] next = it.next();
            byteWriter.putCompactInt((long) next.length);
            byteWriter.putBytes(next);
        }
    }

}
