package com.siriusxi.hexarch.account.port.out;

import com.siriusxi.hexarch.account.core.domain.Account;

import java.time.LocalDateTime;

import static com.siriusxi.hexarch.account.core.domain.Account.*;

public interface LoadAccountPort {

	Account loadAccount(AccountId accountId, LocalDateTime baselineDate);
}
