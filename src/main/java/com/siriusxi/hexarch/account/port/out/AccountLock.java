package com.siriusxi.hexarch.account.port.out;

import com.siriusxi.hexarch.account.core.domain.Account;

import static com.siriusxi.hexarch.account.core.domain.Account.*;

public interface AccountLock {

	void lockAccount(AccountId accountId);

	void releaseAccount(AccountId accountId);

}
