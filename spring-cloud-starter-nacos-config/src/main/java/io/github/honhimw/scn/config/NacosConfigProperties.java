package io.github.honhimw.scn.config;

import io.github.honhimw.nacos.api.PropertyKeyConst;
import io.github.honhimw.nacos.api.config.ConfigService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.*;

/// @author honhimW
/// @since 2025-12-22
@ConfigurationProperties(NacosConfigProperties.PREFIX)
@Validated
public class NacosConfigProperties {

	/**
	 * Prefix for configuration properties.
	 */
	public static final String PREFIX = "spring.cloud.nacos.config";

	private boolean enabled = true;

	private List<String> prefixes = new ArrayList<>(Collections.singletonList("config"));

	/**
	 * nacos config server address.
	 */
	private String serverAddr = "localhost:8848";
	/**
	 * the nacos authentication username.
	 */
	private String username;
	/**
	 * the nacos authentication password.
	 */
	private String password;
	/**
	 * encode for nacos config content.
	 */
	private String encode;
	/**
	 * nacos config group, group is config data meta info.
	 */
	private String group = "DEFAULT_GROUP";
	/**
	 * nacos config dataId prefix.
	 */
	private String prefix;
	/**
	 * the suffix of nacos config dataId, also the file extension of config content.
	 */
	private String fileExtension = "properties";
	/**
	 * timeout for get config from nacos.
	 */
	private int timeout = 3000;
	/**
	 * nacos maximum number of tolerable server reconnection errors.
	 */
	private String maxRetry;
	/**
	 * nacos get config long poll timeout.
	 */
	private String configLongPollTimeout;
	/**
	 * nacos get config failure retry time.
	 */
	private String configRetryTime;
	/**
	 * If you want to pull it yourself when the program starts to get the configuration
	 * for the first time, and the registered Listener is used for future configuration
	 * updates, you can keep the original code unchanged, just add the system parameter:
	 * enableRemoteSyncConfig = "true" ( But there is network overhead); therefore we
	 * recommend that you use {@link ConfigService#getConfigAndSignListener} directly.
	 */
	private boolean enableRemoteSyncConfig = false;
	/**
	 * endpoint for Nacos, the domain name of a service, through which the server address
	 * can be dynamically obtained.
	 */
	private String endpoint;
	/**
	 * namespace, separation configuration of different environments.
	 */
	private String namespace;
	/**
	 * access key for namespace.
	 */
	private String accessKey;
	/**
	 * secret key for namespace.
	 */
	private String secretKey;
	/**
	 * role name for aliyun ram.
	 */
	private String ramRoleName;
	/**
	 * context path for nacos config server.
	 */
	private String contextPath;
	/**
	 * nacos config cluster name.
	 */
	private String clusterName;
	/**
	 * nacos config dataId name.
	 */
	private String name;
	/**
	 * the master switch for refresh configuration, it default opened(true).
	 */
	private boolean refreshEnabled = true;

	public boolean isEnabled() {
		return enabled;
	}

	public NacosConfigProperties setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public List<String> getPrefixes() {
		return prefixes;
	}

	public NacosConfigProperties setPrefixes(List<String> prefixes) {
		this.prefixes = prefixes;
		return this;
	}

	public String getServerAddr() {
		return serverAddr;
	}

	public NacosConfigProperties setServerAddr(String serverAddr) {
		this.serverAddr = serverAddr;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public NacosConfigProperties setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public NacosConfigProperties setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getEncode() {
		return encode;
	}

	public NacosConfigProperties setEncode(String encode) {
		this.encode = encode;
		return this;
	}

	public String getGroup() {
		return group;
	}

	public NacosConfigProperties setGroup(String group) {
		this.group = group;
		return this;
	}

	public String getPrefix() {
		return prefix;
	}

	public NacosConfigProperties setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public NacosConfigProperties setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
		return this;
	}

	public int getTimeout() {
		return timeout;
	}

	public NacosConfigProperties setTimeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	public String getMaxRetry() {
		return maxRetry;
	}

	public NacosConfigProperties setMaxRetry(String maxRetry) {
		this.maxRetry = maxRetry;
		return this;
	}

	public String getConfigLongPollTimeout() {
		return configLongPollTimeout;
	}

	public NacosConfigProperties setConfigLongPollTimeout(String configLongPollTimeout) {
		this.configLongPollTimeout = configLongPollTimeout;
		return this;
	}

	public String getConfigRetryTime() {
		return configRetryTime;
	}

	public NacosConfigProperties setConfigRetryTime(String configRetryTime) {
		this.configRetryTime = configRetryTime;
		return this;
	}

	public boolean isEnableRemoteSyncConfig() {
		return enableRemoteSyncConfig;
	}

	public NacosConfigProperties setEnableRemoteSyncConfig(boolean enableRemoteSyncConfig) {
		this.enableRemoteSyncConfig = enableRemoteSyncConfig;
		return this;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public NacosConfigProperties setEndpoint(String endpoint) {
		this.endpoint = endpoint;
		return this;
	}

	public String getNamespace() {
		return namespace;
	}

	public NacosConfigProperties setNamespace(String namespace) {
		this.namespace = namespace;
		return this;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public NacosConfigProperties setAccessKey(String accessKey) {
		this.accessKey = accessKey;
		return this;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public NacosConfigProperties setSecretKey(String secretKey) {
		this.secretKey = secretKey;
		return this;
	}

	public String getRamRoleName() {
		return ramRoleName;
	}

	public NacosConfigProperties setRamRoleName(String ramRoleName) {
		this.ramRoleName = ramRoleName;
		return this;
	}

	public String getContextPath() {
		return contextPath;
	}

	public NacosConfigProperties setContextPath(String contextPath) {
		this.contextPath = contextPath;
		return this;
	}

	public String getClusterName() {
		return clusterName;
	}

	public NacosConfigProperties setClusterName(String clusterName) {
		this.clusterName = clusterName;
		return this;
	}

	public String getName() {
		return name;
	}

	public NacosConfigProperties setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isRefreshEnabled() {
		return refreshEnabled;
	}

	public NacosConfigProperties setRefreshEnabled(boolean refreshEnabled) {
		this.refreshEnabled = refreshEnabled;
		return this;
	}

	/**
	 * assemble properties for configService. (cause by rename : Remove the interference
	 * of auto prompts when writing,because autocue is based on get method.
	 *
	 * @return properties
	 */
	public Properties assembleConfigServiceProperties() {
		Properties props = new Properties();
		props.put(PropertyKeyConst.SERVER_ADDR, Objects.toString(getServerAddr(), ""));
		props.put(PropertyKeyConst.USERNAME, Objects.toString(getUsername(), ""));
		props.put(PropertyKeyConst.PASSWORD, Objects.toString(getPassword(), ""));
		props.put(PropertyKeyConst.ENCODE, Objects.toString(getEncode(), ""));
		props.put(PropertyKeyConst.NAMESPACE, Objects.toString(getNamespace(), ""));
		props.put(PropertyKeyConst.ACCESS_KEY, Objects.toString(getAccessKey(), ""));
		props.put(PropertyKeyConst.SECRET_KEY, Objects.toString(getSecretKey(), ""));
		props.put(PropertyKeyConst.RAM_ROLE_NAME, Objects.toString(getRamRoleName(), ""));
		props.put(PropertyKeyConst.ENDPOINT_CLUSTER_NAME, Objects.toString(getClusterName(), ""));
		props.put(PropertyKeyConst.MAX_RETRY, Objects.toString(getMaxRetry(), ""));
		props.put(PropertyKeyConst.CONFIG_LONG_POLL_TIMEOUT, Objects.toString(getConfigLongPollTimeout(), ""));
		props.put(PropertyKeyConst.CONFIG_RETRY_TIME, Objects.toString(getConfigRetryTime(), ""));
		props.put(PropertyKeyConst.ENABLE_REMOTE_SYNC_CONFIG, Objects.toString(isEnableRemoteSyncConfig(), ""));
		String endpoint = Objects.toString(getEndpoint(), "");
		String[] endpointPart = endpoint.split(":", 2);
		if (endpointPart.length == 2) {
			props.put(PropertyKeyConst.ENDPOINT, endpointPart[0]);
			props.put(PropertyKeyConst.ENDPOINT_PORT, endpointPart[1]);
		} else {
			props.put(PropertyKeyConst.ENDPOINT, endpoint);
		}
		return props;
	}

	@Override
	public String toString() {
		return "NacosConfigProperties{" +
			   "enabled=" + enabled +
			   ", prefixes=" + prefixes +
			   ", serverAddr='" + serverAddr + '\'' +
			   ", username='" + username + '\'' +
			   ", password='" + password + '\'' +
			   ", encode='" + encode + '\'' +
			   ", group='" + group + '\'' +
			   ", prefix='" + prefix + '\'' +
			   ", fileExtension='" + fileExtension + '\'' +
			   ", timeout=" + timeout +
			   ", maxRetry='" + maxRetry + '\'' +
			   ", configLongPollTimeout='" + configLongPollTimeout + '\'' +
			   ", configRetryTime='" + configRetryTime + '\'' +
			   ", enableRemoteSyncConfig=" + enableRemoteSyncConfig +
			   ", endpoint='" + endpoint + '\'' +
			   ", namespace='" + namespace + '\'' +
			   ", accessKey='" + accessKey + '\'' +
			   ", secretKey='" + secretKey + '\'' +
			   ", ramRoleName='" + ramRoleName + '\'' +
			   ", contextPath='" + contextPath + '\'' +
			   ", clusterName='" + clusterName + '\'' +
			   ", name='" + name + '\'' +
			   ", refreshEnabled=" + refreshEnabled +
			   '}';
	}

}
