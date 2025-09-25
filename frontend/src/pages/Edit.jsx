import React, { useState, useEffect, useRef } from "react";
import ReactQuill, { Quill } from "react-quill";
import "react-quill/dist/quill.snow.css";
import Delta from "quill-delta";
import QuillCursors from "quill-cursors";
import { useParams } from "react-router-dom";
import { Client } from "@stomp/stompjs";

Quill.register("modules/cursors", QuillCursors);

// CRDT Item
class Item {
  constructor(id, left, right, content, isDeleted = false, isBold = false, isItalic = false) {
    this.id = id;
    this.left = left; // id of left item
    this.right = right; // id of right item
    this.content = content;
    this.isDeleted = isDeleted;
    this.isBold = isBold;
    this.isItalic = isItalic;
  }
}

export default function Edit() {
  const quillRef = useRef(null);
  const { docId } = useParams();
  const username = localStorage.getItem("displayName");
  const token = localStorage.getItem("accessToken");

  const [stompClient, setStompClient] = useState(null);
  const [connected, setConnected] = useState(false);
  const [crdtMap, setCrdtMap] = useState({});
  const [firstItem, setFirstItem] = useState(null);
  const [counter, setCounter] = useState(0);

  /** Initialize WebSocket STOMP connection */
  useEffect(() => {
    if (!token) return;

    const client = new Client({
      brokerURL: `ws://localhost:8080/docs/ws`,
      reconnectDelay: 5000,
      connectHeaders: { Authorization: `Bearer ${token}` },
      onConnect: () => {
        console.log("WebSocket connected!");
        setConnected(true);

        // CRDT changes
        client.subscribe(`/docs/broadcast/changes/${docId}`, (msg) => {
          const incoming = JSON.parse(msg.body);
          handleIncomingChange(incoming);
        });
      },
      onStompError: (err) => console.error("STOMP error:", err),
    });

    client.activate();
    setStompClient(client);

    return () => client.deactivate();
  }, [docId, token]);

  /** Insert item into linked list CRDT */
  const insertItem = (itm) => {
    const map = { ...crdtMap };
    map[itm.id] = itm;

    if (!itm.left) {
      // Insert at beginning
      if (firstItem) {
        itm.right = firstItem.id;
        firstItem.left = itm.id;
      }
      setFirstItem(itm);
    } else {
      const leftItem = map[itm.left];
      if (!leftItem) return; // left not arrived yet, skip
      const rightId = leftItem.right;
      itm.left = leftItem.id;
      itm.right = rightId;
      leftItem.right = itm.id;
      if (rightId && map[rightId]) map[rightId].left = itm.id;
    }

    setCrdtMap(map);
  };

  /** Render the CRDT linked list in Quill */
  const renderQuill = () => {
    const quill = quillRef.current.getEditor();
    let delta = new Delta();
    let current = firstItem;

    while (current) {
      if (!current.isDeleted) {
        delta.insert(current.content, { bold: current.isBold, italic: current.isItalic });
      }
      current = current.right ? crdtMap[current.right] : null;
    }

    quill.setContents(delta, "silent");
  };

  /** Handle incoming CRDT changes */
  const handleIncomingChange = (incoming) => {
    if (!incoming || incoming.id?.split("@")[1] === username) return;

    if (incoming.operation === "insert") {
      const itm = new Item(incoming.id, incoming.left, incoming.right, incoming.content, incoming.isDeleted, incoming.isBold, incoming.isItalic);
      insertItem(itm);
    } else if (incoming.operation === "delete") {
      if (!crdtMap[incoming.id]) return;
      crdtMap[incoming.id].isDeleted = true;
    } else if (incoming.operation === "format") {
      if (!crdtMap[incoming.id]) return;
      crdtMap[incoming.id].isBold = incoming.isBold;
      crdtMap[incoming.id].isItalic = incoming.isItalic;
    }

    renderQuill();
  };

  /** Handle local changes */
  const handleLocalChange = (content, delta, source, editor) => {
    if (!stompClient || !connected || source !== "user") return;

    const lastOp = delta.ops[delta.ops.length - 1];
    const quill = quillRef.current.getEditor();

    if ("insert" in lastOp) {
      const text = lastOp.insert;
      for (let i = 0; i < text.length; i++) {
        const char = text[i];
        const id = counter + "@" + username;
        const leftId = firstItem ? firstItem.id : null;

        const itm = new Item(id, leftId, null, char, false, lastOp.attributes?.bold ?? false, lastOp.attributes?.italic ?? false);
        insertItem(itm);

        // Publish each character
        stompClient.publish({
          destination: `/docs/change/${docId}`,
          body: JSON.stringify({ ...itm, operation: "insert" }),
        });

        setCounter((prev) => prev + 1);
      }
    } else if ("delete" in lastOp) {
      // TODO: implement character-wise deletion similar to backend
    }

    renderQuill();
  };

  return (
    <div className="bg-[#f1f3f4] flex justify-center p-4 min-h-screen">
      <div className="w-10/12 lg:w-8/12 text-black bg-white">
        <div id="toolbar" className="flex justify-center ">
          <button className="ql-bold" />
          <button className="ql-italic" />
        </div>
        <ReactQuill ref={quillRef} onChange={handleLocalChange} modules={{ toolbar: ["bold", "italic"] }} theme="snow" />
      </div>
    </div>
  );
}
