package io.github.honhimw.scn.discovery.registry;

import io.github.honhimw.scn.discovery.event.NacosDiscoveryInfoChangedEvent;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationListener;

/// @author honhimW
/// @since 2025-12-25
public class NacosDiscoveryInfoChangedListener implements ApplicationListener<@NonNull NacosDiscoveryInfoChangedEvent> {

	private final NacosAutoServiceRegistration nacosAutoServiceRegistration;

	public NacosDiscoveryInfoChangedListener(NacosAutoServiceRegistration nacosAutoServiceRegistration) {
		this.nacosAutoServiceRegistration = nacosAutoServiceRegistration;
	}

	@Override
	public void onApplicationEvent(NacosDiscoveryInfoChangedEvent event) {
		nacosAutoServiceRegistration.stop();
		nacosAutoServiceRegistration.start();
	}
}
