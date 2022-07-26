package com.sparta.meeting_platform.service;

import com.sparta.meeting_platform.domain.EmailToken;
import com.sparta.meeting_platform.exception.EmailApiException;
import com.sparta.meeting_platform.repository.EmailConfirmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@RequiredArgsConstructor
@Service
public class EmailConfirmTokenService {

    private final EmailConfirmTokenRepository emailConfirmTokenRepository;
    private final JavaMailSender javaMailSender;

    // 이메일로 token 전송
    @Async("mailExecutor")
    public void createEmailConfirmationToken(String receiverEmail) {
        try {
            //      인증 Token 정보 DB 저장
            EmailToken emailToken = EmailToken.createEmailConfirmToken(receiverEmail);
            emailConfirmTokenRepository.save(emailToken);

            //      Mail Message 생성
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(receiverEmail); //받는사람
            helper.setSubject("벙글! 회원가입 이메일 인증"); //메일제목
            helper.setText("인증 링크 배포서버 : " + "<a href=" + "'https://gutner.shop/user/confirmEmail?token=" + emailToken.getId() + "'>" + "인증 하기" + "</a><br>" +
                    "인증 링크 로컬8080 : " + "<a href=" + "'http://localhost:8080/user/confirmEmail2?token=" + emailToken.getId() + "'>" + "인증 하기" + "</a>", true); //ture넣을경우 html

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new EmailApiException("이메일 전송 오류 입니다. 관리자에 문의 주세요");
        }
    }

        // 만료된 기존 token 삭제
        @Async("mailExecutor")
        public void deleteExpiredDateToken (String id){
            emailConfirmTokenRepository.deleteById(id);
        }
    }

