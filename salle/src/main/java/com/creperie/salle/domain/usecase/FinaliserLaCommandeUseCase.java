package com.creperie.salle.domain.usecase;

import com.creperie.salle.domain.Commande;
import com.creperie.salle.domain.CommandeIdentifier;
import com.creperie.salle.domain.CommandeInconnueException;
import com.creperie.salle.domain.command.FinaliserLaCommande;
import com.damdamdeo.pulse.extension.core.MissingAggregateException;
import com.damdamdeo.pulse.extension.core.command.CommandHandler;
import com.damdamdeo.pulse.extension.core.usecase.AbstractDomainUseCase;
import com.damdamdeo.pulse.extension.core.usecase.audience.Audience;
import com.damdamdeo.pulse.extension.core.usecase.audience.Everyone;

import java.util.List;
import java.util.function.Supplier;

public class FinaliserLaCommandeUseCase extends AbstractDomainUseCase<CommandeIdentifier, FinaliserLaCommande, Commande> {

    public FinaliserLaCommandeUseCase(final CommandHandler<Commande, CommandeIdentifier> commandHandler) {
        super(commandHandler);
    }

    @Override
    protected Supplier<MissingAggregateException> missingAggregateException() {
        return CommandeInconnueException::new;
    }

    @Override
    public List<Audience> audiences() {
        return List.of(Everyone.INSTANCE);
    }
}
