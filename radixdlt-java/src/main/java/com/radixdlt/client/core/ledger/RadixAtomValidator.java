package com.radixdlt.client.core.ledger;

import com.radixdlt.client.assets.Asset;
import com.radixdlt.client.core.address.EUID;
import com.radixdlt.client.core.atoms.AbstractConsumable;
import com.radixdlt.client.core.atoms.Atom;
import com.radixdlt.client.core.atoms.AtomValidationException;
import com.radixdlt.client.core.atoms.AtomValidator;
import com.radixdlt.client.core.atoms.Consumer;
import com.radixdlt.client.core.atoms.Particle;
import com.radixdlt.client.core.atoms.RadixHash;
import com.radixdlt.client.core.crypto.ECSignature;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class RadixAtomValidator implements AtomValidator {
	private final static RadixAtomValidator validator = new RadixAtomValidator();

	public static RadixAtomValidator getInstance() {
		return validator;
	}

	private RadixAtomValidator() {
	}

	/**
	 * Checks the owners of each AbstractConsumable particle and makes sure the
	 * atom contains signatures for each owner
	 *
	 * @param atom atom to validate
	 * @throws AtomValidationException if atom has missing/bad signatures for a particle
	 */
	public void validateSignatures(Atom atom) throws AtomValidationException {
		RadixHash hash = atom.getHash();

		Optional<AtomValidationException> exception = atom.getParticles().stream()
			.filter(Particle::isAbstractConsumable)
			.map(Particle::getAsAbstractConsumable)
			.map(particle -> {
				if (particle.getOwners().isEmpty()) {
					return new AtomValidationException("No owners in particle");
				}

				if (particle.getAssetId().equals(Asset.POW.getId())) {
					return null;
				}

				if (particle instanceof Consumer) {
					Optional<AtomValidationException> consumerException = particle.getOwners().stream().map(owner -> {
						Optional<ECSignature> signature = atom.getSignature(owner.getUID());
						if (!signature.isPresent()) {
							return new AtomValidationException("Missing signature");
						}

						if (!hash.verifySelf(owner, signature.get())) {
							return new AtomValidationException("Bad signature");
						}

						return null;
					}).filter(Objects::nonNull).findAny();

					if (consumerException.isPresent()) {
						return consumerException.get();
					}
				}

				return null;
		})
		.filter(Objects::nonNull)
		.findAny();

		if (exception.isPresent()) {
			throw exception.get();
		}
	}

	public void validate(Atom atom) throws AtomValidationException{
		// TODO: check with universe genesis timestamp
		if (atom.getTimestamp() == null || atom.getTimestamp() == 0L) {
			throw new AtomValidationException("Null or Zero Timestamp");
		}

		validateSignatures(atom);
	}
}
