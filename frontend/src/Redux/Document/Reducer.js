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
} from "./ActionType";

const initialState = {
  allDocs: null,
  allDocsLoading: null,
  allDocsError: null,

  deleteDocError: null,
  deleteDocLoading: null,

  renameDocError: null,
  renameDocLoading: null,

  addUserError: null,
  addUserLoading: null,
  noUserError: null,

  createDocLoading: null,
  createDocError: null,
};

export const docReduer = (store = initialState, { type, payload }) => {
  switch (type) {
    case GET_ALL_DOC_REQUEST:
      return { ...store, allDocsLoading: true, allDocsError: null };
    case GET_ALL_DOC_SUCCESS:
      return { ...store, allDocs: payload, allDocsLoading: false, allDocsError: null };
    case GET_ALL_DOC_FAILURE:
      return { ...store, allDocsLoading: false, allDocsError: payload };
    case CREATE_NEW_DOC_REQUEST:
      return { ...store, createDocLoading: true, createDocError: null };
    case CREATE_NEW_DOC_SUCCESS:
      return { ...store, createDocLoading: false, createDocError: null };
    case CREATE_NEW_DOC_FAILURE:
      return { ...store, createDocLoading: false, createDocError: payload };
    case DELETE_DOC_REQUEST:
      return { ...store, deleteDocLoading: true, deleteDocError: null };
    case DELETE_DOC_SUCCESS:
      return { ...store, deleteDocLoading: false, deleteDocError: null };
    case DELETE_DOC_FAILURE:
      return { ...store, deleteDocLoading: false, deleteDocError: payload };
    case UPDATE_TITLE_REQUEST:
      return { ...store, renameDocLoading: true, renameDocError: null };
    case UPDATE_TITLE_SUCCESS:
      return { ...store, renameDocLoading: false, renameDocError: null };
    case UPDATE_TITLE_FAILURE:
      return { ...store, renameDocLoading: false, renameDocError: payload };
    case ADD_USER_REQUEST:
      return { ...store, addUserLoading: true, addUserError: null, noUserError: null };
    case ADD_USER_SUCCESS:
      return { ...store, addUserLoading: false, addUserError: null, noUserError: null };
    case ADD_USER_FAILURE:
      return { ...store, addUserLoading: false, addUserError: payload, noUserError: null };
    case NO_USER:
      return { ...store, addUserLoading: false, addUserError: payload, noUserError: payload };
    default:
      return store;
  }
};
