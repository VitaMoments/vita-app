import React from "react"
import styles from "./Button.module.css"

type ButtonProps = React.ButtonHTMLAttributes<HTMLButtonElement> & {
  children: React.ReactNode;
};

export function Button({ children, className, ...rest }: ButtonProps) {
  return (
    <button
      className={`${styles.button} ${className ?? ""}`}
      {...rest}
    >
      {children}
    </button>
  );
}