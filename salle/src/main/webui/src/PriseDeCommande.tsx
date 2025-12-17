import { TextField, Button, Card, CardContent, Typography, Stack, ToggleButton, ToggleButtonGroup } from '@mui/material';
import {useState} from "react";
import {
    postPriseDeCommandeCommencerLaPriseDeCommande,
    postPriseDeCommandeNumeroDeTableAjouterPlat, postPriseDeCommandeNumeroDeTableFinaliserLaCommande
} from "./api/endpoints.ts";
import {Response, Status} from "./api/model";

export default function PriseDeCommande() {
    const [numeroDeTable, setNumeroDeTable] = useState(1);
    const [nombreDeConvives, setNombreDeConvives] = useState(1);
    const [response, setResponse] = useState<Response | null>(null);
    const saisiCompositionTable = response === null || response.commande.status === Status.FINALISEE;
    const saisiContenuCommande = response !== null && response.commande.status === Status.EN_COURS_DE_PRISE;

    const commencer = async () => {
        const res = await postPriseDeCommandeCommencerLaPriseDeCommande({
            numeroDeTable,
            nombreDeConvives,
        });
        setResponse(res);
    };


    const ajouterPlat = async (nom: string) => {
        const res = await postPriseDeCommandeNumeroDeTableAjouterPlat(numeroDeTable, {
            nom: nom,
        });
        setResponse(res);
    };


    const finaliser = async () => {
        const res = await postPriseDeCommandeNumeroDeTableFinaliserLaCommande(numeroDeTable);
        setResponse(res);
    };


    return (
        <div className="p-6 grid place-items-center min-h-screen bg-gray-100">
            <Card className="w-full max-w-md shadow-xl rounded-2xl">
                <CardContent>
                    <Typography variant="h5" className="mb-4 text-center">
                        Prise de commande
                    </Typography>


                    <Stack spacing={3}>
                        <Typography variant="subtitle1" className="text-center">
                            Numéro de table
                        </Typography>

                        <ToggleButtonGroup
                            value={numeroDeTable}
                            exclusive
                            onChange={(_, value) => {
                                if (value !== null) setNumeroDeTable(value);
                            }}
                            disabled={!saisiCompositionTable}
                            fullWidth
                        >
                            {[...Array(10)].map((_, i) => (
                                <ToggleButton key={i + 1} value={i + 1}>
                                    {i + 1}
                                </ToggleButton>
                            ))}
                        </ToggleButtonGroup>

                        <TextField
                            type="number"
                            label="Nombre de convives"
                            value={nombreDeConvives}
                            onChange={(e) => setNombreDeConvives(Number(e.target.value))} disabled={!saisiCompositionTable}
                            fullWidth
                        />


                        <Button variant="contained" onClick={commencer} disabled={!saisiCompositionTable}>
                            Commencer la prise de commande
                        </Button>

                        <Typography variant="subtitle1" className="text-center">
                            Plat à ajouter
                        </Typography>

                        <Stack spacing={1} direction={"row"}>
                            {[
                                "oeuf emental",
                                "oeuf jambon blanc",
                                "nutella",
                                "caramel beurre salé",
                            ].map((plat) => {
                                return (
                                    <Button
                                        key={plat}
                                        variant={"outlined"}
                                        color={"inherit"}
                                        onClick={() => ajouterPlat(plat)}
                                        disabled={!saisiContenuCommande}
                                        fullWidth
                                    >
                                        {plat}
                                    </Button>
                                );
                            })}
                        </Stack>

                        <Button color="success" variant="contained" onClick={finaliser} disabled={!saisiContenuCommande}>
                            Finaliser la commande
                        </Button>


                        {response && (
                            <Card className="p-4 bg-gray-50 mt-4">
                                <Typography variant="subtitle1">Commande mise à jour :</Typography>
                                <pre className="text-sm mt-2 bg-gray-200 p-2 rounded">
{JSON.stringify(response, null, 2)}
</pre>
                            </Card>
                        )}
                    </Stack>
                </CardContent>
            </Card>
        </div>
    );
}
