import React, {useEffect, useState} from 'react';
import {
    Box,
    Card,
    CardContent,
    CircularProgress,
    Divider,
    Grid,
    List,
    ListItem,
    ListItemText,
    Typography,
} from '@mui/material';

import {getEvents, getStatistiquesCommandes, getStatistiquesFrequentation} from './api/endpoints.ts';

import type {AuditEvent, CommandeStatistique, FrequentationStatistique,} from './api/model';

const DashboardStatistiques: React.FC = () => {
    const [events, setEvents] = useState<AuditEvent[]>([]);
    const [commandes, setCommandes] = useState<CommandeStatistique[]>([]);
    const [frequentations, setFrequentations] = useState<FrequentationStatistique[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const load = async () => {
            try {
                const [eventsRes, commandesRes, frequentationsRes] = await Promise.all([
                    getEvents(),
                    getStatistiquesCommandes(),
                    getStatistiquesFrequentation(),
                ]);

                setEvents(eventsRes ?? []);
                setCommandes(commandesRes ?? []);
                setFrequentations(frequentationsRes ?? []);
            } finally {
                setLoading(false);
            }
        };

        load();
    }, []);

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
                <CircularProgress/>
            </Box>
        );
    }

    return (
        <Grid container spacing={3} padding={3}>
            {/* Colonne gauche : Statistiques */}
            <Grid size={{ xs: 12, md: 8 }}>
                <Grid container spacing={3}>
                    {/* Carte fréquentation */}
                    <Grid size={{ xs: 12 }}>
                        <Card elevation={4} sx={{borderRadius: 2}}>
                            <CardContent>
                                <Typography variant="h6" gutterBottom>
                                    Statistiques de fréquentation
                                </Typography>

                                {frequentations.map((f) => (
                                    <Box
                                        key={f.dateDeService}
                                        display="flex"
                                        justifyContent="space-between"
                                        py={0.5}
                                    >
                                        <Typography variant="body2">
                                            {f.dateDeService}
                                        </Typography>
                                        <Typography variant="body2" fontWeight="bold">
                                            {f.nombreDeClients} clients
                                        </Typography>
                                    </Box>
                                ))}
                            </CardContent>
                        </Card>
                    </Grid>

                    {/* Carte commandes */}
                    <Grid size={{ xs: 12 }}>
                        <Card elevation={4} sx={{borderRadius: 2}}>
                            <CardContent>
                                <Typography variant="h6" gutterBottom>
                                    Statistiques des commandes
                                </Typography>

                                {commandes.map((c) => (
                                    <Box key={c.dateDeService} mb={2}>
                                        <Typography variant="subtitle2" gutterBottom>
                                            Date de service : {c.dateDeService}
                                        </Typography>

                                        <Grid container spacing={1}>
                                            <Grid size={{ xs: 6, md: 4 }}>
                                                <Typography variant="body2">Total : {c.nombreDeCommandes}</Typography>
                                            </Grid>
                                            <Grid size={{ xs: 6, md: 4 }}>
                                                <Typography variant="body2">
                                                    En cours de prise : {c.nombreDeCommandesEnCoursDePrise}
                                                </Typography>
                                            </Grid>
                                            <Grid size={{ xs: 6, md: 4 }}>
                                                <Typography variant="body2">
                                                    Finalisées : {c.nombreDeCommandesFinalisees}
                                                </Typography>
                                            </Grid>
                                            <Grid size={{ xs: 6, md: 4 }}>
                                                <Typography variant="body2">
                                                    En production : {c.nombreDeCommandesEnProduction}
                                                </Typography>
                                            </Grid>
                                            <Grid size={{ xs: 6, md: 4 }}>
                                                <Typography variant="body2">
                                                    Produites : {c.nombreDeCommandeProduite}
                                                </Typography>
                                            </Grid>
                                        </Grid>

                                        {c.plats.length > 0 && (
                                            <Box mt={1}>
                                                <Typography variant="caption" fontWeight="bold">
                                                    Plats commandés
                                                </Typography>
                                                {c.plats.map((p) => (
                                                    <Box
                                                        key={p.nom}
                                                        display="flex"
                                                        justifyContent="space-between"
                                                    >
                                                        <Typography variant="caption">{p.nom}</Typography>
                                                        <Typography variant="caption">
                                                            {p.nombreDePlatsCommandes}
                                                        </Typography>
                                                    </Box>
                                                ))}
                                            </Box>
                                        )}

                                        <Divider sx={{mt: 2}}/>
                                    </Box>
                                ))}
                            </CardContent>
                        </Card>
                    </Grid>
                </Grid>
            </Grid>

            {/* Colonne droite : événements */}
            <Grid size={{ xs: 12, md: 4 }}>
                <Card elevation={4} sx={{borderRadius: 2, height: "100%"}}>
                    <CardContent>
                        <Typography variant="h6" gutterBottom>
                            Événements
                        </Typography>

                        <List dense sx={{maxHeight: "70vh", overflow: "auto"}}>
                            {events.map((event, index) => (
                                <React.Fragment key={index}>
                                    <ListItem alignItems="flex-start">
                                        <ListItemText
                                            primary={event.eventType}
                                            secondary={
                                                <>
                                                    <Typography variant="caption" display="block">
                                                        {event.functionalDomain} – {event.creationDate}
                                                    </Typography>
                                                    <Typography variant="body2">{event.message}</Typography>
                                                </>
                                            }
                                        />
                                    </ListItem>
                                    <Divider component="li"/>
                                </React.Fragment>
                            ))}
                        </List>
                    </CardContent>
                </Card>
            </Grid>
        </Grid>
    );
};

export default DashboardStatistiques;
