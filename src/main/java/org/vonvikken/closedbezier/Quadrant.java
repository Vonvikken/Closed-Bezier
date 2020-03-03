/*
 * Class:     org.vonvikken.closedbezier.Quadrant
 * Author:    Vincenzo Stornanti
 *
 * Copyright 2020 Vincenzo Stornanti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vonvikken.closedbezier;

public enum Quadrant {
    LOWER_RIGHT(0),
    LOWER_LEFT(1),
    UPPER_LEFT(2),
    UPPER_RIGHT(3);

    private final int offset;

    Quadrant(final int offset) {
        this.offset = offset;
    }

    int getOffset() {
        return this.offset;
    }
}
