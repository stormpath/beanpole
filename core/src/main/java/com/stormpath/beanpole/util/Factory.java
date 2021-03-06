/*
 * Copyright 2012 Stormpath
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
package com.stormpath.beanpole.util;

/**
 * Generics-aware interface supporting the
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">Factory Method</a> design pattern.
 *
 * @param <T> The type of the instance returned by the Factory implementation.
 * @since 0.1
 */
public interface Factory<T> {

    /**
     * Returns an instance of the required type.  The implementation determines whether or not a new or cached
     * instance is created every time this method is called.
     *
     * @return an instance of the required type.
     */
    T getInstance();
}
