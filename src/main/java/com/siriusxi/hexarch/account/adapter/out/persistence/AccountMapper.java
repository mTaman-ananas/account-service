package com.siriusxi.hexarch.account.adapter.out.persistence;

import com.siriusxi.hexarch.account.core.domain.Account;
import com.siriusxi.hexarch.account.core.domain.Activity;
import com.siriusxi.hexarch.account.core.domain.ActivityWindow;
import com.siriusxi.hexarch.account.core.domain.Money;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static com.siriusxi.hexarch.account.core.domain.Account.*;
import static com.siriusxi.hexarch.account.core.domain.Activity.*;


@Mapper(componentModel = "spring")
interface AccountMapper {

    default Account toAccount(
            AccountEntity accountEntity,
            List<ActivityEntity> activities,
            Long withdrawalBalance,
            Long depositBalance) {

        Money baselineBalance = Money.subtract(
                Money.of(depositBalance),
                Money.of(withdrawalBalance));

        return withId(
                new AccountId(accountEntity.getId()),
                baselineBalance,
                toActivityWindow(activities));

    }

    default ActivityWindow toActivityWindow(List<ActivityEntity> activityEntities) {
        return new ActivityWindow(toActivities(activityEntities));
    }

    default List<Activity> toActivities(List<ActivityEntity> activityEntities) {
        return activityEntities
                .stream()
                .map(this::toActivity)
                .toList();
    }

    default Activity toActivity(ActivityEntity entity) {
        return new Activity(
                new ActivityId(entity.getId()),
                new AccountId(entity.getOwnerAccountId()),
                new AccountId(entity.getSourceAccountId()),
                new AccountId(entity.getTargetAccountId()),
                entity.getTimestamp(),
                Money.of(entity.getAmount()));
    }

    @Mapping(target = "id", expression = "java( activity.getId() == null ? null : activity.getId().value() )")
    @Mapping(target = "amount", source = "activity.money.amount")
    @Mapping(target = "targetAccountId", source = "activity.targetAccountId.value")
    @Mapping(target = "ownerAccountId", source = "activity.ownerAccountId.value")
    @Mapping(target = "sourceAccountId", source = "activity.sourceAccountId.value")
    @Mapping(target = "timestamp", source = "activity.timestamp")
    ActivityEntity toActivityEntity(Activity activity);

}
