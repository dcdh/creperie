import {useEffect, useState} from "react";
import {useSSE} from "react-hooks-sse";
import {Alert, Snackbar} from "@mui/material";
import {CommandePretePourEtreServie} from "./api/model";

export function CuisineNotifications() {
    const cuisineNotification = useSSE<CommandePretePourEtreServie | undefined>(
        "CommandePretePourEtreServie",
        undefined
    );

    // État pour la Snackbar
    const [open, setOpen] = useState(false);
    const [message, setMessage] = useState("");

    useEffect(() => {
        if (cuisineNotification !== undefined) {
            setMessage(`La commande pour la table ${cuisineNotification.numeroDeTable} est prête !`);
            setOpen(true);
        }
    }, [cuisineNotification]);

    return (
        <>
            <Snackbar
                open={open}
                onClose={() => setOpen(false)}
                anchorOrigin={{vertical: "top", horizontal: "center"}}
            >
                <Alert
                    onClose={() => setOpen(false)}
                    severity="info"
                    variant="filled"
                    sx={{width: "100%"}}
                >
                    {message}
                </Alert>
            </Snackbar>
        </>
    );
}
