package ru.alcereo.pairlearning.Service;

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
    public static String cryptPass(String password, String sole) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
        String first = String.format("%064x", new java.math.BigInteger(1, md.digest()))+sole;
        md.update(first.getBytes());

        return String.format("%064x", new java.math.BigInteger(1, md.digest()));
    }

}
