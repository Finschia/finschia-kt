package network.finschia.sdk.crypto;

import static network.finschia.sdk.crypto.Amino.addAminoPrefix;
import static network.finschia.sdk.crypto.Amino.removeAminoPrefix;
import static network.finschia.sdk.crypto.SecureRandomUtils.secureRandom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.output.WriterOutputStream;
import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.jcajce.provider.digest.SHA256.Digest;
import org.bouncycastle.openpgp.PGPUtil;

import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

import at.favre.lib.crypto.bcrypt.BCrypt;

public final class KeyStore {
    private static final int BCRYPT_SECURITY_PARAMETER = 12;
    private static final int SALT_BYTES_SIZE = 16;
    private static final String PRIV_KEY_TYPE_SECP256K1 = "tendermint/PrivKeySecp256k1";
    private static final Charset US_ASCII_CHARSET = StandardCharsets.US_ASCII;
    private static final BaseEncoding BASE16 = BaseEncoding.base16();

    static final String KDF_TYPE = "bcrypt";
    final String kdf;
    final byte[] armoredPrivateKey;
    final byte[] salt;

    private KeyStore(String kdf, byte[] salt, byte[] armoredPrivateKey) {
        if (!KDF_TYPE.equals(kdf)) {
            throw new IllegalArgumentException(String.format("Unrecognized KDF type: %s", kdf));
        }
        if (salt.length == 0) {
            throw new IllegalArgumentException("Missing salt bytes");
        }
        this.kdf = kdf;
        this.salt = salt;
        this.armoredPrivateKey = armoredPrivateKey;
    }

    public byte[] getPrivateKey(String passphrase) {
        return unarmorPrivateKey(passphrase);
    }

    private byte[] unarmorPrivateKey(String password) {
        final byte[] bcryptKey = BCrypt.withDefaults().hash(BCRYPT_SECURITY_PARAMETER, salt,
                                                            password.getBytes(StandardCharsets.UTF_8));
        final byte[] encryptedKey = new Digest().digest(bcryptKey);
        final byte[] privateKeyBytes = Xsalsa20Symmetric.decryptSymmetric(armoredPrivateKey, encryptedKey);
        return removeAminoPrefix(privateKeyBytes);
    }

    public String export() {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            export(out);
            return new String(out.toByteArray(), US_ASCII_CHARSET);

        } catch (Exception e) {
            throw new RuntimeException("Failed to export keystore", e);
        }
    }

    public void export(Writer writer) {
        try (final OutputStream out = new WriterOutputStream(writer, US_ASCII_CHARSET)) {
            export(out);

        } catch (Exception e) {
            throw new RuntimeException("Failed to export keystore", e);
        }
    }

    public void export(OutputStream out) {
        try (final TendermintArmoredOutputStream tOut = new TendermintArmoredOutputStream(out)
        ) {
            tOut.setHeader("salt", BASE16.lowerCase().encode(salt));
            tOut.setHeader("kdf", kdf);
            tOut.write(armoredPrivateKey);

        } catch (Exception e) {
            throw new RuntimeException("Failed to export keystore", e);
        }
    }

    public static KeyStore load(Reader keyStoreData) {
        try (final InputStream in = new ByteArrayInputStream(
                CharStreams.toString(keyStoreData).getBytes(US_ASCII_CHARSET))) {
            return decode(in);

        } catch (Exception e) {
            throw new RuntimeException("Failed to decode keystore data", e);
        }
    }

    public static KeyStore load(InputStream keystoreData) {
        try {
            return decode(keystoreData);

        } catch (Exception e) {
            throw new RuntimeException("Failed to decode keystore data", e);
        }
    }

    private static KeyStore decode(InputStream in) throws IOException {
        final ArmoredInputStream armored = (ArmoredInputStream) PGPUtil.getDecoderStream(in);
        final Map<String, String> headers = parseHeader(armored.getArmorHeaders());

        return new KeyStore(headers.get("kdf"),
                            BASE16.decode(headers.get("salt").toUpperCase()),
                            ByteStreams.toByteArray(armored));
    }

    private static Map<String, String> parseHeader(String[] headers) {
        return Arrays.stream(headers)
                     .map(Pattern.compile(": ")::split)
                     .collect(Collectors.toMap(s -> s[0], s -> s[1]));
    }

    public static KeyStore createFromPrivateKey(byte[] privateKey, String password) {
        final byte[] salt = generateRandomBytes(SALT_BYTES_SIZE);
        return new KeyStore(KDF_TYPE, salt, armorPrivateKey(privateKey, password, salt));
    }

    private static byte[] generateRandomBytes(int size) {
        final byte[] bytes = new byte[size];
        secureRandom().nextBytes(bytes);
        return bytes;
    }

    private static byte[] armorPrivateKey(byte[] privateKey, String password, byte[] salt) {
        final byte[] bcryptKey = BCrypt.withDefaults().hash(BCRYPT_SECURITY_PARAMETER, salt,
                                                            password.getBytes(StandardCharsets.UTF_8));
        final byte[] encryptedKey = new Digest().digest(bcryptKey);
        final byte[] encodedPrivateKey = addAminoPrefix(PRIV_KEY_TYPE_SECP256K1, privateKey);
        return Xsalsa20Symmetric.encryptSymmetric(encodedPrivateKey, encryptedKey);
    }
}
