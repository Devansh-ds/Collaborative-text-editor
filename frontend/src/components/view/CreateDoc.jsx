import { Dialog, DialogPanel, DialogTitle } from "@headlessui/react";
import React, { useState } from "react";
import InputField from "../../utils/InputField.jsx";
import { useDispatch, useSelector } from "react-redux";
import { createNewDoc, getAllDocs } from "../../Redux/Document/Action.js";

const CreateDoc = ({ isOpen, setIsOpen }) => {
  const [title, setTitle] = useState("");
  const [error, setError] = useState("");
  const dispatch = useDispatch();
  const docStore = useSelector((store) => store.docStore);

  const handleCreate = () => {
    if (!title.trim()) {
      setError("Title cannot be empty");
      return;
    }

    dispatch(createNewDoc({ title: title }))
      .then(() => {
        dispatch(getAllDocs());
        setTitle("");
        setError("");
        setIsOpen(false);
      })
      .catch((err) => {
        console.log("fucked create new doc: ", err);
      });
  };

  return (
    <Dialog open={isOpen} onClose={() => setIsOpen(false)} className="relative z-50">
      {/* Overlay */}
      <div className="fixed inset-0 bg-black/50" aria-hidden="true" />

      {/* Panel */}
      <div className="fixed inset-0 flex items-center justify-center p-4">
        <DialogPanel className="w-full max-w-md rounded-2xl bg-white p-8 shadow-lg space-y-6">
          <DialogTitle className="text-xl font-bold text-gray-800">Create New Document</DialogTitle>

          <div className="relative">
            <InputField type="text" value={title} setValue={setTitle} placeholder="" label={"Document title"} />

            {error && <p className="text-red-500 text-sm mt-1">{error}</p>}
          </div>

          <div className="flex justify-end gap-4 pt-2">
            <button
              onClick={() => {
                setIsOpen(false);
                setError("");
              }}
              className="rounded-lg border border-gray-300 px-4 py-2 text-gray-700 hover:bg-gray-100"
            >
              Cancel
            </button>
            <button
              onClick={handleCreate}
              disabled={docStore?.createDocLoading}
              className={`rounded-lg px-4 py-2 text-white ${
                docStore?.createDocLoading ? "bg-slate-400 hover:bg-slate-600" : "hover:bg-blue-700 bg-blue-600"
              }`}
            >
              {docStore?.createDocLoading ? "Processing..." : "Create"}
            </button>
          </div>
        </DialogPanel>
      </div>
    </Dialog>
  );
};

export default CreateDoc;
