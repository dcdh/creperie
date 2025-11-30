import { TextField, Button, Card, CardContent, Typography, Stack } from "@mui/material";
import {useState} from "react";
import {
    postPriseDeCommandeCommencerLaPriseDeCommande,
    postPriseDeCommandeNumeroDeTableAjouterPlat, postPriseDeCommandeNumeroDeTableFinaliserLaCommande
} from "./api/endpoints.ts";
import {Commande, Status} from "./api/model";

export default function PriseDeCommande() {
    const [numeroDeTable, setNumeroDeTable] = useState(1);
    const [nombreDeConvives, setNombreDeConvives] = useState(1);
    const [platNom, setPlatNom] = useState("");
    const [commande, setCommande] = useState<Commande | null>(null);
    const saisiCompositionTable = commande === null || commande.status === Status.FINALISEE;
    const saisiContenuCommande = commande !== null && commande.status === Status.EN_COURS_DE_PRISE;

    const commencer = async () => {
        const res = await postPriseDeCommandeCommencerLaPriseDeCommande({
            numeroDeTable,
            nombreDeConvives,
        });
        setCommande(res);
    };


    const ajouterPlat = async () => {
        const res = await postPriseDeCommandeNumeroDeTableAjouterPlat(numeroDeTable, {
            nom: platNom,
        });
        setCommande(res);
        setPlatNom("");
    };


    const finaliser = async () => {
        const res = await postPriseDeCommandeNumeroDeTableFinaliserLaCommande(numeroDeTable);
        setCommande(res);
    };


    return (
        <div className="p-6 grid place-items-center min-h-screen bg-gray-100">
            <Card className="w-full max-w-md shadow-xl rounded-2xl">
                <CardContent>
                    <Typography variant="h5" className="mb-4 text-center">
                        Prise de commande
                    </Typography>


                    <Stack spacing={3}>
                        <TextField
                            type="number"
                            label="Numéro de table"
                            value={numeroDeTable}
                            onChange={(e) => setNumeroDeTable(Number(e.target.value))} disabled={!saisiCompositionTable}
                            fullWidth
                        />


                        <TextField
                            type="number"
                            label="Nombre de convives"
                            value={nombreDeConvives}
                            onChange={(e) => setNombreDeConvives(Number(e.target.value))} disabled={!saisiCompositionTable}
                            fullWidth
                        />


                        <Button variant="contained" onClick={commencer} disabled={!saisiCompositionTable}>
                            Commencer la commande
                        </Button>


                        <TextField
                            select
                            label="Nom du plat"
                            value={platNom}
                            onChange={(e) => setPlatNom(e.target.value)}
                            SelectProps={{ native: true }}
                            fullWidth
                            disabled={!saisiContenuCommande}
                        >
                            <option value=""></option>
                            <option value="oeuf emental">oeuf emental</option>
                            <option value="oeuf jambon blanc">oeuf jambon blanc</option>
                            <option value="nutella">nutella</option>
                            <option value="caramel beurre salé">caramel beurre salé</option>
                        </TextField>


                        <Button variant="outlined" onClick={ajouterPlat} disabled={!platNom || !saisiContenuCommande}>
                            Ajouter un plat
                        </Button>


                        <Button color="success" variant="contained" onClick={finaliser} disabled={!saisiContenuCommande}>
                            Finaliser la commande
                        </Button>


                        {commande && (
                            <Card className="p-4 bg-gray-50 mt-4">
                                <Typography variant="subtitle1">Commande mise à jour :</Typography>
                                <pre className="text-sm mt-2 bg-gray-200 p-2 rounded">
{JSON.stringify(commande, null, 2)}
</pre>
                            </Card>
                        )}
                    </Stack>
                </CardContent>
            </Card>
        </div>
    );
}
