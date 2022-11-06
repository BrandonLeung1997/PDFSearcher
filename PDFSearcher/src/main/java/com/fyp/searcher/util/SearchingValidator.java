package com.fyp.searcher.util;

import java.util.function.Function;

public interface SearchingValidator extends Function <String, SearchingValidator.ValidationResult> {

    static SearchingValidator isEmpty(){
        return text -> text != null && !text.isEmpty() ? ValidationResult.SUCCESS : ValidationResult.IS_EMPTY;
    }

    default SearchingValidator and(SearchingValidator other){
        return text ->{
            ValidationResult result = this.apply(text);
            return result.equals(ValidationResult.SUCCESS) ? other.apply(text) : result;
        };
    }

    enum ValidationResult {
        SUCCESS,
        IS_EMPTY
    }

}

