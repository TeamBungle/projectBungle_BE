package com.sparta.meeting_platform.chat.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {
//
    @Value("${JASYPT_ENCRYPTOR_PASSWORD}")
    private String encryptKey; //이 암호화 키로 정보를 암호화시키는 것

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor(){

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        config.setPassword(encryptKey);
        config.setPoolSize("1");
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setStringOutputType("base64");
        config.setKeyObtentionIterations("1000");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        //config를 바탕으로 암호화 진행
        encryptor.setConfig(config);

        //암호화 결과물 return
        return encryptor;
    }
}