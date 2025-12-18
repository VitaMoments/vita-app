import React from "react";
import styles from "./Button.module.css";

type TimelineButtonProps = React.ButtonHTMLAttributes<HTMLButtonElement> & {
  index: number;
  activeIndex: number;
  children: React.ReactNode;
};

export function TimelineButton({
  index,
  activeIndex,
  className,
  children,
  ...rest
}: TimelineButtonProps) {
  const isSelected = index === activeIndex;

  return (
    <button
      className={[
        styles.button,
        isSelected ? styles.selected : styles.unselected,
        className ?? "",
      ].join(" ")}
      {...rest}
    >
      {children}
    </button>
  );
}
