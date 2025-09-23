import { toast } from "sonner";
import { BASE_URL } from "../config.js";
import {
  ADD_USER_FAILURE,
  ADD_USER_REQUEST,
  ADD_USER_SUCCESS,
  CREATE_NEW_DOC_FAILURE,
  CREATE_NEW_DOC_REQUEST,
  CREATE_NEW_DOC_SUCCESS,
  DELETE_DOC_FAILURE,
  DELETE_DOC_REQUEST,
  DELETE_DOC_SUCCESS,
  GET_ALL_DOC_FAILURE,
  GET_ALL_DOC_REQUEST,
  GET_ALL_DOC_SUCCESS,
  NO_USER,
  UPDATE_TITLE_FAILURE,
  UPDATE_TITLE_REQUEST,
  UPDATE_TITLE_SUCCESS,
} from "./ActionType.js";

export const getAllDocs = (userData) => async (dispatch) => {
  try {
    dispatch({ type: GET_ALL_DOC_REQUEST });

    const res = await fetch(`${BASE_URL}/api/docs/all`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
      },
    });

    const resData = await res.json();

    if (!res.ok) {
      toast.error("Something went wrong.");
      dispatch({ type: GET_ALL_DOC_FAILURE, payload: resData });
      return;
    }

    dispatch({
      type: GET_ALL_DOC_SUCCESS,
      payload: resData,
    });
  } catch (error) {
    console.log("get all docs (error): ", error);
    toast.error("Something went wrong!");
    dispatch({ type: GET_ALL_DOC_FAILURE, payload: "Something went wrong!" });
  }
};

export const createNewDoc = (newDocData) => async (dispatch) => {
  try {
    dispatch({ type: CREATE_NEW_DOC_REQUEST });

    const res = await fetch(`${BASE_URL}/api/docs/create`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
      },
      body: JSON.stringify(newDocData),
    });

    const resData = await res.json();

    if (!res.ok) {
      toast.error("Something went wrong.");
      dispatch({ type: CREATE_NEW_DOC_FAILURE, payload: resData });
      return;
    }

    dispatch({
      type: CREATE_NEW_DOC_SUCCESS,
      payload: resData,
    });
    toast.success("Document created successfully!");
  } catch (error) {
    console.log("Create new doc (error): ", error);
    toast.error("Something went wrong!");
    dispatch({ type: CREATE_NEW_DOC_FAILURE, payload: error });
  }
};

export const deleteDoc = (docId) => async (dispatch) => {
  try {
    dispatch({ type: DELETE_DOC_REQUEST });

    const res = await fetch(`${BASE_URL}/api/docs/delete/${docId}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
      },
    });

    const resData = await res.json();

    if (!res.ok) {
      toast.error("Document does not exist");
      dispatch({ type: DELETE_DOC_FAILURE, payload: resData });
      return;
    }

    dispatch({
      type: DELETE_DOC_SUCCESS,
      payload: resData,
    });
    toast.success("Document deleted successfully!");
  } catch (error) {
    console.log("delete doc (error): ", error);
    toast.error("Something went wrong!");
    dispatch({ type: DELETE_DOC_FAILURE, payload: error });
  }
};

export const renaemDoc =
  ({ docId, reqData, setError }) =>
  async (dispatch) => {
    try {
      dispatch({ type: UPDATE_TITLE_REQUEST });

      const res = await fetch(`${BASE_URL}/api/docs/rename/${docId}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
        },
        body: JSON.stringify(reqData),
      });

      const resData = await res.text();

      if (!res.ok) {
        toast.error("Title could not be updated");
        dispatch({ type: UPDATE_TITLE_FAILURE, payload: resData });
        return;
      }

      dispatch({
        type: UPDATE_TITLE_SUCCESS,
        payload: resData,
      });
      toast.success("Document renamed successfully!");
    } catch (error) {
      console.log("update title (error): ", error);
      toast.error("Something went wrong!");
      dispatch({ type: UPDATE_TITLE_FAILURE, payload: error });
    }
  };

// export const addUser =
//   ({ docId, reqData }) =>
//   async (dispatch) => {
//     try {
//       dispatch({ type: ADD_USER_REQUEST });

//       const res = await fetch(`${BASE_URL}/api/docs/users/add/${docId}`, {
//         method: "PATCH",
//         headers: {
//           "Content-Type": "application/json",
//           Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
//         },
//         body: JSON.stringify(reqData),
//       });

//       if (!res.ok) {
//         if (res.status === 404) {
//           dispatch({ type: NO_USER, payload: "No user found" });
//         } else {
//           toast.error("Adding user failed");
//           dispatch({ type: ADD_USER_FAILURE, payload: "No user found" });
//         }
//         return;
//       }

//       dispatch({
//         type: ADD_USER_SUCCESS,
//         payload: "nothing",
//       });
//       toast.success("Document shared with user successfully!");
//     } catch (error) {

//       console.log("add user (error): ", error);
//       toast.error("Something went wrong!");
//       dispatch({ type: ADD_USER_FAILURE, payload: error });
//     }
//   };

export const addUser =
  ({ docId, reqData }) =>
  async (dispatch) => {
    try {
      dispatch({ type: ADD_USER_REQUEST });

      const res = await fetch(`${BASE_URL}/api/docs/users/add/${docId}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
        },
        body: JSON.stringify(reqData),
      });

      if (!res.ok) {
        if (res.status === 404) {
          toast.error("User not found");
          dispatch({ type: NO_USER, payload: "No user found" });
          return { success: false, error: "No user found" };
        } else {
          toast.error("Adding user failed");
          dispatch({ type: ADD_USER_FAILURE, payload: "Add user failed" });
          return { success: false, error: "Add user failed" };
        }
      }

      dispatch({ type: ADD_USER_SUCCESS });
      toast.success("Document shared with user successfully!");
      return { success: true };
    } catch (error) {
      console.log("add user (error): ", error);
      toast.error("Something went wrong!");
      dispatch({ type: ADD_USER_FAILURE, payload: error });
      return { success: false, error };
    }
  };
