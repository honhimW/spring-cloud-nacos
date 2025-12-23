package io.github.honhimw.scn.cfg;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.TaskScheduler;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/// @author honhimW
/// @since 2025-12-22
public class ConfigWatch implements ApplicationEventPublisherAware, SmartLifecycle {

	private static final Logger log = LoggerFactory.getLogger(ConfigWatch.class);

	private final NacosConfigProperties properties;

	private final ConfigService nacos;

	private final AtomicBoolean running = new AtomicBoolean(false);

	private static final AtomicLong REFRESH_COUNT = new AtomicLong(0);

	private final Map<String, Listener> listenerMap = new ConcurrentHashMap<>(16);

	private ApplicationEventPublisher publisher;

	public ConfigWatch(NacosConfigProperties properties, ConfigService nacos) {
		this.properties = properties;
		this.nacos = nacos;
	}

	@Override
	public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
		this.publisher = applicationEventPublisher;
	}

	@Override
	public void start() {
		if (this.running.compareAndSet(false, true)) {
			this.registerNacosListenersForApplications();
		}
	}

	@Override
	public void stop() {

	}

	@Override
	public boolean isRunning() {
		return running.get();
	}

	/**
	 * register Nacos Listeners.
	 */
	private void registerNacosListenersForApplications() {
	}

}
