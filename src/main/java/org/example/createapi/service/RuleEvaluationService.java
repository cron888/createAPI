package org.example.createapi.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.example.createapi.model.dto.DynamicRuleQueryDto;
import org.example.createapi.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
public class RuleEvaluationService {

    private static final int CACHE_MAX_SIZE = 1000;
    private static final long CACHE_TTL_MINUTES = 10;

    private final TransactionRepository transactionRepository;

    private final Cache<String, Long> transactionCountCache;
    private final Cache<String, BigDecimal> transactionSumCache;
    private final Cache<String, Object[]> depositWithdrawCompareCache;

    public RuleEvaluationService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionCountCache = Caffeine.newBuilder()
                .maximumSize(CACHE_MAX_SIZE)
                .expireAfterWrite(CACHE_TTL_MINUTES, TimeUnit.MINUTES)
                .build();
        this.transactionSumCache = Caffeine.newBuilder()
                .maximumSize(CACHE_MAX_SIZE)
                .expireAfterWrite(CACHE_TTL_MINUTES, TimeUnit.MINUTES)
                .build();
        this.depositWithdrawCompareCache = Caffeine.newBuilder()
                .maximumSize(CACHE_MAX_SIZE)
                .expireAfterWrite(CACHE_TTL_MINUTES, TimeUnit.MINUTES)
                .build();
    }

    /**
     * Evaluates a single query against a user.
     */
    public boolean evaluateQuery(DynamicRuleQueryDto query, String userId) {
        boolean result;

        switch (query.getQuery()) {
            case "USER_OF" -> result = evaluateUserOf(query, userId);
            case "ACTIVE_USER_OF" -> result = evaluateActiveUserOf(query, userId);
            case "TRANSACTION_SUM_COMPARE" -> result = evaluateTransactionSumCompare(query, userId);
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> result = evaluateDepositWithdrawCompare(query, userId);
            default -> throw new IllegalArgumentException("Unknown query type: " + query.getQuery());
        }

        // Apply negate
        if (query.isNegate()) {
            return !result;
        }
        return result;
    }

    /**
     * USER_OF: Check if user has at least 1 transaction for products of given type.
     */
    private boolean evaluateUserOf(DynamicRuleQueryDto query, String userId) {
        String productType = query.getArguments()[0];
        String cacheKey = userId + ":" + productType;

        long count = transactionCountCache.get(cacheKey, key ->
                transactionRepository.countByUserIdAndProductType(userId, productType));

        return count >= 1;
    }

    /**
     * ACTIVE_USER_OF: Check if user has at least 5 transactions for products of given type.
     */
    private boolean evaluateActiveUserOf(DynamicRuleQueryDto query, String userId) {
        String productType = query.getArguments()[0];
        String cacheKey = userId + ":" + productType;

        long count = transactionCountCache.get(cacheKey, key ->
                transactionRepository.countByUserIdAndProductType(userId, productType));

        return count >= 5;
    }

    /**
     * TRANSACTION_SUM_COMPARE: Compare sum of transactions of type Y for products of type X with constant C.
     * Arguments: [productType, transactionType, operator, constant]
     */
    private boolean evaluateTransactionSumCompare(DynamicRuleQueryDto query, String userId) {
        String productType = query.getArguments()[0];
        String transactionType = query.getArguments()[1];
        String operator = query.getArguments()[2];
        int constant = Integer.parseInt(query.getArguments()[3]);

        String cacheKey = userId + ":" + productType + ":" + transactionType;

        BigDecimal sum = transactionSumCache.get(cacheKey, key ->
                transactionRepository.sumByUserIdAndProductTypeAndTransactionType(userId, productType, transactionType));

        return compare(sum.doubleValue(), constant, operator);
    }

    /**
     * TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW: Compare sum of DEPOSIT vs WITHDRAW for products of type X.
     * Arguments: [productType, operator]
     */
    private boolean evaluateDepositWithdrawCompare(DynamicRuleQueryDto query, String userId) {
        String productType = query.getArguments()[0];
        String operator = query.getArguments()[1];

        String cacheKey = userId + ":" + productType;

        Object[] result = depositWithdrawCompareCache.get(cacheKey, key ->
                transactionRepository.sumDepositAndWithdrawByUserIdAndProductType(userId, productType));

        BigDecimal depositSum = (BigDecimal) result[0];
        BigDecimal withdrawSum = (BigDecimal) result[1];

        return compare(depositSum.doubleValue(), withdrawSum.doubleValue(), operator);
    }

    /**
     * Compare two values using the given operator string.
     */
    private boolean compare(double left, double right, String operator) {
        return switch (operator) {
            case ">" -> left > right;
            case "<" -> left < right;
            case "=" -> left == right;
            case ">=" -> left >= right;
            case "<=" -> left <= right;
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }
}
