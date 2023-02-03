package com.siriusxi.hexarch.account.port.in;

import com.siriusxi.hexarch.account.core.domain.Account;
import com.siriusxi.hexarch.account.core.domain.Account.AccountId;
import com.siriusxi.hexarch.account.core.domain.Money;

public interface GetAccountBalanceQuery {

	Money getAccountBalance(AccountId accountId);

}
