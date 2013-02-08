/*
 * Copyright 2011 napile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.napile.cpp4idea.util;

import javax.swing.Icon;

import com.intellij.openapi.util.IconLoader;

/**
 * @author VISTALL
 * @date 2:29/10.12.2011
 */
public interface CIcons
{
	public static final Icon SOURCE_FILE = IconLoader.getIcon("/org/napile/cpp4idea/icons/types/c.png");
	public static final Icon HEADER_FILE = IconLoader.getIcon("/org/napile/cpp4idea/icons/types/h.png");
}