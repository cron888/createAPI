package org.example.createapi.service;

import org.example.createapi.model.dto.DynamicRuleQueryDto;
import org.example.createapi.model.dto.RecommendationResponse;
import org.example.createapi.model.dto.RecommendationResponse.RecommendationItem;
import org.example.createapi.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationService {

    private final RuleEvaluationService ruleEvaluationService;
    private final DynamicRuleService dynamicRuleService;
    private final ProductRepository productRepository;

    public RecommendationService(RuleEvaluationService ruleEvaluationService,
                                 DynamicRuleService dynamicRuleService,
                                 ProductRepository productRepository) {
        this.ruleEvaluationService = ruleEvaluationService;
        this.dynamicRuleService = dynamicRuleService;
        this.productRepository = productRepository;
    }

    /**
     * Get recommendations for a user by evaluating all dynamic rules.
     */
    @Transactional(readOnly = true)
    public RecommendationResponse getRecommendations(String userId) {
        List<RecommendationItem> recommendations = new ArrayList<>();

        // Evaluate dynamic rules
        List<org.example.createapi.model.rule.DynamicRule> allRules =
                dynamicRuleService.getAllRules().stream()
                        .map(r -> {
                            org.example.createapi.model.rule.DynamicRule entity =
                                    new org.example.createapi.model.rule.DynamicRule();
                            entity.setId(r.getId());
                            entity.setProductName(r.getProductName());
                            entity.setProductId(r.getProductId());
                            entity.setProductText(r.getProductText());
                            entity.setRule(r.getRule());
                            return entity;
                        })
                        .toList();

        for (org.example.createapi.model.rule.DynamicRule rule : allRules) {
            List<DynamicRuleQueryDto> queries = dynamicRuleService.parseRule(rule.getRule());

            boolean ruleMatches = evaluateRule(queries, userId);

            if (ruleMatches) {
                recommendations.add(new RecommendationItem(
                        rule.getProductId(),
                        rule.getProductText()
                ));
            }
        }

        // Evaluate fixed rules (hardcoded examples)
        evaluateFixedRules(userId, recommendations);

        return new RecommendationResponse(userId, recommendations);
    }

    /**
     * Evaluate a single rule: all queries must return true (AND logic).
     */
    private boolean evaluateRule(List<DynamicRuleQueryDto> queries, String userId) {
        for (DynamicRuleQueryDto query : queries) {
            boolean queryResult = ruleEvaluationService.evaluateQuery(query, userId);
            if (!queryResult) {
                return false;
            }
        }
        return true;
    }

    /**
     * Evaluate fixed rules alongside dynamic rules.
     * These are hardcoded business rules that are not converted to dynamic format.
     */
    private void evaluateFixedRules(String userId, List<RecommendationItem> recommendations) {
        // Example fixed rule: recommend "Premium Debit" to users who have at least 10 DEBIT transactions
        // This would be evaluated using the same RuleEvaluationService
        // For now, we leave this extensible for future fixed rules
    }
}
