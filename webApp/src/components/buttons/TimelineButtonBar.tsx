import React from "react";
import { TimelineButton } from "./TimelineButton";
import styles from "./Button.module.css";

export type TimelineButtonBarProps = {
  activeIndex: number;
  labels: readonly string[];
  onChange: (index: number) => void;
  className?: string;
};

export function TimelineButtonBar({
  activeIndex,
  onChange,
  labels,
  className,
}: TimelineButtonBarProps) {
  return (
    <div className={`${styles.bar} ${className ?? ""}`.trim()}>
      {labels.map((label, index) => (
        <TimelineButton
          key={index}
          index={index}
          activeIndex={activeIndex}
          onClick={() => onChange(index)}
        >
          {label}
        </TimelineButton>
      ))}
    </div>
  );
}
