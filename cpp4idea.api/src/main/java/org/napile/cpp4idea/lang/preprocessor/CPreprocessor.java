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

package org.napile.cpp4idea.lang.preprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.napile.cpp4idea.config.facet.CFacetUtil;
import org.napile.cpp4idea.lang.CDialect;
import org.napile.cpp4idea.lang.parser.parsingMain.builder.CMainPsiBuilder;
import org.napile.cpp4idea.lang.psi.CPsiFile;
import org.napile.cpp4idea.lang.psi.CTokens;
import org.napile.cpp4idea.lang.psiInitial.CPsiCompilerVariable;
import org.napile.cpp4idea.lang.psiInitial.CPsiSharpDefine;
import org.napile.cpp4idea.lang.psiInitial.CPsiSharpDefineValue;
import org.napile.cpp4idea.lang.psiInitial.CPsiSharpFile;
import org.napile.cpp4idea.lang.psiInitial.CPsiSharpIfBody;
import org.napile.cpp4idea.lang.psiInitial.CPsiSharpIfDef;
import org.napile.cpp4idea.lang.psiInitial.CPsiSharpInclude;
import org.napile.cpp4idea.lang.psiInitial.CPsiSharpIndepInclude;
import org.napile.cpp4idea.lang.psiInitial.visitors.CSharpPsiElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @date 11:07/30.12.12
 */
public class CPreprocessor
{
	public static Key<CPsiFile> C_PROCESSED_FILE = Key.create("c-processed-file");
	public static Key<Boolean> ACTIVE_BLOCK = Key.create("c-active-block");

	@Nullable
	public static CPsiFile getAfterProcessedFile(PsiElement psiElement)
	{
		return preProcess((CPsiSharpFile) psiElement.getContainingFile());
	}

	@Nullable
	public static CPsiFile preProcess(CPsiSharpFile element)
	{
		CPsiFile psiFile = element.getUserData(C_PROCESSED_FILE);
		if(psiFile != null)
			return psiFile;

		CDialect dialect = CFacetUtil.findDialect(element);
		if(dialect == null)
			return null;

		final List<PsiElement> elements = new ArrayList<PsiElement>();
		final Map<String, List<PsiElement>> define = new HashMap<String, List<PsiElement>>();

		CSharpPsiElementVisitor recursiveElementVisitor = new CSharpPsiElementVisitor()
		{
			@Override
			public void visitSFile(CPsiSharpFile file)
			{
				file.acceptChildren(this);
			}

			@Override
			public void visitElement(PsiElement element)
			{
				if(element.getNode().getElementType() == CTokens.IDENTIFIER)
				{
					List<PsiElement> defineElements = define.get(element.getText());
					if(defineElements != null)
					{
						elements.addAll(defineElements);
						return;
					}
				}
				elements.add(element);
			}

			@Override
			public void visitSDefine(CPsiSharpDefine element)
			{
				CPsiCompilerVariable variable = element.getVariable();
				CPsiSharpDefineValue value = element.getValue();
				if(variable != null && value != null)
					define.put(variable.getText(), collectChildren(value));
			}

			@Override
			public void visitSIfDef(CPsiSharpIfDef element)
			{
				CPsiCompilerVariable variable = element.getVariable();
				CPsiSharpIfBody body = element.getBody();
				if(body != null)
				{
					if(variable != null && (define.containsKey(variable.getText()) && !element.isReverted() || !define.containsKey(variable.getText()) && element.isReverted()))
					{
						element.putUserData(ACTIVE_BLOCK, Boolean.TRUE);
						body.acceptChildren(this);
						return;
					}

					element.putUserData(ACTIVE_BLOCK, Boolean.FALSE);
				}
			}

			@Override
			public void visitSInclude(CPsiSharpInclude element)
			{

			}

			@Override
			public void visitSIndependInclude(CPsiSharpIndepInclude element)
			{

			}
		};
		element.accept(recursiveElementVisitor);

		CMainPsiBuilder builder = new CMainPsiBuilder(element.getProject(), elements);

		ASTNode node = dialect.parseMain(builder, CTokens.C_PROCESSED_FILE_TYPE);

		node.putUserData(CPsiFile.C_SHARP_FILE, element);

		psiFile = (CPsiFile) node.getPsi();
		element.putUserData(C_PROCESSED_FILE, psiFile);
		return psiFile;
	}

	private static List<PsiElement> collectChildren(PsiElement e)
	{
		List<PsiElement> list = new ArrayList<PsiElement>();
		PsiElement child = e.getFirstChild();
		while(child != null)
		{
			list.add(child);
			child = child.getNextSibling();
		}
		return list;
	}
}
