/*
 * Copyright 2010-2012 napile.org
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

package org.napile.cpp4idea.lang;

import consulo.cpp.preprocessor.parser.InitialParsing;
import org.napile.cpp4idea.lang.parser.parsingMain.MainParsing;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @date 22:23/28.12.12
 */
public abstract class CDialect {
	public static ASTNode parseInitial(PsiBuilder builder, IElementType root) {
		PsiBuilder.Marker rootMarker = builder.mark();

		InitialParsing.parse(builder, InitialParsing.EAT_LAST_END_IF);

		rootMarker.done(root);

		return builder.getTreeBuilt();
	}

	public static ASTNode parseMain(PsiBuilder builder, IElementType root) {
		PsiBuilder.Marker rootMarker = builder.mark();

		MainParsing.parseElement(builder, 0);

		rootMarker.done(root);

		return builder.getTreeBuilt();
	}
}
