package com.siriusxi.hexarch.account.port.out;

import com.siriusxi.hexarch.account.core.domain.Account;

public interface UpdateAccountStatePort {

	void updateActivities(Account account);

}
