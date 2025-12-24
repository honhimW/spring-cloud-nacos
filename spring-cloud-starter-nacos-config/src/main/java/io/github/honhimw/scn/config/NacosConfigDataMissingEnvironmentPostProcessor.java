/*
 * Copyright 2015-present the original author or authors.
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

package io.github.honhimw.scn.config;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.cloud.commons.ConfigDataMissingEnvironmentPostProcessor;
import org.springframework.core.env.Environment;

import static io.github.honhimw.scn.config.configdata.NacosConfigDataLocationResolver.PREFIX;
import static org.springframework.cloud.util.PropertyUtils.bootstrapEnabled;
import static org.springframework.cloud.util.PropertyUtils.useLegacyProcessing;

public class NacosConfigDataMissingEnvironmentPostProcessor extends ConfigDataMissingEnvironmentPostProcessor {

	/**
	 * Order of post processor, set to run after
	 * {@link ConfigDataEnvironmentPostProcessor}.
	 */
	public static final int ORDER = ConfigDataEnvironmentPostProcessor.ORDER + 1000;

	@Override
	public int getOrder() {
		return ORDER;
	}

	@Override
	protected boolean shouldProcessEnvironment(@NonNull Environment environment) {
		// don't run if using bootstrap or legacy processing
		if (bootstrapEnabled(environment) || useLegacyProcessing(environment)) {
			return false;
		}
		boolean coreEnabled = environment.getProperty(NacosConfigProperties.PREFIX + ".enabled", Boolean.class, true);
		boolean importCheckEnabled = environment.getProperty(NacosConfigProperties.PREFIX + ".import-check.enabled", Boolean.class, true);
		return coreEnabled && importCheckEnabled;
	}

	@NonNull
	@Override
	protected String getPrefix() {
		return PREFIX;
	}

	static class ImportExceptionFailureAnalyzer extends AbstractFailureAnalyzer<@NonNull ImportException> {

		@Override
		protected FailureAnalysis analyze(@NonNull Throwable rootFailure, ImportException cause) {
			String description;
			if (cause.missingPrefix) {
				description = "The spring.config.import property is missing a " + PREFIX + " entry";
			} else {
				description = "No spring.config.import property has been defined";
			}
			String action = "Add a spring.config.import=nacos: property to your configuration.\n"
							+ "\tIf configuration is not required add spring.config.import=optional:nacos: instead.\n"
							+ "\tTo disable this check, set spring.cloud.nacos.config.import-check.enabled=false.";
			return new FailureAnalysis(description, action, cause);
		}

	}

}
