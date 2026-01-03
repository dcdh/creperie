import ReactDOM from 'react-dom/client';
import * as React from 'react';
import {StyledEngineProvider} from '@mui/material/styles';
import PriseDeCommande from './PriseDeCommande.tsx';
import {SSEProvider} from 'react-hooks-sse';
import {CuisineNotifications} from './CuisineNotification.tsx';

const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
);
root.render(
    <React.StrictMode>
        <StyledEngineProvider injectFirst>
            <SSEProvider endpoint="/notifier/sse/stream">
                <PriseDeCommande/>
                <CuisineNotifications/>
            </SSEProvider>
        </StyledEngineProvider>
    </React.StrictMode>
);
