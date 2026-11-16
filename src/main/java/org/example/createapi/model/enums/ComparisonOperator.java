package org.example.createapi.model.enums;

public enum ComparisonOperator {
    LT, LTE, GT, GTE, EQ;

    public boolean compare(double left, double right) {
        return switch (this) {
            case LT -> left < right;
            case LTE -> left <= right;
            case GT -> left > right;
            case GTE -> left >= right;
            case EQ -> left == right;
        };
    }
}
