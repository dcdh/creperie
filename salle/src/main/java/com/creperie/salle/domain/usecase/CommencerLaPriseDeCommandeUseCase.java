package com.creperie.salle.domain.usecase;

import com.creperie.salle.domain.*;
import com.creperie.salle.domain.command.CommencerLaPriseDeCommande;
import com.damdamdeo.pulse.extension.core.DuplicateAggregateException;
import com.damdamdeo.pulse.extension.core.SequenceNumber;
import com.damdamdeo.pulse.extension.core.command.CommandHandler;
import com.damdamdeo.pulse.extension.core.usecase.AbstractCreationalDomainUseCase;
import com.damdamdeo.pulse.extension.core.usecase.UseCaseExecutionException;
import com.damdamdeo.pulse.extension.core.usecase.audience.Audience;
import com.damdamdeo.pulse.extension.core.usecase.audience.Everyone;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class CommencerLaPriseDeCommandeUseCase extends AbstractCreationalDomainUseCase<CommandeIdentifier, CommencerLaPriseDeCommande, Commande> {

    private final DatePriseDeCommandeProvider datePriseDeCommandeProvider;

    public CommencerLaPriseDeCommandeUseCase(final CommandHandler<Commande, CommandeIdentifier> commandHandler,
                                             final DatePriseDeCommandeProvider datePriseDeCommandeProvider) {
        super(commandHandler);
        this.datePriseDeCommandeProvider = datePriseDeCommandeProvider;
    }

    @Override
    protected CommencerLaPriseDeCommande onBefore(final CommencerLaPriseDeCommande command) throws UseCaseExecutionException {
        Objects.requireNonNull(command);
        final DatePriseDeCommande datePriseDeCommande = datePriseDeCommandeProvider.provide();
        return command.with(datePriseDeCommande);
    }

    @Override
    protected Function<SequenceNumber, CommandeIdentifier> creational(final CommencerLaPriseDeCommande commencerLaPriseDeCommande) {
        return sequenceNumber -> new CommandeIdentifier(commencerLaPriseDeCommande.numeroDeTable(), sequenceNumber);
    }

    @Override
    protected Function<CommandeIdentifier, DuplicateAggregateException> duplicateAggregateException() {
        return DuplicateCommandException::new;
    }

    @Override
    public List<Audience> audiences() {
        return List.of(Everyone.INSTANCE);
    }
}
