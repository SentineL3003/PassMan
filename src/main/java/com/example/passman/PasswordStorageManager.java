package com.example.passman;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PasswordStorageManager {
    private static final String FILE_NAME = "passwords.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static class PasswordDTO {
        private String title, login, password, url;

        public PasswordDTO() {}

        PasswordDTO(Password password) {
            this.title = password.getTitle();
            this.login = password.getLogin();
            this.url = password.getUrl();

            // Шифруем пароль
            try {
                if (CryptoPass.isReady()) {
                    this.password = CryptoPass.encrypt(password.getPassword());
                } else {
                    this.password = password.getPassword(); // на всякий случай
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.password = password.getPassword();
            }
        }
        Password toPassword() {
            String decrypted = this.password;
            try {
                if (CryptoPass.isReady() && this.password != null) {
                    decrypted = CryptoPass.decrypt(this.password);
                }
            } catch (Exception e) {
                // Если не смогли расшифровать — оставляем как есть (или можно выбросить ошибку)
                e.printStackTrace();
            }
            return new Password(title, login, decrypted, url);
        }
    }

    // сохранение
    public static void savePasswords(List<Password> passwords) {
        List<PasswordDTO> dtos = passwords.stream().map(PasswordDTO::new).collect(Collectors.toList());

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(FILE_NAME), StandardCharsets.UTF_8)) {
            gson.toJson(dtos, writer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // загрузка из файла
    public static List<Password> loadPasswords() {
        File file = new File(FILE_NAME);
        if (!file.exists())
            return new ArrayList<>();
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            List<PasswordDTO> dtos = gson.fromJson(reader, new TypeToken<List<PasswordDTO>>() {}.getType());
            if (dtos == null)
                return new ArrayList<>();
            return dtos.stream().map(PasswordDTO::toPassword).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}