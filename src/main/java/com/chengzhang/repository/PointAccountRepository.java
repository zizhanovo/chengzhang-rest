package com.chengzhang.repository;

import com.chengzhang.entity.PointAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 积分账户数据访问层
 */
@Repository
public interface PointAccountRepository extends JpaRepository<PointAccount, Long> {

    /**
     * 通过用户ID查找积分账户
     */
    Optional<PointAccount> findByUserId(Long userId);

    /**
     * 检查用户是否有积分账户
     */
    boolean existsByUserId(Long userId);
}
