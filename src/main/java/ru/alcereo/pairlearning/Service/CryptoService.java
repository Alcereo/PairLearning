package ru.alcereo.pairlearning.Service;

import ru.alcereo.exoption.Option;
import ru.alcereo.pairlearning.Service.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Сервис хеширования паролей
 */
public class CryptoService {


    /**
     * Функция хеширования паролей - SHA256( SHA256(password)+sole )
     * @param password
     *  Пароль
     * @param sole
     *  Соль
     * @return
     *  Строка = SHA256( SHA256(password)+sole )
     */
    public static Option<String, NoSuchAlgorithmException> cryptPass(String password, String sole) {
        return Option.asOption(() -> MessageDigest.getInstance("SHA-256"))
                .map(md -> {
                    md.update(password.getBytes());
                    String first = String.format("%064x", new java.math.BigInteger(1, md.digest()))+sole;
                    md.update(first.getBytes());
                    return String.format("%064x", new java.math.BigInteger(1, md.digest()));
                });
    }

    public static boolean validateUserPassword(User user){

        return true;

    }

}
