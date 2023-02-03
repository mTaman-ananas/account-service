package com.siriusxi.hexarch.account.port.in;

import com.siriusxi.hexarch.account.core.domain.Account.AccountId;
import com.siriusxi.hexarch.account.core.domain.Money;
import com.siriusxi.hexarch.infra.common.SelfValidating;
import lombok.EqualsAndHashCode;
import lombok.Value;

import jakarta.validation.constraints.NotNull;

@Value
@EqualsAndHashCode(callSuper = false)
public
class SendMoneyCommand extends SelfValidating<SendMoneyCommand> {

    @NotNull
    AccountId sourceAccountId;

    @NotNull
    AccountId targetAccountId;

    @NotNull
    Money money;

    public SendMoneyCommand(
            AccountId sourceAccountId,
            AccountId targetAccountId,
            Money money) {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.money = money;
        this.validateSelf();
    }
}
