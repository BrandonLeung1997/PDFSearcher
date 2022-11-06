package com.fyp.searcher.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface ValidationResult{
    static ValidationResult valid(){
        return new Valid();
    }

    static ValidationResult invalid(String token, String reason){
        return new Invalid(token, reason);
    }

    boolean isValid();

    Optional<HashMap<String, String>> getReason();
}

final class Invalid implements ValidationResult {
    private final HashMap<String, String> reasons;

    Invalid(){
        reasons = new HashMap<>();
    }

    Invalid(String token, String reason){
        this();
        this.reasons.put(token,reason);
    }

    Invalid(HashMap<String, String> reasons){
        this();
        this.reasons.putAll(reasons);
    }

    public boolean isValid(){
        return false;
    }


    public Optional<HashMap<String, String>> getReason(){
        return Optional.of(reasons);
    }

}

final class Valid implements ValidationResult {

    public boolean isValid(){
        return true;
    }

    public Optional<HashMap<String, String>> getReason(){
        return Optional.empty();
    }

}
