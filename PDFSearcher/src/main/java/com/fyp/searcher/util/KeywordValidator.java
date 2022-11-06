package com.fyp.searcher.util;

import com.fyp.searcher.controller.KeywordController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.fyp.searcher.util.ValidationResult.invalid;
import static com.fyp.searcher.util.ValidationResult.valid;

public interface KeywordValidator extends Function<KeywordController, ValidationResult> {

    static KeywordValidator isEmpty() {
        return holds(key ->
                        key.getKeyword() != null
                        && !key.getKeyword().trim().isEmpty()
                        && key.getKeyword().matches("[a-zA-Z]+")
                , "KEYWORD","Keyword is for one word and letters only");
    }

    static KeywordValidator posEmpty() {
        return holds(key -> !key.getPOS().isEmpty(), "POS", "Part of speech is empty");

    }

    static KeywordValidator operatorEmpty() {
        return holds(key -> key.getOperator() != null, "OPERATOR", "Operator is empty");
    }

    static KeywordValidator holds(Predicate<KeywordController> p, String token, String message){
        return key -> p.test(key) ? valid() : invalid(token, message);
    }

    static KeywordValidator all(KeywordValidator... validations){
        return key -> Arrays.stream(validations).map(v -> v.apply(key)).reduce(valid(), (acc, next) -> {
            if(next.isValid()) return acc;
            HashMap<String, String> reasons = new HashMap<>(acc.getReason().orElse(new HashMap<>()));
            reasons.putAll(next.getReason().get());
            return new Invalid(reasons);
        });
    }

    default KeywordValidator and(KeywordValidator other) {
        return key -> {
            final ValidationResult result = this.apply(key);
            return result.isValid() ? other.apply(key) : result;
        };
    }

}

