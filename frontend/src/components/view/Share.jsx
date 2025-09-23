import { Dialog, DialogPanel, DialogTitle, Description } from "@headlessui/react";
import React, { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { addUser, getAllDocs } from "../../Redux/Document/Action";

const Share = ({ isOpen, setIsOpen, doc }) => {
  const [username, setUsername] = useState("");
  const [permission, setPermission] = useState("VIEW"); // default permission
  const [error, setError] = useState("");
  const dispatch = useDispatch();
  const docStore = useSelector((store) => store.docStore);

  // const handleAddUser = () => {

  //   if (!username.trim()) {
  //     setError("Username cannot be empty.");
  //     return;
  //   }

  //   dispatch(
  //     addUser({
  //       docId: doc.id,
  //       reqData: {
  //         username: username,
  //         permission: permission,
  //       },
  //     })
  //   )
  //     .then(() => {
  //       dispatch(getAllDocs());
  //       if (docStore?.noUserError) {
  //         setError("User does not exist");
  //       } else {
  //         setIsOpen(false);
  //         setError("");
  //       }
  //     })
  //     .catch((err) => {
  //       console.log("Fuck add user: ", err);
  //     });
  // };
  const handleAddUser = async (e) => {
    e.preventDefault(); // prevent page refresh

    if (!username.trim()) {
      setError("Username cannot be empty.");
      return;
    }

    const result = await dispatch(
      addUser({
        docId: doc.id,
        reqData: { username, permission },
      })
    );

    if (result?.success) {
      setIsOpen(false); // close modal on success
      setError("");
      setUsername("");
      dispatch(getAllDocs());
    } else if (result?.error) {
      setError(result.error); // show inline error if needed
    }
  };

  return (
    <Dialog open={isOpen} onClose={() => setIsOpen(false)} className="relative z-50">
      {/* Overlay */}
      <div className="fixed inset-0 bg-black/50" aria-hidden="true" />

      {/* Panel */}
      <div className="fixed inset-0 flex items-center justify-center p-4">
        <DialogPanel className="w-full max-w-lg rounded-2xl bg-white p-8 shadow-lg space-y-4">
          <DialogTitle className="text-xl font-bold text-gray-800">{`Share '${doc.title}'`}</DialogTitle>
          <Description className="text-gray-600">Enter a username and select the permission level.</Description>

          {/* Username input */}
          <div className="flex flex-col">
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Enter username"
              className={`w-full rounded-lg border p-2 focus:outline-none focus:ring-2 ${
                error ? "border-red-500 focus:ring-red-500" : "border-gray-300 focus:ring-blue-500"
              }`}
            />
            {error && <p className="text-red-500 text-sm mt-1">{error}</p>}
          </div>

          {/* Permission selection */}
          <div className="flex flex-col">
            <label className="mb-1 font-semibold text-gray-700">Select Permission</label>
            <select
              value={permission}
              onChange={(e) => setPermission(e.target.value)}
              className="w-full rounded-lg border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="VIEW">VIEW</option>
              <option value="EDIT">EDIT</option>
            </select>
          </div>

          {/* Buttons */}
          <div className="flex justify-end gap-4 pt-4">
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
              onClick={handleAddUser}
              className={`rounded-lg px-4 py-2 text-white ${
                docStore?.addUserLoading ? "bg-slate-400 hover:bg-slate-600" : "hover:bg-blue-700 bg-blue-600"
              }`}
            >
              {docStore?.addUserLoading ? "Processing..." : "Add User"}
            </button>
          </div>
        </DialogPanel>
      </div>
    </Dialog>
  );
};

export default Share;
