import { useState, useEffect } from 'react';
import axios from '../api/axios';

export const useBackendWakeup = () => {
    const [isReady, setIsReady] = useState(false);
    const [isWaking, setIsWaking] = useState(true);

    useEffect(() => {
        const wakeBackend = async () => {
            try {
                await axios.get('/health', { timeout: 30000 });
                setIsReady(true);
                setIsWaking(false);
            } catch (error) {
                console.log("Backend waking up, retrying...", error);
                setTimeout(wakeBackend, 2000);
            }
        };

        wakeBackend();
    }, []);

    return { isReady, isWaking };
}