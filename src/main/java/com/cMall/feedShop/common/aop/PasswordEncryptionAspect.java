package com.cMall.feedShop.common.aop;

import com.cMall.feedShop.annotation.CustomEncryption;
import com.cMall.feedShop.user.domain.service.PasswordEncryptionService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Modifier;
import java.util.Arrays;

@Aspect
@Component
@AllArgsConstructor
public class PasswordEncryptionAspect {

    private final PasswordEncryptionService passwordEncryptionService;

    /**
     * com.cMall.feedShop.user.presentation 패키지 (및 하위) 내의
     * 모든 클래스의 모든 메서드 실행 전후에 이 Aspect를 적용합니다.
     * 컨트롤러에서 DTO를 받을 때 비밀번호를 암호화하는 용도입니다.
     */
    @Around("execution(* com.cMall.feedShop.user.presentation..*.*(..))")
    public Object passwordEncryptionAspect(ProceedingJoinPoint pjp) throws Throwable {
        Arrays.stream(pjp.getArgs())
                .forEach(this::fieldEncryption);

        return pjp.proceed();
    }

    public void fieldEncryption(Object object) {
        if (ObjectUtils.isEmpty(object)) {
            return;
        }

        FieldUtils.getAllFieldsList(object.getClass())
                .stream()
                .filter(field -> !(Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())))
                .forEach(field -> {
                    try {
                        boolean encryptionTarget = field.isAnnotationPresent(CustomEncryption.class);
                        if (!encryptionTarget) {
                            return;
                        }

                        Object fieldValue = FieldUtils.readField(field, object, true);
                        if (!(fieldValue instanceof String)) {
                            return;
                        }

                        String rawString = (String) fieldValue;
                        String encryptedString = passwordEncryptionService.encrypt(rawString);
                        FieldUtils.writeField(field, object, encryptedString, true);
                    } catch (Exception e) {
                        throw new RuntimeException("Error during field encryption: " + field.getName(), e);
                    }
                });
    }
}