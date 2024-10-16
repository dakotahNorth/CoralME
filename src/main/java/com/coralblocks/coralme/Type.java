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

public enum Type implements CharEnum {
    MARKET('M', "1"),
    LIMIT('L', "2");

    private final char b;
    private final String fixCode;
    public static final CharMap<Type> ALL = new CharMap<Type>();

    static {
        for (Type t : Type.values()) {
            if (ALL.put(t.getChar(), t) != null) throw new IllegalStateException("Duplicate: " + t);
        }
    }

    private Type(char b, String fixCode) {
        this.b = b;
        this.fixCode = fixCode;
    }

    public static final Type fromFixCode(CharSequence sb) {
        for (Type s : Type.values()) {
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
