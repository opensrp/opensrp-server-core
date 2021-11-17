package org.opensrp.domain;

import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.api.client.util.Key;
import org.apache.commons.codec.binary.Base64;

public class AttestationStatement extends JsonWebSignature.Payload {
	@Key
	private String nonce;

	@Key
	private long timestampMs;

	@Key
	private String apkPackageName;

	@Key
	private String[] apkCertificateDigestSha256;

	@Key
	private String apkDigestSha256;

	@Key
	private boolean ctsProfileMatch;

	@Key
	private boolean basicIntegrity;

	public byte[] getNonce() {
		return Base64.decodeBase64(nonce);
	}

	public long getTimestampMs() {
		return timestampMs;
	}

	public String getApkPackageName() {
		return apkPackageName;
	}

	public byte[] getApkDigestSha256() {
		return Base64.decodeBase64(apkDigestSha256);
	}

	public byte[][] getApkCertificateDigestSha256() {
		byte[][] certs = new byte[apkCertificateDigestSha256.length][];
		for (int i = 0; i < apkCertificateDigestSha256.length; i++) {
			certs[i] = Base64.decodeBase64(apkCertificateDigestSha256[i]);
		}
		return certs;
	}

	public boolean isCtsProfileMatch() {
		return ctsProfileMatch;
	}

	public boolean hasBasicIntegrity() {
		return basicIntegrity;
	}

}
