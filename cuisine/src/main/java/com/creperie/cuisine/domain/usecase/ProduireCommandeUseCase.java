package com.creperie.cuisine.domain.usecase;

import com.creperie.cuisine.domain.PreparationIdentifier;
import com.creperie.cuisine.domain.Production;
import com.creperie.cuisine.domain.ProductionDuplicatesException;
import com.creperie.cuisine.domain.command.ProduireCommande;
import com.damdamdeo.pulse.extension.core.DuplicateAggregateException;
import com.damdamdeo.pulse.extension.core.SequenceNumber;
import com.damdamdeo.pulse.extension.core.command.CommandHandler;
import com.damdamdeo.pulse.extension.core.usecase.AbstractCreationalDomainUseCase;
import com.damdamdeo.pulse.extension.core.usecase.audience.Audience;
import com.damdamdeo.pulse.extension.core.usecase.audience.Everyone;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ProduireCommandeUseCase extends AbstractCreationalDomainUseCase<PreparationIdentifier, ProduireCommande, Production> {

    protected ProduireCommandeUseCase(final CommandHandler<Production, PreparationIdentifier> commandHandler) {
        super(commandHandler);
    }

    @Override
    public List<Audience> audiences() {
        return List.of(Everyone.INSTANCE);
    }

    @Override
    protected Function<SequenceNumber, PreparationIdentifier> creational(final ProduireCommande produireCommande) {
        Objects.requireNonNull(produireCommande);
        return sequenceNumber -> new PreparationIdentifier("P" + produireCommande.commandeIdentifier().id());
    }

    @Override
    protected Function<PreparationIdentifier, DuplicateAggregateException> duplicateAggregateException() {
        return preparationIdentifier -> new ProductionDuplicatesException();
    }
}
