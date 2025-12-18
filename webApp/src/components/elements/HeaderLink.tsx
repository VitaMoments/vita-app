import React from "react";
import { Link } from "react-router-dom";
import styles from "./Header.module.css";

export default function HeaderLink({ to, icon: Icon, text, onClick, className }) {
  return (
    <Link
      to={to}
      className={`${styles.link} ${className ?? ""}`.trim()}
      onClick={onClick}
    >
      {Icon ? (
        <span className={styles.linkIcon} aria-hidden="true">
          <Icon />
        </span>
      ) : null}
    </Link>
  );
}