import React, { useState } from "react";
import doc_image from "../assets/doc_image.png";
import InputField from "../utils/InputField.jsx";
import { Transition } from "@headlessui/react";
import { useDispatch, useSelector } from "react-redux";
import { login, register } from "../Redux/Auth/Action.js";
import { toast } from "sonner";

const Auth = () => {
  const [signingIn, setSigningIn] = useState(true);
  const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const dispatch = useDispatch();
  const authStore = useSelector((store) => store.authStore);

  const handleSubmit = () => {
    if (!signingIn) {
      if (password !== confirmPassword) {
        toast.error("Password and confirm password must be same.");
      } else {
        dispatch(register({ username: username.trim(), email: email.trim(), password: password.trim() }));
      }
    } else {
      dispatch(login({ email: email.trim(), password: password.trim() }));
    }
  };

  return (
    <div className="bg-[#f0f4f9] h-screen flex justify-center items-center">
      <div className={`transition-all duration-[600ms] bg-white w-9/12 rounded-3xl flex space-between ${signingIn ? "h-3/6" : "h-4/6"}`}>
        {/* left side for the logo and the text */}
        <div className="w-1/2 p-8 flex flex-col">
          <img src={doc_image} width={50} height={50} className="mb-8" />
          <h1 className="text-4xl">{signingIn ? "Sign In" : "Create account"}</h1>
          <p className="text-black mt-4 text-2xl">Continue to Docs lite</p>
        </div>
        {/* right side for the input and button */}
        <div className="w-1/2 p-8 flex flex-col justify-center items-center">
          {/* input field part */}
          <div className="w-full flex-1 flex flex-col justify-center mr-6">
            <Transition
              show={!signingIn}
              enter="transition-opacity duration-500"
              enterFrom="opacity-0"
              enterTo="opacity-100"
              leave="transition-opacity duration-500"
              leaveFrom="opacity-100"
              leaveTo="opacity-0"
            >
              <div>
                <InputField value={username} setValue={setUsername} label="Username" type="text" />
              </div>
            </Transition>
            <div>
              <InputField value={email} setValue={setEmail} label="Email" type="email" />
            </div>
            <div>
              <InputField value={password} setValue={setPassword} label="Password" type="password" />
            </div>
            <Transition
              show={!signingIn}
              enter="transition-opacity duration-500"
              enterFrom="opacity-0"
              enterTo="opacity-100"
              leave="transition-opacity duration-500"
              leaveFrom="opacity-100"
              leaveTo="opacity-0"
            >
              <div>
                <InputField value={confirmPassword} setValue={setConfirmPassword} label="Confirm Password" type="password" />
              </div>
            </Transition>
          </div>
          {/* button part */}
          <div className="flex justify-end w-full">
            <button
              onClick={() => {
                setSigningIn(!signingIn);
              }}
              disabled={authStore?.loading}
              className="text-blue-600 bg-white mr-6 self-end px-4 py-2 mb-4 rounded-3xl hover:bg-slate-100"
            >
              {signingIn ? "Create an account" : "Already have an account"}
            </button>
            <button
              onClick={handleSubmit}
              className={`${
                authStore?.loading ? "hover:bg-slate-800" : "hover:bg-[#0e4eb5]"
              } self-end text-white px-4 py-2 mb-4 mr-4 rounded-3xl shadow-md ${authStore?.loading ? "bg-slate-500" : "bg-[#0b57d0]"} `}
              disabled={authStore?.loading}
            >
              {authStore?.loading ? "Processing" : signingIn ? "Sign in" : "Sign up"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Auth;
