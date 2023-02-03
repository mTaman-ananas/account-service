package com.siriusxi.hexarch.account.core.usecase;

import com.siriusxi.hexarch.account.core.domain.Account.AccountId;
import com.siriusxi.hexarch.account.port.out.AccountLock;
import org.springframework.stereotype.Component;

@Component
class NoOpAccountLock implements AccountLock {

	@Override
	public void lockAccount(AccountId accountId) {
		// do nothing
	}

	@Override
	public void releaseAccount(AccountId accountId) {
		// do nothing
	}

}
