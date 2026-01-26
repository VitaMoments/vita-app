import React from "react";
import { Link } from "react-router-dom";
import type { IconType } from "react-icons";
import styles from "./HeaderLink.module.css";

type HeaderLinkProps = {
  to: string;
  icon?: IconType;
  text: string;
  onClick?: () => void;
  className?: string; // ✅ optional
};

export default function HeaderLink({
  to,
  icon: Icon,
  text,
  onClick,
  className,
}: HeaderLinkProps) {
  return (
    <Link
      to={to}
      className={`${styles.link} ${className ?? ""}`.trim()}
      onClick={onClick}
      aria-label={text}
      title={text}
    >
      {Icon ? (
        <span className={styles.linkIcon} aria-hidden="true">
          <Icon />
        </span>
      ) : null}
      <span className={styles.linkText}>{text}</span>
    </Link>
  );
}
