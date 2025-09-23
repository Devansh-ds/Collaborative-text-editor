import { applyMiddleware, combineReducers, legacy_createStore } from "redux";
import { thunk } from "redux-thunk";
import { authReducer } from "./Auth/Reducer.js";
import { docReduer } from "./Document/Reducer.js";

const rootReducer = combineReducers({
  authStore: authReducer,
  docStore: docReduer,
});

export const store = legacy_createStore(rootReducer, applyMiddleware(thunk));
