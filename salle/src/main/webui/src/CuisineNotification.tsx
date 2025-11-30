import {useEffect, useState} from "react";
import {useSSE} from "react-hooks-sse";
import {Alert, Snackbar} from "@mui/material";

interface CommandePretePourEtreServieDTO {
    numeroDeTable: number;
}

export function CuisineNotifications() {
    const cuisineNotification = useSSE<CommandePretePourEtreServieDTO | undefined>(
        "cuisine",
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
                autoHideDuration={3000}
                onClose={() => setOpen(false)}
                anchorOrigin={{vertical: "bottom", horizontal: "center"}}
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
