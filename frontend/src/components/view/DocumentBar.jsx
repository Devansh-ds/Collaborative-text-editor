import { Menu, MenuButton, MenuItem, MenuItems } from "@headlessui/react";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import DeleteIcon from "@mui/icons-material/Delete";
import DriveFileRenameOutlineIcon from "@mui/icons-material/DriveFileRenameOutline";
import DoNotTouchIcon from "@mui/icons-material/DoNotTouch";
import VisibilityIcon from "@mui/icons-material/Visibility";
import ShareIcon from "@mui/icons-material/Share";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import Delete from "./Delete";
import Rename from "./Rename";
import Share from "./Share";

const DocumentBar = ({ index, doc }) => {
  const navigate = useNavigate();
  const [showDelete, setShowDelete] = useState(false);
  const [showRename, setShowRename] = useState(false);
  const [showShare, setShowShare] = useState(false);
  const displayName = localStorage.getItem("displayName");

  return (
    <div
      key={index}
      className="w-10/12 mx-auto px-8 mt-4 py-4 flex flex-row items-center justify-between text-[#5f6368] bg-white rounded-md shadow-md hover:border-blue-400 cursor-pointer relative hover:z-50"
      onClick={(e) => {
        // if click is inside a button or menu/dialog, stop navigation
        if (e.target.closest("button") || e.target.closest("[role='menu']")) {
          e.stopPropagation();
        } else {
          navigate(`/edit/${doc.id}`, { state: doc.title });
        }
      }}
    >
      <h1 className="basis-7/12 text-xl font-semibold truncate">{doc.title}</h1>
      <p className="basis-2/12 truncate">{doc.owner}</p>
      <p className="basis-2/12 truncate">{doc.sharedWith.length}</p>

      {/* menu components start */}
      <Delete isOpen={showDelete} setIsOpen={setShowDelete} doc={doc} />
      <Rename isOpen={showRename} setIsOpen={setShowRename} doc={doc} />
      <Share isOpen={showShare} setIsOpen={setShowShare} doc={doc} />
      {/* menu components end */}
      <div className="basis-1/12 font-bold">
        <Menu as="div" className="relative inline-block text-left hover:z-50" onClick={(e) => e.stopPropagation()}>
          <MenuButton className="inline-flex items-center gap-2 bg-slate-200 rounded-full px-2 py-2 text-sm font-semibold text-black shadow-inner hover:bg-slate-300">
            <MoreVertIcon />
          </MenuButton>

          <MenuItems
            anchor="bottom end"
            className="absolute right-0 mt-2 w-52 origin-top-right rounded-xl border border-white/5 bg-white p-1 text-sm text-black shadow-md focus:outline-none
               data-[closed]:scale-95 data-[closed]:opacity-0 transition ease-out duration-100 shadow-gray-500 z-50"
          >
            {/* Owner */}
            {doc.owner === displayName && (
              <MenuItem>
                {({ active }) => (
                  <button
                    className={`${active ? "bg-white/10" : ""} group flex w-full items-center gap-2 rounded-lg px-3 py-1.5 hover:bg-slate-200`}
                    onClick={() => setShowDelete(true)}
                  >
                    <DeleteIcon className="size-4 fill-white/30" />
                    Delete
                  </button>
                )}
              </MenuItem>
            )}

            {/* Owner or Editor */}
            {(doc.owner === displayName || doc.sharedWith?.some((p) => p.username === displayName && p.permission === "EDIT")) && (
              <>
                <MenuItem>
                  {({ active }) => (
                    <button
                      className={`${active ? "bg-white/10" : ""} group flex w-full items-center gap-2 rounded-lg px-3 py-1.5 hover:bg-slate-200`}
                      onClick={() => setShowRename(true)}
                    >
                      <DriveFileRenameOutlineIcon className="size-4 fill-white/30" />
                      Rename
                    </button>
                  )}
                </MenuItem>
                <MenuItem>
                  {({ active }) => (
                    <button
                      className={`${active ? "bg-white/10" : ""} group flex w-full items-center gap-2 rounded-lg px-3 py-1.5 hover:bg-slate-200`}
                      onClick={() => setShowShare(true)}
                    >
                      <ShareIcon className="size-4 fill-white/30" />
                      Share
                    </button>
                  )}
                </MenuItem>
              </>
            )}

            {/* Viewer only */}
            {doc.sharedWith?.some((p) => p.username === displayName && p.permission === "VIEW") && doc.owner !== displayName && (
              <MenuItem disabled>
                {({ active }) => (
                  <div className={`${active ? "bg-white/10" : ""} group flex w-full items-center gap-2 rounded-lg px-3 py-1.5 text-gray-500`}>
                    <VisibilityIcon className="size-4 fill-white/30" />
                    You can only view this doc
                  </div>
                )}
              </MenuItem>
            )}

            {/* No access */}
            {doc.owner !== displayName && !doc.sharedWith?.some((p) => p.username === displayName) && (
              <MenuItem disabled>
                {({ active }) => (
                  <div className={`${active ? "bg-white/10" : ""} group flex w-full items-center gap-2 rounded-lg px-3 py-1.5 text-red-500`}>
                    <DoNotTouchIcon className="size-4 fill-white/30" />
                    You do not have access to this document
                  </div>
                )}
              </MenuItem>
            )}
          </MenuItems>
        </Menu>
      </div>
    </div>
  );
};

export default DocumentBar;
