import {Button, Card, CardActions, CardContent, Typography} from '@mui/material';
import type {CommandeAProduire} from './api/model';
import {postProductionMarkProductionTerminee} from "./api/endpoints.ts";

interface Props {
    commande: CommandeAProduire;
    onTerminee: (id: string) => void;
}

export function CommandeCard({commande, onTerminee}: Props) {
    const handleClick = async () => {
        await postProductionMarkProductionTerminee({id: commande.id});
        onTerminee(commande.id);
    };

    return (
        <Card sx={{marginBottom: 2}}>
            <CardContent>
                <Typography variant="h6">
                    Commande #{commande.id}
                </Typography>

                <Typography variant="subtitle1">Plats :</Typography>
                <ul>
                    {commande.plats.map((plat, i) => (
                        <li key={i}>{plat.nom}</li>
                    ))}
                </ul>
            </CardContent>

            <CardActions>
                <Button
                    variant="contained"
                    color="success"
                    onClick={handleClick}
                >
                    Production terminée
                </Button>
            </CardActions>
        </Card>
    );
}
