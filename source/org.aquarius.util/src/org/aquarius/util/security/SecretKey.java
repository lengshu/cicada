/**
 *
 */
package org.aquarius.util.security;

/**
 *
 * The security key model.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SecretKey {

	public String algorithm;

	public byte[] iv;

	public byte[] key;

	/**
	 * Check it to make sure the are valid.<BR>
	 */
	public void check() {
		this.iv = check(this.iv);
		this.key = check(this.key);
	}

	/**
	 * Check it to make sure the are valid.<BR>
	 * 
	 * @param bytes it's length should be >=16, or use 0 to fill.<BR>
	 * @return
	 */
	private byte[] check(byte[] bytes) {
		if (bytes.length >= 16) {
			return bytes;
		} else {
			byte[] newBytes = new byte[16];
			System.arraycopy(bytes, 0, newBytes, (16 - bytes.length), bytes.length);

			return newBytes;
		}
	}

}
