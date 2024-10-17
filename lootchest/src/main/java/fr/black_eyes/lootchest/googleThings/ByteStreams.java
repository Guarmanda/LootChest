package fr.black_eyes.lootchest.googleThings;

/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */




import static java.lang.Math.max;
import static java.lang.Math.min;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;




/**
 * Provides utility methods for working with byte arrays and I/O streams.
 *
 * @author Chris Nokleberg
 * @author Colin Decker
 * @since 1.0
 */

public final class ByteStreams {

  private static final int BUFFER_SIZE = 8192;

  /** Creates a new byte array for buffering reads or writes. */
  static byte[] createBuffer() {
    return new byte[BUFFER_SIZE];
  }

  private ByteStreams() {}

 

  /**
   * Copies all bytes from the readable channel to the writable channel. Does not close or flush
   * either channel.
   *
   * @param from the readable channel to read from
   * @param to the writable channel to write to
   * @return the number of bytes copied
   * @throws IOException if an I/O error occurs
   */
  


  /** Max array length on JVM. */
  private static final int MAX_ARRAY_LEN = Integer.MAX_VALUE - 8;

  /** Large enough to never need to expand, given the geometric progression of buffer sizes. */
  private static final int TO_BYTE_ARRAY_DEQUE_SIZE = 20;

  
  
  public static int saturatedMultiply(int a, int b) {
	    return saturatedCast((long) a * b);
	  }
	  
	  public static int saturatedCast(long value) {
		    if (value > Integer.MAX_VALUE) {
		      return Integer.MAX_VALUE;
		    }
		    if (value < Integer.MIN_VALUE) {
		      return Integer.MIN_VALUE;
		    }
		    return (int) value;
		  }
  
  
  /**
   * Returns a byte array containing the bytes from the buffers already in {@code bufs} (which have
   * a total combined length of {@code totalLen} bytes) followed by all bytes remaining in the given
   * input stream.
   */
  private static byte[] toByteArrayInternal(InputStream in, Queue<byte[]> bufs, int totalLen)
      throws IOException {
    // Roughly size to match what has been read already. Some file systems, such as procfs, return 0
    // as their length. These files are very small, so it's wasteful to allocate an 8KB buffer.
    int initialBufferSize = min(BUFFER_SIZE, max(128, Integer.highestOneBit(totalLen) * 2));
    // Starting with an 8k buffer, double the size of each successive buffer. Smaller buffers
    // quadruple in size until they reach 8k, to minimize the number of small reads for longer
    // streams. Buffers are retained in a deque so that there's no copying between buffers while
    // reading and so all of the bytes in each new allocated buffer are available for reading from
    // the stream.
    for (int bufSize = initialBufferSize;
        totalLen < MAX_ARRAY_LEN;
        bufSize = saturatedMultiply(bufSize, bufSize < 4096 ? 4 : 2)) {
      byte[] buf = new byte[min(bufSize, MAX_ARRAY_LEN - totalLen)];
      bufs.add(buf);
      int off = 0;
      while (off < buf.length) {
        // always OK to fill buf; its size plus the rest of bufs is never more than MAX_ARRAY_LEN
        int r = in.read(buf, off, buf.length - off);
        if (r == -1) {
          return combineBuffers(bufs, totalLen);
        }
        off += r;
        totalLen += r;
      }
    }

    // read MAX_ARRAY_LEN bytes without seeing end of stream
    if (in.read() == -1) {
      // oh, there's the end of the stream
      return combineBuffers(bufs, MAX_ARRAY_LEN);
    } else {
      throw new OutOfMemoryError("input is too large to fit in a byte array");
    }
  }

  private static byte[] combineBuffers(Queue<byte[]> bufs, int totalLen) {
    if (bufs.isEmpty()) {
      return new byte[0];
    }
    byte[] result = bufs.remove();
    if (result.length == totalLen) {
      return result;
    }
    int remaining = totalLen - result.length;
    result = Arrays.copyOf(result, totalLen);
    while (remaining > 0) {
      byte[] buf = bufs.remove();
      int bytesToCopy = min(remaining, buf.length);
      int resultOffset = totalLen - remaining;
      System.arraycopy(buf, 0, result, resultOffset, bytesToCopy);
      remaining -= bytesToCopy;
    }
    return result;
  }

  /**
   * Reads all bytes from an input stream into a byte array. Does not close the stream.
   *
   * @param in the input stream to read from
   * @return a byte array containing all the bytes from the stream
   * @throws IOException if an I/O error occurs
   */
  public static byte[] toByteArray(InputStream in) throws IOException {

    return toByteArrayInternal(in, new ArrayDeque<byte[]>(TO_BYTE_ARRAY_DEQUE_SIZE), 0);
  }

  /**
   * Reads all bytes from an input stream into a byte array. The given expected size is used to
   * create an initial byte array, but if the actual number of bytes read from the stream differs,
   * the correct result will be returned anyway.
   */
  static byte[] toByteArray(InputStream in, long expectedSize) throws IOException {

    if (expectedSize > MAX_ARRAY_LEN) {
      throw new OutOfMemoryError(expectedSize + " bytes is too large to fit in a byte array");
    }

    byte[] bytes = new byte[(int) expectedSize];
    int remaining = (int) expectedSize;

    while (remaining > 0) {
      int off = (int) expectedSize - remaining;
      int read = in.read(bytes, off, remaining);
      if (read == -1) {
        // end of stream before reading expectedSize bytes
        // just return the bytes read so far
        return Arrays.copyOf(bytes, off);
      }
      remaining -= read;
    }

    // bytes is now full
    int b = in.read();
    if (b == -1) {
      return bytes;
    }

    // the stream was longer, so read the rest normally
    Queue<byte[]> bufs = new ArrayDeque<>(TO_BYTE_ARRAY_DEQUE_SIZE + 2);
    bufs.add(bytes);
    bufs.add(new byte[] {(byte) b});
    return toByteArrayInternal(in, bufs, bytes.length + 1);
  }

  /**
   * Reads and discards data from the given {@code InputStream} until the end of the stream is
   * reached. Returns the total number of bytes read. Does not close the stream.
   *
   * @since 20.0
   */
  


  /**
   * Returns a new {@link ByteArrayDataInput} instance to read from the {@code bytes} array from the
   * beginning.
   */
  
  public static ByteArrayDataInput newDataInput(byte[] bytes) {
    return newDataInput(new ByteArrayInputStream(bytes));
  }


  /**
   * Returns a new {@link ByteArrayDataInput} instance to read from the given {@code
   * ByteArrayInputStream}. The given input stream is not reset before being read from by the
   * returned {@code ByteArrayDataInput}.
   *
   * @since 17.0
   */
  
  public static ByteArrayDataInput newDataInput(ByteArrayInputStream byteArrayInputStream) {
    return new ByteArrayDataInputStream(byteArrayInputStream);
  }

  private static class ByteArrayDataInputStream implements ByteArrayDataInput {
    final DataInput input;

    ByteArrayDataInputStream(ByteArrayInputStream byteArrayInputStream) {
      this.input = new DataInputStream(byteArrayInputStream);
    }

 

    @Override
    public String readUTF() {
      try {
        return input.readUTF();
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }



	@Override
	public void readFully(byte[] b) throws IOException {
		
		
	}



	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		
		
	}



	@Override
	public int skipBytes(int n) throws IOException {
		
		return 0;
	}



	@Override
	public boolean readBoolean() throws IOException {
		
		return false;
	}



	@Override
	public byte readByte() throws IOException {
		
		return 0;
	}



	@Override
	public int readUnsignedByte() throws IOException {
		
		return 0;
	}



	@Override
	public short readShort() throws IOException {
		
		return 0;
	}



	@Override
	public int readUnsignedShort() throws IOException {
		
		return 0;
	}



	@Override
	public char readChar() throws IOException {
		
		return 0;
	}



	@Override
	public int readInt() throws IOException {
		
		return 0;
	}



	@Override
	public long readLong() throws IOException {
		
		return 0;
	}



	@Override
	public float readFloat() throws IOException {
		
		return 0;
	}



	@Override
	public double readDouble() throws IOException {
		
		return 0;
	}



	@Override
	public String readLine() throws IOException {
		
		return null;
	}
  }

  /** Returns a new {@link ByteArrayDataOutput} instance with a default size. */
  
  public static ByteArrayDataOutput newDataOutput() {
    return newDataOutput(new ByteArrayOutputStream());
  }

  /**
   * Returns a new {@link ByteArrayDataOutput} instance sized to hold {@code size} bytes before
   * resizing.
   *
   * @throws IllegalArgumentException if {@code size} is negative
   */
  
  public static ByteArrayDataOutput newDataOutput(int size) {
    // When called at high frequency, boxing size generates too much garbage,
    // so avoid doing that if we can.
    if (size < 0) {
      throw new IllegalArgumentException(String.format("Invalid size: %s", size));
    }
    return newDataOutput(new ByteArrayOutputStream(size));
  }

  /**
   * Returns a new {@link ByteArrayDataOutput} instance which writes to the given {@code
   * ByteArrayOutputStream}. The given output stream is not reset before being written to by the
   * returned {@code ByteArrayDataOutput} and new data will be appended to any existing content.
   *
   * <p>Note that if the given output stream was not empty or is modified after the {@code
   * ByteArrayDataOutput} is created, the contract for {@link ByteArrayDataOutput#toByteArray} will
   * not be honored (the bytes returned in the byte array may not be exactly what was written via
   * calls to {@code ByteArrayDataOutput}).
   *
   * @since 17.0
   */
  
  public static ByteArrayDataOutput newDataOutput(ByteArrayOutputStream byteArrayOutputStream) {
    return new ByteArrayDataOutputStream(byteArrayOutputStream);
  }

  private static class ByteArrayDataOutputStream implements ByteArrayDataOutput {

    final DataOutput output;
    final ByteArrayOutputStream byteArrayOutputStream;

    ByteArrayDataOutputStream(ByteArrayOutputStream byteArrayOutputStream) {
      this.byteArrayOutputStream = byteArrayOutputStream;
      output = new DataOutputStream(byteArrayOutputStream);
    }

 
    @Override
    public void writeUTF(String s) {
      try {
        output.writeUTF(s);
      } catch (IOException impossible) {
        throw new AssertionError(impossible);
      }
    }

    @Override
    public byte[] toByteArray() {
      return byteArrayOutputStream.toByteArray();
    }


	@Override
	public void write(int b) throws IOException {
		
		
	}


	@Override
	public void write(byte[] b) throws IOException {
		
		
	}


	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		
		
	}


	@Override
	public void writeBoolean(boolean v) throws IOException {
		
		
	}


	@Override
	public void writeByte(int v) throws IOException {
		
		
	}


	@Override
	public void writeShort(int v) throws IOException {
		
		
	}


	@Override
	public void writeChar(int v) throws IOException {
		
		
	}


	@Override
	public void writeInt(int v) throws IOException {
		
		
	}


	@Override
	public void writeLong(long v) throws IOException {
		
		
	}


	@Override
	public void writeFloat(float v) throws IOException {
		
		
	}


	@Override
	public void writeDouble(double v) throws IOException {
		
		
	}


	@Override
	public void writeBytes(String s) throws IOException {
		
		
	}


	@Override
	public void writeChars(String s) throws IOException {
		
		
	}
  }

 







}
