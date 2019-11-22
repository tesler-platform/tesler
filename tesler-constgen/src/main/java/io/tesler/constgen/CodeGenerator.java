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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import lombok.Getter;

class CodeGenerator {

	private final TypeElement typeElement;

	private final Element superclass;

	private final Elements elements;

	@Getter
	private final String packageName;

	@Getter
	private final String className;

	CodeGenerator(TypeElement typeElement, Element superclass, Elements elements) {
		this.typeElement = typeElement;
		this.superclass = superclass;
		this.elements = elements;
		this.packageName = elements.getPackageOf(typeElement).getQualifiedName().toString();
		this.className = typeElement.getSimpleName() + "_";
	}

	private static boolean isStatic(VariableElement el) {
		return el.getModifiers().contains(Modifier.STATIC);
	}

	JavaFile generate() {
		Builder classBuilder = TypeSpec.classBuilder(className);
		if (superclass != null) {
			classBuilder.superclass(ClassName.get(
					elements.getPackageOf(superclass).getQualifiedName().toString(),
					superclass.getSimpleName() + "_"
			));
		}
		classBuilder.addModifiers(Modifier.PUBLIC);
		for (Constant constant : collectFields()) {
			ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
					ClassName.get(DtoField.class), TypeName.get(typeElement.asType()), constant.getType()
			);
			FieldSpec fieldSpec = FieldSpec.builder(parameterizedTypeName, constant.getName())
					.addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
					.initializer("new DtoField($S)", constant.getName())
					.build();
			classBuilder.addField(fieldSpec);
		}
		return JavaFile.builder(packageName, classBuilder.build()).build();
	}

	private List<Constant> collectFields() {
		List<Constant> fields = new ArrayList<>();
		for (Element el : typeElement.getEnclosedElements()) {
			if (el.getKind() == ElementKind.FIELD) {
				VariableElement varEl = (VariableElement) el;
				if (!isTransient(varEl) && !isStatic(varEl)) {
					fields.add(new Constant(el.getSimpleName().toString(), TypeName.get(varEl.asType()).box()));
				}
			}
		}
		Collections.sort(fields);
		return fields;
	}

	private boolean isTransient(VariableElement el) {
		for (AnnotationMirror am : elements.getAllAnnotationMirrors(el)) {
			Name qualifiedName = ((TypeElement) am.getAnnotationType().asElement()).getQualifiedName();
			if (qualifiedName.contentEquals("io.tesler.constgen.DtoMetamodelIgnore")) {
				return true;
			}
		}
		return false;
	}

}
