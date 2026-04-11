package com.jian.hobbyadventure.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.util.List;
import java.util.Properties;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private PasswordValidator validator;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        Properties props = new Properties();
        props.setProperty("TOO_SHORT", "비밀번호는 8자 이상이어야 합니다.");
        props.setProperty("TOO_LONG", "비밀번호는 32자 이하여야 합니다.");
        props.setProperty("INSUFFICIENT_ALPHABETICAL", "비밀번호는 영문자를 포함해야 합니다.");
        props.setProperty("INSUFFICIENT_DIGIT", "비밀번호는 숫자를 포함해야 합니다.");
        props.setProperty("INSUFFICIENT_SPECIAL", "비밀번호는 특수문자를 포함해야 합니다.");

        MessageResolver resolver = new PropertiesMessageResolver(props);

        validator = new PasswordValidator(resolver,
                new LengthRule(8, 32),
                new CharacterRule(EnglishCharacterData.Alphabetical, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1)
        );
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        RuleResult result = validator.validate(new PasswordData(value));
        if (result.isValid()) return true;

        List<String> messages = validator.getMessages(result);
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(messages.get(0)).addConstraintViolation();
        return false;
    }
}
