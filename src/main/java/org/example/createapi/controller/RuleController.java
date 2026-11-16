package org.example.createapi.controller;

import org.example.createapi.model.dto.DynamicRuleRequest;
import org.example.createapi.model.dto.DynamicRuleResponse;
import org.example.createapi.model.dto.RecommendationResponse;
import org.example.createapi.service.DynamicRuleService;
import org.example.createapi.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rule")
public class RuleController {

    private final DynamicRuleService dynamicRuleService;
    private final RecommendationService recommendationService;

    public RuleController(DynamicRuleService dynamicRuleService,
                          RecommendationService recommendationService) {
        this.dynamicRuleService = dynamicRuleService;
        this.recommendationService = recommendationService;
    }

    /**
     * Create a new dynamic rule.
     * POST /rule
     */
    @PostMapping
    public ResponseEntity<DynamicRuleResponse> createRule(@RequestBody DynamicRuleRequest request) {
        DynamicRuleResponse response = dynamicRuleService.createRule(request);
        return ResponseEntity.ok(response);
    }

    /**
     * List all dynamic rules.
     * GET /rule
     */
    @GetMapping
    public ResponseEntity<Map<String, List<DynamicRuleResponse>>> getAllRules() {
        List<DynamicRuleResponse> rules = dynamicRuleService.getAllRules();
        return ResponseEntity.ok(Map.of("data", rules));
    }

    /**
     * Delete a rule by product_id.
     * DELETE /rule/{product_id}
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteRule(@PathVariable String productId) {
        dynamicRuleService.deleteRuleByProductId(productId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get recommendations for a user.
     * GET /recommendation/{user_id}
     */
    @GetMapping("/recommendation/{userId}")
    public ResponseEntity<RecommendationResponse> getRecommendations(@PathVariable String userId) {
        RecommendationResponse response = recommendationService.getRecommendations(userId);
        return ResponseEntity.ok(response);
    }
}
