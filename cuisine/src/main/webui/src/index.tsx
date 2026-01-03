import ReactDOM from 'react-dom/client';
import * as React from 'react';
import {StyledEngineProvider} from '@mui/material/styles';
import {SSEProvider} from 'react-hooks-sse';
import {Production} from './Production.tsx';

const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
);
root.render(
    <React.StrictMode>
        <StyledEngineProvider injectFirst>
            <SSEProvider endpoint="/notifier/sse/stream">
                <Production/>
            </SSEProvider>
        </StyledEngineProvider>
    </React.StrictMode>
);
