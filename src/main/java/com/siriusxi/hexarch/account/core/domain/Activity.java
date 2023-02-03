package com.siriusxi.hexarch.account.core.domain;

import com.siriusxi.hexarch.account.core.domain.Account.AccountId;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * A money transfer activity between {@link Account}s.
 */
@Value
@RequiredArgsConstructor
public class Activity {

	public record ActivityId(Long value) {}

	@Getter
	ActivityId id;

	/**
	 * The account that owns this activity.
	 */
	@Getter
	@NonNull
	AccountId ownerAccountId;

	/**
	 * The debited account.
	 */
	@Getter
	@NonNull
	AccountId sourceAccountId;

	/**
	 * The credited account.
	 */
	@Getter
	@NonNull
	AccountId targetAccountId;

	/**
	 * The timestamp of the activity.
	 */
	@Getter
	@NonNull
	LocalDateTime timestamp;

	/**
	 * The money that was transferred between the accounts.
	 */
	@Getter
	@NonNull
	Money money;

	public Activity(
			@NonNull AccountId ownerAccountId,
			@NonNull AccountId sourceAccountId,
			@NonNull AccountId targetAccountId,
			@NonNull LocalDateTime timestamp,
			@NonNull Money money) {
		this.id = null;
		this.ownerAccountId = ownerAccountId;
		this.sourceAccountId = sourceAccountId;
		this.targetAccountId = targetAccountId;
		this.timestamp = timestamp;
		this.money = money;
	}
}
