import {
  LOGIN_FAILURE,
  LOGIN_REQUEST,
  LOGIN_SUCCESS,
  LOGOUT,
  REGISTER_FAILURE,
  REGISTER_REQUEST,
  REGISTER_SUCCESS,
} from "./ActionType.js";
import { jwtDecode } from "jwt-decode";
import { isTokenValid } from "./isTokenValid.js";

const storedAccessToken = localStorage.getItem("accessToken");
const storedRefreshToken = localStorage.getItem("refreshToken");

let decoded = null;
if (storedAccessToken && isTokenValid(storedAccessToken)) {
  decoded = jwtDecode(storedAccessToken);
} else {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");
}

const initialState = {
  accessToken: decoded ? storedAccessToken : null,
  refreshToken: decoded ? storedRefreshToken : null,
  isAuthenticated: !!decoded,
  loading: false,
  error: null,
  email: decoded?.sub || null,
  displayName: null,
};

export const authReducer = (store = initialState, { type, payload }) => {
  switch (type) {
    // Register
    case REGISTER_REQUEST:
      return { ...store, loading: true, error: null };
    case REGISTER_SUCCESS:
      const decoded = jwtDecode(payload.accessToken);
      return {
        ...store,
        accessToken: payload.accessToken,
        refreshToken: payload.refreshToken,
        email: decoded.username,
        isAuthenticated: true,
        loading: false,
        error: null,
        displayName: payload.displayName,
      };
    case REGISTER_FAILURE:
      return {
        ...store,
        loading: false,
        error: payload,
      };
    // Login
    case LOGIN_REQUEST:
      return { ...store, loading: true, error: null };
    case LOGIN_SUCCESS:
      const loginDecoded = jwtDecode(payload.accessToken);

      return {
        ...store,
        accessToken: payload.accessToken,
        refreshToken: payload.refreshToken,
        email: loginDecoded.sub,
        isAuthenticated: true,
        loading: false,
        error: null,
        displayName: payload.displayName,
      };
    case LOGIN_FAILURE:
      return {
        ...store,
        loading: false,
        error: payload,
      };
    case LOGOUT:
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      return {
        ...initialState,
        isAuthenticated: false,
      };
    default:
      return store;
  }
};
