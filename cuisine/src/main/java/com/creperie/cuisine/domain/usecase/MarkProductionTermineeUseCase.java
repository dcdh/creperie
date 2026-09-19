package com.creperie.cuisine.domain.usecase;

import com.creperie.cuisine.domain.PreparationIdentifier;
import com.creperie.cuisine.domain.Production;
import com.creperie.cuisine.domain.ProductionMissingAggregateException;
import com.creperie.cuisine.domain.command.MarkProductionTerminee;
import com.damdamdeo.pulse.extension.core.MissingAggregateException;
import com.damdamdeo.pulse.extension.core.command.CommandHandler;
import com.damdamdeo.pulse.extension.core.usecase.AbstractDomainUseCase;
import com.damdamdeo.pulse.extension.core.usecase.audience.Audience;
import com.damdamdeo.pulse.extension.core.usecase.audience.Everyone;

import java.util.List;
import java.util.function.Supplier;

public class MarkProductionTermineeUseCase extends AbstractDomainUseCase<PreparationIdentifier, MarkProductionTerminee, Production> {

    protected MarkProductionTermineeUseCase(final CommandHandler<Production, PreparationIdentifier> commandHandler) {
        super(commandHandler);
    }

    @Override
    protected Supplier<MissingAggregateException> missingAggregateException() {
        return ProductionMissingAggregateException::new;
    }

    @Override
    public List<Audience> audiences() {
        return List.of(Everyone.INSTANCE);
    }
}
