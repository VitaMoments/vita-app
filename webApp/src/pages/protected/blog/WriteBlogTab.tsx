import React, { useCallback, useEffect, useMemo, useState } from "react";
import styles from "./BlogsPage.module.css";

import { useEditor, useEditorState, EditorContent } from "@tiptap/react";
import Document from "@tiptap/extension-document";
import Paragraph from "@tiptap/extension-paragraph";
import Text from "@tiptap/extension-text";
import Bold from "@tiptap/extension-bold";
import Italic from "@tiptap/extension-italic";
import Underline from "@tiptap/extension-underline";
import HardBreak from "@tiptap/extension-hard-break";
import History from "@tiptap/extension-history";
import Placeholder from "@tiptap/extension-placeholder";

type Props = {
  isActive: boolean;
};

const WriteBlogTab: React.FC<Props> = ({ isActive }) => {
    const [title, setTitle] = useState<string | null>(null)
    const [subtitle, setSubtitle] = useState<string | null>(null)

    return (
            <div> test </div>
        )
    }
export default WriteBlogTab;