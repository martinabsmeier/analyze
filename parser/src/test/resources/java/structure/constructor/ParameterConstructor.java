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
package java.structure.constructor;

/**
 * Constructors with parameters to check if we process this correct.
 *
 * @author Martin Absmeier
 */
public class ParameterConstructor<T extends Number> {

    private T count;

    public ParameterConstructor(Number count) {
        this.count = count;
    }

    public ParameterConstructor(T count) {
        this.count = count;
    }
}