package org.example.createapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.createapi.model.dto.DynamicRuleQueryDto;
import org.example.createapi.model.dto.DynamicRuleRequest;
import org.example.createapi.model.dto.DynamicRuleResponse;
import org.example.createapi.model.rule.DynamicRule;
import org.example.createapi.repository.RuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DynamicRuleService {

    private final RuleRepository ruleRepository;
    private final ObjectMapper objectMapper;

    public DynamicRuleService(RuleRepository ruleRepository, ObjectMapper objectMapper) {
        this.ruleRepository = ruleRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Create a new dynamic rule.
     */
    @Transactional
    public DynamicRuleResponse createRule(DynamicRuleRequest request) {
        String id = UUID.randomUUID().toString();

        // Serialize queries to JSON string
        String ruleJson;
        try {
            ruleJson = objectMapper.writeValueAsString(request.getQueries());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize rule queries", e);
        }

        DynamicRule dynamicRule = new DynamicRule();
        dynamicRule.setId(id);
        dynamicRule.setProductName(request.getProductName());
        dynamicRule.setProductId(request.getProductId());
        dynamicRule.setProductText(request.getProductText());
        dynamicRule.setRule(ruleJson);

        dynamicRule = ruleRepository.save(dynamicRule);

        return toResponse(dynamicRule);
    }

    /**
     * Get all dynamic rules.
     */
    @Transactional(readOnly = true)
    public List<DynamicRuleResponse> getAllRules() {
        return ruleRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Delete a rule by product_id.
     */
    @Transactional
    public void deleteRuleByProductId(String productId) {
        List<DynamicRule> rules = ruleRepository.findByProductId(productId);
        ruleRepository.deleteAll(rules);
    }

    /**
     * Convert entity to response DTO.
     */
    private DynamicRuleResponse toResponse(DynamicRule entity) {
        return new DynamicRuleResponse(
                entity.getId(),
                entity.getProductName(),
                entity.getProductId(),
                entity.getProductText(),
                entity.getRule()
        );
    }

    /**
     * Parse rule JSON string back to query DTOs.
     */
    public List<DynamicRuleQueryDto> parseRule(String ruleJson) {
        try {
            return objectMapper.readValue(ruleJson, new TypeReference<List<DynamicRuleQueryDto>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse rule JSON", e);
        }
    }
}
