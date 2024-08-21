package com.example.spot.validation.validator;

import com.example.spot.api.code.status.ErrorStatus;
import com.example.spot.repository.PostCommentRepository;
import com.example.spot.validation.annotation.ExistPostComment;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistPostCommentValidator implements ConstraintValidator<ExistPostComment, Long> {

    private final PostCommentRepository postCommentRepository;

    @Override
    public void initialize(ExistPostComment constraintAnnotation) {}

    @Override
    public boolean isValid(Long commentId, ConstraintValidatorContext context) {

        boolean isValid = false;
        ErrorStatus errorStatus;

        if (commentId == null) {
            errorStatus = ErrorStatus._POST_COMMENT_ID_NULL;
        } else if (!postCommentRepository.existsById(commentId)) {
            errorStatus = ErrorStatus._POST_COMMENT_NOT_FOUND;
        } else {
            errorStatus = ErrorStatus._POST_COMMENT_NOT_FOUND; // Ignore this
            isValid = true;
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorStatus.getMessage())
                    .addConstraintViolation();
        }

        return isValid;
    }
}
