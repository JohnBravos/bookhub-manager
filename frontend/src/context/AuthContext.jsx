import { createContext, useState } from "react";

const AuthContext = createContext(null);

export function AuthProvider({children}) {
    const [authState, setAuthState] = useState(() => {
        const savedToken = localStorage.getItem("token");
        const savedUser = localStorage.getItem("user");

        if (savedToken && savedUser) {
            try {
                return {
                    user: JSON.parse(savedUser),
                    token: savedToken,
                    loading: false
                };
            } catch {
                return { user: null, token: null, loading: false };
            }
        }

        return { user: null, token: null, loading: false };
    });

    const { user, token, loading } = authState;

    const login = (userData, jwtToken) => {
        setAuthState({ user: userData, token: jwtToken, loading: false });
        localStorage.setItem("token", jwtToken);
        localStorage.setItem("user", JSON.stringify(userData));
    };

    const logout = () => {
        setAuthState({ user: null, token: null, loading: false });
        localStorage.removeItem("token");
        localStorage.removeItem("user");
    };

    const value = {user, token, loading, login, logout};

      return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export { AuthContext };