/*
 * Copyright 2022 Martin Absmeier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package java.structure;

import java.io.Serializable;

/**
 * Implementation of {@link Tree} interface used in JUnit test cases.
 *
 * @author Martin Absmeier
 */
public class OakTree implements Tree, Cloneable {

    private float root;
    private float hight;

    void initialize() {
        this.root = 1.0;
        this.hight = 1.5;
    }

    float getRoot() {
        return root;
    }

    float getHight() {
        return hight;
    }
}