package com.radixdlt.client.core.address;

import java.io.File;

public final class RadixUniverseConfigs {

	private RadixUniverseConfigs() {}

	public static final RadixUniverseConfig getWinterfell() {
		return RadixUniverseConfig.fromFile(getConfigFile("testuniverse.json"));
	}

	public static final RadixUniverseConfig getSunstone() {
		return RadixUniverseConfig.fromFile(getConfigFile("testuniverse.json"));
	}

	public static final RadixUniverseConfig getHighgarden() {
		return RadixUniverseConfig.fromFile(getConfigFile("highgarden.json"));
	}

	public static final RadixUniverseConfig getAlphanet() {
		return RadixUniverseConfig.fromFile(getConfigFile("alphanet.json"));
	}
	private static File getConfigFile(String name){
		return new File(RadixUniverseConfig.class.getResource("/configs/bootstrap/" + name).getFile());
	}
}
