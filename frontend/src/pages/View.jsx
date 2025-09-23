import { Listbox, ListboxButton, ListboxOption, ListboxOptions } from "@headlessui/react";
import { useEffect, useState } from "react";
import plus from "../assets/plus.png";
import DocumentBar from "../components/view/DocumentBar";
import CreateDoc from "../components/view/CreateDoc";
import { useDispatch, useSelector } from "react-redux";
import { getAllDocs } from "../Redux/Document/Action";
import { toast } from "sonner";

const View = () => {
  const options = ["Owned by me", "Shared with me", "All documents"];
  const [selectedOption, setSelectedOption] = useState(options[2]);
  const displayName = localStorage.getItem("displayName");
  const [showCreateDoc, setShowCreateDoc] = useState(false);
  const dispatch = useDispatch();
  const docStore = useSelector((store) => store.docStore);

  useEffect(() => {
    dispatch(getAllDocs()).then(() => {
      toast.success("Fetched all documents!");
    });
  }, [dispatch]);

  const filteredDocs = docStore?.allDocs?.filter((doc) => {
    if (selectedOption === options[0]) return doc.owner === displayName;
    if (selectedOption === options[1]) return doc.sharedWith.some((shared) => shared.username === displayName);
    return true; // "All documents"
  });

  return (
    <div className="min-h-screen bg-[#ecf0f2]">
      {/* title of the view page and doc seeing option */}
      <div className="w-[70%] mx-auto py-10 pb-6 flex flex-row justify-between items-center">
        <p className="text-4xl text-slate-600 font-semibold">Recent Documents</p>
        <div className="">
          <Listbox value={selectedOption} onChange={setSelectedOption}>
            <ListboxButton className="bg-white w-48 py-3 rounded-lg shadow-md text-[#5f6368] flex flex-row items-center justify-between px-5 border-1 active:bg-slate-50">
              <p>{selectedOption}</p>
              <p>▼</p>
            </ListboxButton>
            <ListboxOptions anchor="bottom" className="text-[#484a4e] absolute z-10 mt-2 w-48 bg-white rounded-md shadow-md text-md">
              {options.map((option) => (
                <ListboxOption key={option} value={option} className="cursor-pointer p-2 px-5 hover:bg-gray-200">
                  {option}
                </ListboxOption>
              ))}
            </ListboxOptions>
          </Listbox>
        </div>
      </div>

      {/* if no docs found */}
      {filteredDocs?.length === 0 && (
        <div className="w-10/12 mx-auto px-8 mt-4 py-4 text-[#5f6368] bg-white rounded-md shadow-md text-lg text-center font-semibold">
          You don't have any document. Create a new document!
        </div>
      )}

      {/* list of documens of person */}
      {filteredDocs?.length !== 0 && (
        <div className="w-10/12 mx-auto px-8 py-3 pb-1 flex flex-row items-center justify-between text-[#5f6368] ">
          <h1 className="basis-7/12 text-xl font-semibold truncate">Title</h1>
          <p className="basis-2/12 truncate">Owner</p>
          <p className="basis-2/12 truncate">Shared with</p>
          <div className="basis-1/12"></div>
        </div>
      )}

      {/* actual documents details here */}
      {docStore?.allDocs?.map((doc, index) => {
        if (selectedOption === options[0] && doc.owner !== displayName) return null;
        else if (selectedOption === options[1] && !doc.sharedWith.some((shared) => shared.username === displayName)) return null;
        else return <DocumentBar key={index} index={index} doc={doc} />;
      })}

      {/* create doc dialog option */}
      <CreateDoc isOpen={showCreateDoc} setIsOpen={setShowCreateDoc} />
      {/* create more document button */}
      <button
        className="fixed right-8 bottom-8 flex justify-center gap-2 items-center bg-white overflow-hidden rounded-2xl px-4 shadow-lg shadow-black/25 hover:shadow-lg hover:shadow-black/50 transition-shadow duration-300"
        onClick={() => setShowCreateDoc(true)}
      >
        <p className="bg-white">Create new Doc</p>
        <img className="" src={plus} alt="add doc image" width={50} height={50} />
      </button>
    </div>
  );
};

export default View;
