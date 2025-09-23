import { Dialog, DialogPanel, DialogTitle, Description } from "@headlessui/react";
import React, { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { getAllDocs, renaemDoc } from "../../Redux/Document/Action";

const Rename = ({ isOpen, setIsOpen, doc }) => {
  const [newName, setNewName] = useState(doc?.title);
  const dispatch = useDispatch();
  const { renameDocLoading } = useSelector((store) => store.docStore);

  const handleRename = () => {
    if (newName.trim() === "") return;
    dispatch(renaemDoc({ docId: doc.id, reqData: { title: newName } }))
      .then(() => {
        dispatch(getAllDocs());
        setIsOpen(false);
      })
      .catch((err) => {
        console.log("fuck rename err: ", err);
      });
  };

  return (
    <Dialog open={isOpen} onClose={() => setIsOpen(false)} className="relative z-50">
      {/* Overlay */}
      <div className="fixed inset-0 bg-black/50" aria-hidden="true" />

      {/* Panel */}
      <div className="fixed inset-0 flex items-center justify-center p-4">
        <DialogPanel className="w-full max-w-lg rounded-2xl bg-white p-8 shadow-lg space-y-4">
          <DialogTitle className="text-xl font-bold text-gray-800">Rename Document</DialogTitle>
          <Description className="text-gray-600">Enter a new name for your document.</Description>
          <input
            type="text"
            value={newName}
            onChange={(e) => setNewName(e.target.value)}
            className="w-full rounded-lg border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <div className="flex justify-end gap-4 pt-4">
            <button onClick={() => setIsOpen(false)} className="rounded-lg border border-gray-300 px-4 py-2 text-gray-700 hover:bg-gray-100">
              Cancel
            </button>
            <button onClick={handleRename} className={`rounded-lg px-4 py-2 text-white ${renameDocLoading? "bg-slate-400 hover:bg-slate-600" : "hover:bg-blue-700 bg-blue-600"}`}>
              {renameDocLoading? "Processing..." : "Save"}
            </button>
          </div>
        </DialogPanel>
      </div>
    </Dialog>
  );
};

export default Rename;
