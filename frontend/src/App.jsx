import React, { useEffect } from "react";
import { BrowserRouter, Route, Routes, useNavigate } from "react-router-dom";
import Auth from "./pages/Auth";
import View from "./pages/View.jsx";
import { Toaster } from "sonner";
import { useDispatch, useSelector } from "react-redux";
import { isTokenValid } from "./Redux/Auth/isTokenValid.js";
import NavBar from "./components/Navbar/Navbar.jsx";
import Edit from "./pages/Edit.jsx";
import { StompSessionProvider } from "react-stomp-hooks";
import { BASE_URL } from "./Redux/config.js";

const App = () => {
  const { isAuthenticated, accessToken } = useSelector((store) => store.authStore);
  const dispatch = useDispatch();
  const displayName = localStorage.getItem("displayName");

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
            <Route path={"/edit/:docId"} element={<EditWrapper username={displayName} />} />
          </Routes>
        </div>
      )}
    </>
  );
};

function EditWrapper({ username }) {
  const navigate = useNavigate();
  const token = localStorage.getItem("accessToken");

  return (
    <StompSessionProvider
      url="ws://localhost:8080/docs/ws"
      connectHeaders={{
        Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
      }}
    >
      <Edit username={username} />
    </StompSessionProvider>
  );
}

export default App;
