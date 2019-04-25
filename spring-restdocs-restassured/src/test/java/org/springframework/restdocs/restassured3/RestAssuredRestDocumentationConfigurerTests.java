/*
 * Copyright 2014-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.restassured3;

import java.util.List;
import java.util.Map;

import io.restassured.filter.FilterContext;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.snippet.WriterResolver;
import org.springframework.restdocs.templates.TemplateEngine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link RestAssuredRestDocumentationConfigurer}.
 *
 * @author Andy Wilkinson
 * @author Filip Hrisafov
 */
public class RestAssuredRestDocumentationConfigurerTests {

	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	private final FilterableRequestSpecification requestSpec = mock(
			FilterableRequestSpecification.class);

	private final FilterableResponseSpecification responseSpec = mock(
			FilterableResponseSpecification.class);

	private final FilterContext filterContext = mock(FilterContext.class);

	private final RestAssuredRestDocumentationConfigurer configurer = new RestAssuredRestDocumentationConfigurer(
			this.restDocumentation);

	@Test
	public void nextFilterIsCalled() {
		this.configurer.filter(this.requestSpec, this.responseSpec, this.filterContext);
		verify(this.filterContext).next(this.requestSpec, this.responseSpec);
	}

	@Test
	public void configurationIsAddedToTheContext() {
		this.configurer.operationPreprocessors()
				.withRequestDefaults(Preprocessors.prettyPrint())
				.withResponseDefaults(Preprocessors.removeHeaders("Foo"))
				.filter(this.requestSpec, this.responseSpec, this.filterContext);
		@SuppressWarnings("rawtypes")
		ArgumentCaptor<Map> configurationCaptor = ArgumentCaptor.forClass(Map.class);
		verify(this.filterContext).setValue(
				eq(RestDocumentationFilter.CONTEXT_KEY_CONFIGURATION),
				configurationCaptor.capture());
		@SuppressWarnings("unchecked")
		Map<String, Object> configuration = configurationCaptor.getValue();
		assertThat(configuration.get(TemplateEngine.class.getName()))
				.isInstanceOf(TemplateEngine.class);
		assertThat(configuration.get(WriterResolver.class.getName()))
				.isInstanceOf(WriterResolver.class);
		assertThat(configuration
				.get(RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_SNIPPETS))
						.isInstanceOf(List.class);
		assertThat(configuration.get(
				RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_OPERATION_REQUEST_PREPROCESSOR))
						.isInstanceOf(OperationRequestPreprocessor.class);
		assertThat(configuration.get(
				RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_OPERATION_RESPONSE_PREPROCESSOR))
						.isInstanceOf(OperationResponsePreprocessor.class);
	}

}
