/*
 * Copyright 2023 (c) CoralBlocks - http://www.coralblocks.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.coralblocks.coralme;

import com.coralblocks.coralme.util.CharEnum;
import com.coralblocks.coralme.util.CharMap;

public enum ReduceRejectReason implements CharEnum {
    ZERO('Z'),
    NEGATIVE('N'),
    INCREASE('I'),
    SUPERFLUOUS('S'),
    NOT_FOUND('F');

    private final char b;
    public static final CharMap<ReduceRejectReason> ALL = new CharMap<ReduceRejectReason>();

    static {
        for (ReduceRejectReason rrr : ReduceRejectReason.values()) {
            if (ALL.put(rrr.getChar(), rrr) != null)
                throw new IllegalStateException("Duplicate: " + rrr);
        }
    }

    private ReduceRejectReason(char b) {
        this.b = b;
    }

    @Override
    public final char getChar() {
        return b;
    }
}
