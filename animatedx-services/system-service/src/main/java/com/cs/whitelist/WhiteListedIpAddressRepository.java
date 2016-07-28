package com.cs.whitelist;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Hadi Movaghar
 */
@Repository
public interface WhiteListedIpAddressRepository extends JpaRepository<WhiteListedIpAddress, Long>, QueryDslPredicateExecutor<WhiteListedIpAddress> {

    List<WhiteListedIpAddress> findByDeletedFalse();

    @Query("select wip from WhiteListedIpAddress wip where wip.deleted = 'false'")
    Page<WhiteListedIpAddress> findWhiteListedIpAddresses(Pageable pageable);
}
