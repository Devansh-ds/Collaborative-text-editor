import { Description, Dialog, DialogPanel, DialogTitle } from "@headlessui/react";
import { useDispatch, useSelector } from "react-redux";
import { deleteDoc, getAllDocs } from "../../Redux/Document/Action";

const Delete = ({ isOpen, setIsOpen, doc }) => {
  const dispatch = useDispatch();
  const docStore = useSelector((store) => store.docStore);

  const handleSubmit = () => {
    dispatch(deleteDoc(doc.id))
      .then(() => {
        dispatch(getAllDocs());
        setIsOpen(false);
      })
      .catch((err) => {
        console.log("Fucked delete doc: ", err);
      });
  };

  return (
    <Dialog open={isOpen} onClose={() => setIsOpen(false)} className="relative z-50">
      {/* Overlay */}
      <div className="fixed inset-0 bg-black/50" aria-hidden="true" />

      {/* Panel wrapper */}
      <div className="fixed inset-0 flex w-screen items-center justify-center p-4">
        <DialogPanel className="w-full max-w-lg space-y-4 rounded-2xl bg-white p-8 shadow-lg">
          <DialogTitle className="text-xl font-bold text-gray-800">{`Delete '${doc.title}'`}</DialogTitle>
          <Description className="text-gray-600">This will permanently remove your document.</Description>
          <p className="text-gray-600">Are you sure you want to delete this document? This action cannot be undone.</p>
          <div className="flex justify-end gap-4 pt-4">
            <button onClick={() => setIsOpen(false)} className="rounded-lg border border-gray-300 px-4 py-2 text-gray-700 hover:bg-gray-100">
              Cancel
            </button>
            <button
              onClick={handleSubmit}
              disabled={docStore?.deleteDocLoading}
              className={`rounded-lg px-4 py-2 text-white  ${
                docStore?.deleteDocLoading ? "bg-slate-400 hover:bg-slate-600" : "hover:bg-red-700 bg-red-600"
              }`}
            >
              {docStore?.deleteDocLoading ? "Processing..." : "Delete"}
            </button>
          </div>
        </DialogPanel>
      </div>
    </Dialog>
  );
};

export default Delete;
