import useAuth from "../hooks/useAuth";
import { Navigate } from "react-router-dom";

export default function ProtectedRoute({children, requiredRole}) {
    const {token, loading, user} = useAuth();

    if (loading) {
        return <div>Loading...</div>
    }

    if (!token) {
        return <Navigate to="/login" replace/>
    }

    if (requiredRole && user?.role?.toUpperCase() !== requiredRole.toUpperCase()) {
        return <Navigate to="/" replace/>
    }

    return children;
}