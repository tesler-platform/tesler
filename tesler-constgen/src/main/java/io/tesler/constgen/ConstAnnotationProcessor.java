/*-
 * #%L
 * IO Tesler - DTO Constant Generator
 * %%
 * Copyright (C) 2018 - 2019 Tesler Contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package io.tesler.constgen;

import com.google.auto.service.AutoService;
import java.io.Writer;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes({"io.tesler.constgen.GeneratesDtoMetamodel"})
@AutoService(Processor.class)
public class ConstAnnotationProcessor extends AbstractProcessor {

	private static final boolean ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS = false;

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Running " + getClass().getSimpleName());
		if (roundEnv.processingOver() || annotations.isEmpty()) {
			return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
		}
		if (roundEnv.getRootElements() == null || roundEnv.getRootElements().isEmpty()) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "No sources to process");
			return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
		}
		for (TypeElement annotation : annotations) {
			Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
			for (Element element : elements) {
				try {
					TypeElement typeElement = (TypeElement) element;
					CodeGenerator codeGen = new CodeGenerator(
							typeElement, getSuperclass(typeElement), processingEnv.getElementUtils()
					);
					JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
							codeGen.getPackageName() + "." + codeGen.getClassName()
					);
					try (Writer writer = jfo.openWriter()) {
						codeGen.generate().writeTo(writer);
					}
				} catch (Exception e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
				}
			}
		}
		return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
	}

	private Element getSuperclass(final TypeElement typeElement) {
		final TypeMirror superclass = typeElement.getSuperclass();
		if (superclass != null && superclass.getKind() == TypeKind.DECLARED) {
			final Element superclassElement = ((DeclaredType) superclass).asElement();
			if (superclassElement.getAnnotation(GeneratesDtoMetamodel.class) != null) {
				return superclassElement;
			}
		}
		return null;
	}

}
