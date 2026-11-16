package org.example.createapi.repository;

import org.example.createapi.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<org.example.createapi.model.entity.Transaction, String> {

    /**
     * Count transactions for a user's products of given type.
     */
    @Query("""
            SELECT COUNT(t) FROM Transaction t
            WHERE t.product.userId = :userId
            AND t.product.type = :productType
            """)
    long countByUserIdAndProductType(@Param("userId") String userId,
                                     @Param("productType") String productType);

    /**
     * Sum of transaction amounts for a user's products of given type and transaction type.
     */
    @Query("""
            SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t
            WHERE t.product.userId = :userId
            AND t.product.type = :productType
            AND t.type = :transactionType
            """)
    BigDecimal sumByUserIdAndProductTypeAndTransactionType(@Param("userId") String userId,
                                                           @Param("productType") String productType,
                                                           @Param("transactionType") String transactionType);

    /**
     * Compare deposit vs withdraw sums for a user's products of given type.
     * Returns [depositSum, withdrawSum].
     */
    @Query("""
            SELECT
                COALESCE(SUM(CASE WHEN t.type = 'DEPOSIT' THEN t.amount ELSE 0 END)),
                COALESCE(SUM(CASE WHEN t.type = 'WITHDRAW' THEN t.amount ELSE 0 END))
            FROM Transaction t
            WHERE t.product.userId = :userId
            AND t.product.type = :productType
            """)
    Object[] sumDepositAndWithdrawByUserIdAndProductType(@Param("userId") String userId,
                                                         @Param("productType") String productType);
}
