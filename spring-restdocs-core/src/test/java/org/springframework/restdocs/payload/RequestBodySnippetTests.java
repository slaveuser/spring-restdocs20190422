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

package org.springframework.restdocs.payload;

import java.io.IOException;

import org.junit.Test;

import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link RequestBodySnippet}.
 *
 * @author Andy Wilkinson
 */
public class RequestBodySnippetTests extends AbstractSnippetTests {

	public RequestBodySnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void requestWithBody() throws IOException {
		requestBody().document(this.operationBuilder.request("http://localhost")
				.content("some content").build());
		assertThat(this.generatedSnippets.snippet("request-body"))
				.is(codeBlock(null, "nowrap").withContent("some content"));
	}

	@Test
	public void requestWithNoBody() throws IOException {
		requestBody().document(this.operationBuilder.request("http://localhost").build());
		assertThat(this.generatedSnippets.snippet("request-body"))
				.is(codeBlock(null, "nowrap").withContent(""));
	}

	@Test
	public void subsectionOfRequestBody() throws IOException {
		requestBody(beneathPath("a.b"))
				.document(this.operationBuilder.request("http://localhost")
						.content("{\"a\":{\"b\":{\"c\":5}}}").build());
		assertThat(this.generatedSnippets.snippet("request-body-beneath-a.b"))
				.is(codeBlock(null, "nowrap").withContent("{\"c\":5}"));
	}

	@Test
	public void customSnippetAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-body"))
				.willReturn(snippetResource("request-body-with-language"));
		requestBody(attributes(key("language").value("json")))
				.document(this.operationBuilder
						.attribute(TemplateEngine.class.getName(),
								new MustacheTemplateEngine(resolver))
						.request("http://localhost").content("{\"a\":\"alpha\"}")
						.build());
		assertThat(this.generatedSnippets.snippet("request-body"))
				.is(codeBlock("json", "nowrap").withContent("{\"a\":\"alpha\"}"));
	}

}
