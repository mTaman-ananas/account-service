package com.siriusxi.hexarch.account.core.usecase;

import com.siriusxi.hexarch.account.core.domain.Account.AccountId;
import com.siriusxi.hexarch.account.core.domain.Money;
import com.siriusxi.hexarch.account.port.in.GetAccountBalanceQuery;
import com.siriusxi.hexarch.account.port.out.LoadAccountPort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
class GetAccountBalanceService implements GetAccountBalanceQuery {

	private final LoadAccountPort loadAccountPort;

	@Override
	public Money getAccountBalance(AccountId accountId) {
		return loadAccountPort.loadAccount(accountId, LocalDateTime.now())
				.calculateBalance();
	}
}
