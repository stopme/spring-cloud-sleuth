/*
 * Copyright 2013-2017 the original author or authors.
 *
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
 */

package org.springframework.cloud.sleuth.instrument.web.client;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.springframework.cloud.sleuth.*;
import org.springframework.cloud.sleuth.log.NoOpSpanLogger;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.sleuth.trace.DefaultTracer;
import org.springframework.core.task.AsyncListenableTaskExecutor;

import java.util.Random;
import java.util.concurrent.Callable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

/**
 * @author Marcin Grzejszczak
 */
public class TraceAsyncListenableTaskExecutorTest {

	AsyncListenableTaskExecutor delegate = mock(AsyncListenableTaskExecutor.class);
	Tracer tracer = new DefaultTracer(new AlwaysSampler(), new Random(),
			new DefaultSpanNamer(), new NoOpSpanLogger(), new NoOpSpanReporter(), new TraceKeys()) {
		@Override
		public boolean isTracing() {
			return true;
		}
	};
	TraceAsyncListenableTaskExecutor traceAsyncListenableTaskExecutor = new TraceAsyncListenableTaskExecutor(
			this.delegate, this.tracer);

	@Test
	public void should_submit_listenable_trace_runnable() throws Exception {
		this.traceAsyncListenableTaskExecutor.submitListenable(aRunnable());

		BDDMockito.then(this.delegate).should().submitListenable(any(TraceRunnable.class));
	}

	@Test
	public void should_submit_listenable_trace_callable() throws Exception {
		this.traceAsyncListenableTaskExecutor.submitListenable(aCallable());

		BDDMockito.then(this.delegate).should().submitListenable(any(TraceCallable.class));
	}

	@Test
	public void should_execute_a_trace_runnable() throws Exception {
		this.traceAsyncListenableTaskExecutor.execute(aRunnable());

		BDDMockito.then(this.delegate).should().execute(any(TraceRunnable.class));
	}

	@Test
	public void should_execute_with_timeout_a_trace_runnable() throws Exception {
		this.traceAsyncListenableTaskExecutor.execute(aRunnable(), 1L);

		BDDMockito.then(this.delegate).should().execute(any(TraceRunnable.class),
				BDDMockito.anyLong());
	}

	@Test
	public void should_submit_trace_callable() throws Exception {
		this.traceAsyncListenableTaskExecutor.submit(aCallable());

		BDDMockito.then(this.delegate).should().submit(any(TraceCallable.class));
	}

	@Test
	public void should_submit_trace_runnable() throws Exception {
		this.traceAsyncListenableTaskExecutor.submit(aRunnable());

		BDDMockito.then(this.delegate).should().submit(any(TraceRunnable.class));
	}

	Runnable aRunnable() {
		return () -> {

		};
	}

	Callable<?> aCallable() {
		return () -> null;
	}
}