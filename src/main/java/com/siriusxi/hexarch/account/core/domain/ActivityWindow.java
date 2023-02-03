package com.siriusxi.hexarch.account.core.domain;

import com.siriusxi.hexarch.account.core.domain.Account.AccountId;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.*;

/**
 * A window of account activities.
 */
public class ActivityWindow {

	/**
	 * The list of account activities within this window.
	 */
	private final List<Activity> activities;

	/**
	 * The timestamp of the first activity within this window.
	 */
	public LocalDateTime getStartTimestamp() {
		return activities.stream()
				.min(Comparator.comparing(Activity::getTimestamp))
				.orElseThrow(IllegalStateException::new)
				.getTimestamp();
	}

	/**
	 * The timestamp of the last activity within this window.
	 * @return window end time
	 */
	public LocalDateTime getEndTimestamp() {
		return activities.stream()
				.max(Comparator.comparing(Activity::getTimestamp))
				.orElseThrow(IllegalStateException::new)
				.getTimestamp();
	}

	/**
	 * Calculates the balance by summing up the values of all activities within this window.
	 */
	public Money calculateBalance(AccountId accountId) {
		Money depositBalance = activities.stream()
				.filter(a -> a.getTargetAccountId().equals(accountId))
				.map(Activity::getMoney)
				.reduce(Money.ZERO, Money::add);

		Money withdrawalBalance = activities.stream()
				.filter(a -> a.getSourceAccountId().equals(accountId))
				.map(Activity::getMoney)
				.reduce(Money.ZERO, Money::add);

		return Money.add(depositBalance, withdrawalBalance.negate());
	}

	public ActivityWindow(@NonNull List<Activity> activities) {
		this.activities = activities;
	}

	public ActivityWindow(@NonNull Activity... activities) {
		this.activities = new ArrayList<>(Arrays.asList(activities));
	}

	public List<Activity> getActivities() {
		return Collections.unmodifiableList(this.activities);
	}

	public void addActivity(Activity activity) {
		this.activities.add(activity);
	}
}