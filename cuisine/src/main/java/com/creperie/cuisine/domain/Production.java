package com.creperie.cuisine.domain;

import com.creperie.cuisine.domain.command.MarkProductionTerminee;
import com.creperie.cuisine.domain.command.ProduireCommande;
import com.creperie.cuisine.domain.event.CommandeAProduire;
import com.creperie.cuisine.domain.event.ProductionTerminee;
import com.damdamdeo.pulse.extension.core.AggregateRoot;
import com.damdamdeo.pulse.extension.core.BelongsTo;
import com.damdamdeo.pulse.extension.core.event.EventAppender;
import com.damdamdeo.pulse.extension.core.event.OwnedBy;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Production extends AggregateRoot<PreparationIdentifier> {

    private List<Plat> plats;
    private Status status;

    public Production(PreparationIdentifier id) {
        super(id);
    }

    public Production(PreparationIdentifier id, List<Plat> plats, Status status) {
        super(id);
        this.plats = plats;
        this.status = status;
    }

    public void handle(final ProduireCommande produireCommande, final EventAppender eventAppender) {
        Objects.requireNonNull(produireCommande);
        Objects.requireNonNull(eventAppender);
        eventAppender.append(new CommandeAProduire(produireCommande.plats()));
    }

    public void handle(final MarkProductionTerminee markProductionTerminee, final EventAppender eventAppender) {
        Objects.requireNonNull(markProductionTerminee);
        Objects.requireNonNull(eventAppender);
        eventAppender.append(new ProductionTerminee());
    }

    public void on(final CommandeAProduire commandeAProduire) {
        this.plats = commandeAProduire.plats();
        this.status = Status.A_PRODUIRE;
    }

    public void on(final ProductionTerminee productionTerminee) {
        this.status = Status.PRODUCTION_TERMINEE;
    }

    public List<Plat> plats() {
        return Collections.unmodifiableList(plats);
    }

    public Status status() {
        return status;
    }

    @Override
    public BelongsTo belongsTo() {
        return new BelongsTo(id);
    }

    @Override
    public OwnedBy ownedBy() {
        return new OwnedBy("TOUT_LE_MONDE");
    }
}
