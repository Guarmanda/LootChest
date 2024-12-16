package fr.black_eyes.lootchest.googlethings;

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




import org.jetbrains.annotations.NotNull;


import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * Provides utility methods for working with byte arrays and I/O streams.
 *
 * @author Chris Nokleberg
 * @author Colin Decker
 * @since 1.0
 */

public final class ByteStreams {

  private ByteStreams() {}
  




  /** Returns a new {@link ByteArrayDataOutput} instance with a default size. */
  
  public static ByteArrayDataOutput newDataOutput() {
    return newDataOutput(new ByteArrayOutputStream());
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
    public void writeUTF(@NotNull String s) {
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
	public void write(byte @NotNull [] b) throws IOException {
		
		
	}


	@Override
	public void write(byte @NotNull [] b, int off, int len) throws IOException {
		
		
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
	public void writeBytes(@NotNull String s) throws IOException {
		
		
	}


	@Override
	public void writeChars(@NotNull String s) throws IOException {


	}
  }

 







}
