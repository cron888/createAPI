package org.example.createapi.repository;

import org.example.createapi.model.rule.DynamicRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleRepository extends JpaRepository<DynamicRule, String> {

    List<DynamicRule> findByProductId(String productId);
}
