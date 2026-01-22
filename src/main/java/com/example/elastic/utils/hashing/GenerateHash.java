package com.example.elastic.utils.hashing;

import com.example.elastic.exceptions.HashSha256Exception;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class GenerateHash {

    // Define a buffer size for efficient reading of large files
    private final int bufferSize = 8192; // 8KB buffer

    /**
     * Calculates the SHA-256 hash of a file's content.
     * @param file The file (e.g., MultipartFile) to hash.
     * @return The 64-character hexadecimal SHA-256 hash string.
     */
    public String calculateSha256(MultipartFile file) throws HashSha256Exception {

        try (InputStream inputStream = file.getInputStream()) {
            //  Steps to get SHA-256 of bytes
            // 1. Get the SHA-256 MessageDigest instance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] buffer = new byte[bufferSize];
            int bytesRead;

            // 2. Read the file in chunks and update the digest
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // Pass the chunk of bytes to the digest object
                digest.update(buffer, 0, bytesRead);
            }

            // 3. Finalize the hash calculation and get the byte array
            byte[] hashedBytes = digest.digest();

            // 4. Convert the byte array to a hexadecimal string
            return bytesToHex(hashedBytes);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new HashSha256Exception("An Exception occurred during hash generation "+e.getMessage());
        }
    }

    /**
     * @param hash a byte array
     * @return hexadecimal string - human-readable
     * Method to convert the byte array hash into a hex string.
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);

            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex);
        }
        return hexString.toString();
    }
}
