package fr.black_eyes.lootchest.googlethings;

/*
 * Copyright (C) 2009 The Guava Authors
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

import java.io.DataOutput;
import java.io.IOException;

/**
 * An extension of {@code DataOutput} for writing to in-memory byte arrays; its methods offer
 * identical functionality but do not throw {@link IOException}.
 *
 * @author Jayaprabhakar Kadarkarai
 * @since 1.0
 */

public interface ByteArrayDataOutput extends DataOutput {

  @Override
  void writeUTF(@NotNull String s);



  /** Returns the contents that have been written to this instance, as a byte array. */
  byte[] toByteArray();
}
