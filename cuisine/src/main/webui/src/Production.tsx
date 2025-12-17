import { useEffect, useState } from "react";
import { useSSE } from "react-hooks-sse";
import { Alert, Snackbar, Box } from "@mui/material";
import {CommandeAProduire, Message} from "./api/model";
import { CommandeCard } from "./CommandeCard";

export function Production() {
    const premiereCommandeDuService = useSSE<Message | undefined>(
        "PremiereCommandeDuService",
        undefined
    );

    const commandeAProduire = useSSE<CommandeAProduire | undefined>(
        "CommandeAProduire",
        undefined
    );

    // Snackbar
    const [open, setOpen] = useState(false);
    const [message, setMessage] = useState<Message | null>(null);

    // Liste dynamique de commandes
    const [commandes, setCommandes] = useState<CommandeAProduire[]>([]);

    // Pour afficher "PremiereCommandeDuService"
    useEffect(() => {
        if (premiereCommandeDuService) {
            setMessage(premiereCommandeDuService);
            setOpen(true);
        }
    }, [premiereCommandeDuService]);

    // Pour ajouter chaque commande à la liste
    useEffect(() => {
        if (commandeAProduire) {
            setCommandes((prev) => [...prev, commandeAProduire]);
        }
    }, [commandeAProduire]);

    const removeCommande = (id: string) => {
        setCommandes((prev) => prev.filter((c) => c.id !== id));
    };

    return (
        <>
            {/* Snackbar */}
            <Snackbar
                open={open}
                onClose={() => setOpen(false)}
                anchorOrigin={{ vertical: "top", horizontal: "center" }}
            >
                <Alert
                    onClose={() => setOpen(false)}
                    severity="info"
                    variant="filled"
                    sx={{ width: "100%" }}
                >
                    {message?.message}
                </Alert>
            </Snackbar>

            {/* Liste des commandes */}
            <Box sx={{ padding: 2 }}>
                {commandes.map((c) => (
                    <CommandeCard
                        key={c.id}
                        commande={c}
                        onTerminee={removeCommande}
                    />
                ))}
            </Box>
        </>
    );
}
