package com.example.passman;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MasterPasswordManager {
    private static final String FILE_NAME = "master.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final int BCRYPT_COST = 13;

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
        String hash = BCrypt.hashpw(master, BCrypt.gensalt(BCRYPT_COST));
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
            return BCrypt.checkpw(inputMaster, data.masterHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
