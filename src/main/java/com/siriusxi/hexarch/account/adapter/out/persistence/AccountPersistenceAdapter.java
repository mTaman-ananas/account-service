package com.siriusxi.hexarch.account.adapter.out.persistence;


import com.siriusxi.hexarch.account.core.domain.Account;
import com.siriusxi.hexarch.account.core.domain.Activity;
import com.siriusxi.hexarch.account.port.out.LoadAccountPort;
import com.siriusxi.hexarch.account.port.out.UpdateAccountStatePort;
import com.siriusxi.hexarch.infra.common.annotation.PersistenceAdapter;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import static com.siriusxi.hexarch.account.core.domain.Account.*;

@RequiredArgsConstructor
@PersistenceAdapter
class AccountPersistenceAdapter implements
		LoadAccountPort,
		UpdateAccountStatePort {

	private final SpringAccountRepository accountRepository;
	private final ActivityRepository activityRepository;
	private final AccountMapper accountMapper;

	@Override
	public Account loadAccount(
					AccountId accountId,
					LocalDateTime baselineDate) {

		AccountEntity account =
				accountRepository.findById(accountId.value())
						.orElseThrow(EntityNotFoundException::new);

		List<ActivityEntity> activities =
				activityRepository.findByOwnerSince(
						accountId.value(),
						baselineDate);

		Long withdrawalBalance = orZero(activityRepository
				.getWithdrawalBalanceUntil(
						accountId.value(),
						baselineDate));

		Long depositBalance = orZero(activityRepository
				.getDepositBalanceUntil(
						accountId.value(),
						baselineDate));

		return accountMapper.toAccount(
				account,
				activities,
				withdrawalBalance,
				depositBalance);

	}

	private Long orZero(Long value){
		return value == null ? 0L : value;
	}


	@Override
	public void updateActivities(Account account) {
		for (Activity activity : account.getActivityWindow().getActivities()) {
			if (activity.getId() == null) {
				activityRepository.save(accountMapper.toActivityEntity(activity));
			}
		}
	}

}
