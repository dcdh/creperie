package com.creperie.salle.domain;

import com.creperie.salle.domain.command.AjouterPlat;
import com.creperie.salle.domain.command.CommencerLaPriseDeCommande;
import com.creperie.salle.domain.command.FinaliserLaCommande;
import com.creperie.salle.domain.event.CommandeEnCoursDePrise;
import com.creperie.salle.domain.event.CommandeFinalisee;
import com.creperie.salle.domain.event.PlatAjoute;
import com.damdamdeo.pulse.extension.core.AggregateRoot;
import com.damdamdeo.pulse.extension.core.BelongsTo;
import com.damdamdeo.pulse.extension.core.BusinessException;
import com.damdamdeo.pulse.extension.core.ExecutionContext;
import com.damdamdeo.pulse.extension.core.event.EventAppender;
import com.damdamdeo.pulse.extension.core.event.OwnedBy;
import com.damdamdeo.pulse.extension.core.executedby.ExecutedBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Commande extends AggregateRoot<CommandeIdentifier> {

    private NombreDeConvives nombreDeConvives = new NombreDeConvives(0);
    private DatePriseDeCommande datePriseDeCommande;
    private List<Plat> plats = new ArrayList<>();
    private Status status = Status.INCONNU;

    public Commande(final CommandeIdentifier id) {
        super(id);
    }

    public Commande(final CommandeIdentifier id, final NombreDeConvives nombreDeConvives, final List<Plat> plats, final Status status) {
        super(id);
        this.nombreDeConvives = Objects.requireNonNull(nombreDeConvives);
        this.plats = Objects.requireNonNull(plats);
        this.status = Objects.requireNonNull(status);
    }

    public void handle(final CommencerLaPriseDeCommande commencerLaPriseDeCommande, final ExecutionContext executionContext, final EventAppender<CommandeIdentifier> eventAppender) throws BusinessException {
        Objects.requireNonNull(commencerLaPriseDeCommande);
        Objects.requireNonNull(executionContext);
        Objects.requireNonNull(eventAppender);
        eventAppender.append(new CommandeEnCoursDePrise(commencerLaPriseDeCommande.nombreDeConvives(),
                commencerLaPriseDeCommande.datePriseDeCommande()));
    }

    public void handle(final AjouterPlat ajouterPlat, final ExecutionContext executionContext, final EventAppender<CommandeIdentifier> eventAppender) throws BusinessException {
        Objects.requireNonNull(ajouterPlat);
        Objects.requireNonNull(executionContext);
        Objects.requireNonNull(eventAppender);
        eventAppender.append(new PlatAjoute(ajouterPlat.plat()));
    }

    public void handle(final FinaliserLaCommande finaliserLaCommande, final ExecutionContext executionContext, final EventAppender<CommandeIdentifier> eventAppender) throws BusinessException {
        Objects.requireNonNull(finaliserLaCommande);
        Objects.requireNonNull(executionContext);
        Objects.requireNonNull(eventAppender);
        eventAppender.append(new CommandeFinalisee());
    }

    public void on(final CommandeEnCoursDePrise commandeEnCoursDePrise, final ExecutedBy executedBy) {
        Objects.requireNonNull(commandeEnCoursDePrise);
        Objects.requireNonNull(executedBy);
        this.nombreDeConvives = commandeEnCoursDePrise.nombreDeConvives();
        this.datePriseDeCommande = commandeEnCoursDePrise.datePriseDeCommande();
        this.status = Status.EN_COURS_DE_PRISE;
    }

    public void on(final PlatAjoute platAjoute, final ExecutedBy executedBy) {
        Objects.requireNonNull(platAjoute);
        Objects.requireNonNull(executedBy);
        this.plats.add(platAjoute.plat());
    }

    public void on(final CommandeFinalisee commandeFinalisee, final ExecutedBy executedBy) {
        Objects.requireNonNull(commandeFinalisee);
        Objects.requireNonNull(executedBy);
        this.status = Status.FINALISEE;
    }

    public NombreDeConvives nombreDeConvives() {
        return nombreDeConvives;
    }

    public DatePriseDeCommande datePriseDeCommande() {
        return datePriseDeCommande;
    }

    public List<Plat> plats() {
        return Collections.unmodifiableList(plats);
    }

    public Status status() {
        return status;
    }

    @Override
    public BelongsTo belongsTo() {
        return BelongsTo.himself(this);
    }

    @Override
    public OwnedBy ownedBy() {
        return OwnedBy.himself(this);
    }
}
