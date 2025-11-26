import Navbar from "./Navbar";
import { Outlet, useLocation } from "react-router-dom";

export default function Layout() {
  const location = useLocation();
  const isFullscreenPage = 
    location.pathname === "/" ||
    location.pathname === "/member-dashboard" ||
    location.pathname === "/books" ||
    location.pathname === "/my-loans" ||
    location.pathname === "/my-reservations" ||
    location.pathname === "/admin/users" ||
    location.pathname === "/admin/books" ||
    location.pathname === "/admin/authors" ||
    location.pathname === "/admin/loans" ||
    location.pathname === "/admin/reservations" ||
    location.pathname === "/admin/settings";

  return (
    <>
      <Navbar />
      <div className={isFullscreenPage ? "w-screen h-screen overflow-auto" : "p-6 max-w-6xl mx-auto"}>
        <Outlet />
      </div>
    </>
  );
}

