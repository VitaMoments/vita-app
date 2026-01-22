import React, { useMemo, useState } from "react";
import styles from "./BlogsPage.module.css";

import { CreateBlogInput } from "./CreateBlogInput"

const CreateBlogPage: React.FC = () => {
  return (
    <div className={styles.page}>
      <div className={styles.content}>
        <h1>Blogs</h1>
        <p>
            <CreateBlogInput />
        </p>
      </div>
    </div>
  );
};

export default CreateBlogPage;
