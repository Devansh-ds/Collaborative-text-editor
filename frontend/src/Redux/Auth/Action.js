import { toast } from "sonner";
import { BASE_URL } from "../config.js";
import { LOGIN_FAILURE, LOGIN_REQUEST, LOGIN_SUCCESS, REGISTER_FAILURE, REGISTER_REQUEST, REGISTER_SUCCESS } from "./ActionType.js";

export const register = (userData) => async (dispatch) => {
  try {
    dispatch({ type: REGISTER_REQUEST });

    const res = await fetch(`${BASE_URL}/auth/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(userData),
    });

    const resData = await res.json();

    if (!res.ok) {
      if (res.status === 409) {
        toast.error("Username or email already taken.");
      }
      dispatch({ type: REGISTER_FAILURE, payload: { error: resData } });
      return;
    }

    localStorage.setItem("accessToken", resData.accessToken);
    localStorage.setItem("refreshToken", resData.refreshToken);
    localStorage.setItem("displayName", resData.displayName);

    dispatch({
      type: REGISTER_SUCCESS,
      payload: {
        accessToken: resData.accessToken,
        refreshToken: resData.refreshToken,
        displayName: resData.displayName,
      },
    });
    toast.success("Created new account!");
  } catch (error) {
    console.log("Register (error): ", error);
    toast.error("Something went wrong!");
    dispatch({ type: REGISTER_FAILURE, payload: "Something went wrong!" });
  }
};

export const login = (userData) => async (dispatch) => {
  try {
    dispatch({ type: LOGIN_REQUEST });

    const res = await fetch(`${BASE_URL}/auth/authenticate`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(userData),
    });

    const resData = await res.json();

    if (!res.ok) {
      toast.error("Email or password is incorrect");
      dispatch({ type: LOGIN_FAILURE, payload: { error: resData } });
      return;
    }

    localStorage.setItem("accessToken", resData.accessToken);
    localStorage.setItem("refreshToken", resData.refreshToken);
    localStorage.setItem("displayName", resData.displayName);

    dispatch({
      type: LOGIN_SUCCESS,
      payload: {
        accessToken: resData.accessToken,
        refreshToken: resData.refreshToken,
        displayName: resData.displayName,
      },
    });
    toast.success("Successfully logged in!");
  } catch (error) {
    console.log("Login (error): ", error);
    toast.error("Email or password is incorrect!!");
    dispatch({ type: LOGIN_FAILURE, payload: "Something went wrong!" });
  }
};
