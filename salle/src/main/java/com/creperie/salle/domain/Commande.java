package com.creperie.salle.domain;

import com.creperie.salle.domain.command.AjouterPlat;
import com.creperie.salle.domain.command.CommencerLaPriseDeCommande;
import com.creperie.salle.domain.command.FinaliserLaCommande;
import com.creperie.salle.domain.event.CommandeEnCoursDePrise;
import com.creperie.salle.domain.event.CommandeFinalisee;
import com.creperie.salle.domain.event.PlatAjoute;
import com.damdamdeo.pulse.extension.core.AggregateRoot;
import com.damdamdeo.pulse.extension.core.BelongsTo;
import com.damdamdeo.pulse.extension.core.event.EventAppender;
import com.damdamdeo.pulse.extension.core.event.OwnedBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Commande extends AggregateRoot<CommandeIdentifier> {

    private NombreDeConvives nombreDeConvives = new NombreDeConvives(0);
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

    public void handle(final CommencerLaPriseDeCommande commencerLaPriseDeCommande, final EventAppender eventAppender) {
        Objects.requireNonNull(commencerLaPriseDeCommande);
        Objects.requireNonNull(eventAppender);
        eventAppender.append(new CommandeEnCoursDePrise(commencerLaPriseDeCommande.nombreDeConvives().nombre()));
    }

    public void handle(final AjouterPlat ajouterPlat, final EventAppender eventAppender) {
        Objects.requireNonNull(ajouterPlat);
        Objects.requireNonNull(eventAppender);
        eventAppender.append(new PlatAjoute(ajouterPlat.plat().nom()));
    }

    public void handle(final FinaliserLaCommande finaliserLaCommande, final EventAppender eventAppender) {
        Objects.requireNonNull(finaliserLaCommande);
        Objects.requireNonNull(eventAppender);
        eventAppender.append(new CommandeFinalisee());
    }

    public void on(final CommandeEnCoursDePrise commandeEnCoursDePrise) {
        this.nombreDeConvives = new NombreDeConvives(commandeEnCoursDePrise.nombreDeConvives());
        this.status = Status.EN_COURS_DE_PRISE;
    }

    public void on(final PlatAjoute platAjoute) {
        this.plats.add(platAjoute.plat());
    }

    public void on(final CommandeFinalisee commandeFinalisee) {
        this.status = Status.FINALISEE;
    }

    public NombreDeConvives nombreDeConvives() {
        return nombreDeConvives;
    }

    public List<Plat> plats() {
        return plats;
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
