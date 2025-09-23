import React, { useEffect } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Auth from "./pages/Auth";
import View from "./pages/View.jsx";
import { Toaster } from "sonner";
import { useDispatch, useSelector } from "react-redux";
import { isTokenValid } from "./Redux/Auth/isTokenValid.js";
import NavBar from "./components/Navbar/Navbar.jsx";

const App = () => {
  const { isAuthenticated, accessToken } = useSelector((store) => store.authStore);
  const dispatch = useDispatch();

  useEffect(() => {
    if (accessToken && !isTokenValid(accessToken)) {
      dispatch({ type: LOGOUT });
    }
  }, [accessToken, dispatch]);

  return (
    <>
      <Toaster richColors position="top-right" />
      {!isAuthenticated ? (
        <Auth />
      ) : (
        <div>
          <NavBar />
          <Routes>
            <Route path="/" element={<View />} />
          </Routes>
        </div>
      )}
    </>
  );
};

export default App;
