package com.siriusxi.hexarch.account.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface SpringAccountRepository extends JpaRepository<AccountEntity, Long> {
}
