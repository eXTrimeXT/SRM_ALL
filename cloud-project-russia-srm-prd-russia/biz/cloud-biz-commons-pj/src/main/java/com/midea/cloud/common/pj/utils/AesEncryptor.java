package com.midea.cloud.common.pj.utils;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jetbrains.annotations.Nullable;

public class AesEncryptor {
    public static void main(String[] args) {
        String ciphertext = "343cf7b159f747cbedd8284439dd751c85679e672195d29356149d265ed9de6987e11577cb2af26fb52388ddebfa3f996f9828ce70764c1a7a7b5c5c49f4a33a50484da4e76df02503544aab62ef583f13bee748dec86f1f58b8073168ef2e8092c69454e96fc48998a14fa8ddaa58f35eae9aba210cccaf732ee043ca8d69fab420f11216ecd40158cfec373f0865a9ef84b436cf1510f773212729bde605bb852263be46511b59bea7ff02e3086e4f78fe0d9b551631cb8793262ea4fff679ac69d4c2b82ba03dcc179837f5f91ec64b15adc36bdc6a5d6d6d90789c741e89df470c69bb8d010cc51b525d43621962e386783a5bcc4b2ef0d7b2db2403abbaf10ac1888ea470055fed6de69ec3129161f59dd96f5afa6f9ae3ae9854f64a09c31dd8be4d823c4edf6f6361295969e4df263f78e50a6125067a241f5e08b09d75d14b8b9c84d4ea05e76d54393d6517436a20e0ec27eb9549a3dcc6c41609d9882f77161e9fd0416b7e371aec7fdfef9f10ab8c674f305f280b4888532febb9fdfd91c7f8ea3ca461173eebc5ee66868230ff1e0cd69799a049524ab7433da302c354e2169da2261144d30d4e97f53b90b68743fb5803c3cd510ab866a6f4b029730fe2331c342f6e879a2359eaecefaec33ed10e8f4b015f109fed8b3cd9ece9094b2913c63867a69afbc3555bd6d09d5806a427499ad52e904b72980cdaed677148fb6d27cf4be7aa06225b73574bdab91114493a0066673f273a165e6f2f8973668e9f88843e8946dd7b4f215bc142d08b33141884947fded1e2abb567cc41896cb01b46247ddf84a833e9e81f08a78a4ce87d4c463972eecfd75ca45518159d1f242e345234c17cbad0c9340ae8622a8953113dc48a26282837296997d6dc5abbf7f8d03a6168e181515d2a9ffaf98087acb5a16a8af018c89466fedb1cbfb7072bf6acd9d6c06c91c43ae18f2f627def3a34d5ec33177dd450556913fc502b6de1127cffc47224408638d66d999d80df9f8b1daf11691101be7ff762d83b26920933e4ea9a0f40045b156a111fce9d383e5be77fc4f25fa95204130347adf36ed92523fdf794d12a858cf02cf24eda6b2675708555b12afaa07e6ac2eb1c700a1b31ce97d07d540e2969468dcfc3611711914310574181d96bfa924ef845c968419222d5e2dd11ec468d4781fae70b69a379275254453b442cae2593e47068a9de9038a2838b2218fd2a8952aadfbdb7c9a94a9e46181286d80c822ccf9400154e8182b06a3d2c03b789a2e9c146970615be6ee65bfc654f140469e9250b9180509dc4d957d3d66be742aa2c8bb8b0b9edf592ca3acd713ee0928779686ed333f8b72b617577171c07b322d8b3d76db8bf10de625c7c3bb4bfe89cfc02ab80688b04d77afb3ee466494824af3ab969ed1f94a85932385488c269c7991e93d6da6d71ff806209bbfd637e57f554ed38e49c30f23a2ddf7216444d149ceb23e5318cc230b2e0b6fce6d2b8e1eca7c1bb44b04d0267f9978854e66f59f8dceabfdb694705ea7dc01912a348386dedc4e3861936a87e35b7e12b855723cb726dc1256cad4ed9bd39002a3780c1ebda1a63011849352812c4ca9891f7d74c62a089819ccb2d2cb6c4d730d4cee91263749850884c7568102f5bb40d037910c8c204fe1a21a1fff08e9001a9971864813b7176bf11dfd5aaf955bf7d61631e62962d7f222ffea813dbede81ce4fcc184bbe8cd6684720c1bc2d2dbe3851dfb17d46de53f1333c441f8da792776cb1ff5a641b83b9dd421cf727cc755aa591e32531b35e49a8f084e28f354a737e28a085d6d627171597a20dc275f4b9dc7c8ec516b25bf36617218eb516f8be9fb855803a45c7cdce75a10d7034aad0cc57868bd9060a8edacb0c73052c0c6e63ac6d5bcc33bf5de7a5bee78c35b25a33368fa";

        // Оба пароля для проверки
        String[] passwords = {"gn4^Qa0k+WyeCkKt", "43D8OchKs9qPm0_V"};

        for (String password : passwords) {
            try {
                String decrypted = getString(password, ciphertext);
                System.out.println("Успех с паролем: " + password);
                System.out.println("Расшифрованное значение: " + decrypted);
                return; // Выходим, если пароль подошел
            } catch (Exception e) {
                System.out.println("Пароль '" + password + "' не подошел или возникла ошибка: " + e.getMessage());
            }
        }
    }

    @Nullable
    private static String getString(String password, String ciphertext) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        config.setPassword(password);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000"); // Стандартное значение Jasypt
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator"); // Для PBEWithMD5AndDES IV не используется
        config.setStringOutputType("hexadecimal"); // Так как шифротекст в hex

        encryptor.setConfig(config);

        return encryptor.decrypt(ciphertext);
    }
}