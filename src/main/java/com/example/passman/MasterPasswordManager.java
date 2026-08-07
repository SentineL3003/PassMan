package com.example.passman;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MasterPasswordManager {
    private static final String FILE_NAME = "master.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static class MasterData {
        private String masterHash;

        public MasterData() {}
        public MasterData(String masterHash) {
            this.masterHash = masterHash;
        }
    }

    public static boolean hasMaster() {
        File file = new File(FILE_NAME);
        return file.exists() && file.length() > 0;
    }

    public static void saveMaster(String master) {
        String hash = hashMaster(master);
        MasterData data = new MasterData(hash);

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(FILE_NAME), StandardCharsets.UTF_8)) {
            gson.toJson(data, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkMaster(String inputMaster) {
        if (!hasMaster())
            return false;
        try (Reader reader = new InputStreamReader(new FileInputStream(FILE_NAME), StandardCharsets.UTF_8)) {
            MasterData data = gson.fromJson(reader, MasterData.class);
            if (data == null || data.masterHash == null) return false;
            return data.masterHash.equals(hashMaster(inputMaster));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String hashMaster(String master) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(master.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString =  new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 error", e);
        }
    }
}
