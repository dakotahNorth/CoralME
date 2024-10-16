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
import com.coralblocks.coralme.util.StringUtils;

public enum ExecuteSide implements CharEnum {
    TAKER('T', "Y"),
    MAKER('M', "N");

    private final char b;
    private final String fixCode;
    public static final CharMap<ExecuteSide> ALL = new CharMap<ExecuteSide>();

    static {
        for (ExecuteSide es : ExecuteSide.values()) {
            if (ALL.put(es.getChar(), es) != null)
                throw new IllegalStateException("Duplicate: " + es);
        }
    }

    private ExecuteSide(char b, String fixCode) {
        this.b = b;
        this.fixCode = fixCode;
    }

    public static final ExecuteSide fromFixCode(CharSequence sb) {
        for (ExecuteSide s : ExecuteSide.values()) {
            if (StringUtils.equals(s.getFixCode(), sb)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public final char getChar() {
        return b;
    }

    public final String getFixCode() {
        return fixCode;
    }
}
