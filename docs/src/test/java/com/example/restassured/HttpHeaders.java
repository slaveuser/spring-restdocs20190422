/*
 * Copyright 2014-2017 the original author or authors.
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

package com.example.restassured;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class HttpHeaders {

	private RequestSpecification spec;

	public void headers() throws Exception {
		// tag::headers[]
		RestAssured.given(this.spec)
			.filter(document("headers",
					requestHeaders( // <1>
							headerWithName("Authorization").description(
									"Basic auth credentials")), // <2>
					responseHeaders( // <3>
							headerWithName("X-RateLimit-Limit").description(
									"The total number of requests permitted per period"),
							headerWithName("X-RateLimit-Remaining").description(
									"Remaining requests permitted in current period"),
							headerWithName("X-RateLimit-Reset").description(
									"Time at which the rate limit period will reset"))))
			.header("Authorization", "Basic dXNlcjpzZWNyZXQ=") // <4>
			.when().get("/people")
			.then().assertThat().statusCode(is(200));
		// end::headers[]
	}
}
