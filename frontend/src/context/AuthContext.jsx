import { createContext, useEffect, useState } from "react";

export const AuthContext = createContext(null);

export function AuthProvider({children}) {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(null);
    const [loading, setLoading] = useState(null);

    // Φόρτωσε από localStorage όταν ανοίγει η εφαρμογή
    useEffect(() => {
        const savedToken = localStorage.getItem("token");
        const savedUser = localStorage.getItem("user");

        if (savedToken && savedUser) {
            setToken(savedToken);
            try {
                setUser(JSON.parse(savedUser));
            } catch {
                setUser(null);
            }
        }

        setLoading(false);
    }, []);

    const login = (userData, jwtToken) => {
        setUser(userData);
        setToken(jwtToken);
        localStorage.setItem("token", jwtToken);
        localStorage.setItem("user", JSON.stringify(userData));
    };

    const logout = () => {
        setUser(null);
        setToken(null);
        localStorage.removeItem("token");
        localStorage.removeItem("user");
    };

    const value = {user, token, loading, login, logout};

      return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}