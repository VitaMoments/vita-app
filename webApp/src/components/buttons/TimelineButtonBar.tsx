import React from "react";
import { TimelineButton } from "./TimelineButton";
import styles from "./Button.module.css";

type TimelineButtonBarProps = {
  activeIndex: number;
  onChange: (index: number) => void | Promise<void>;
  labels: [string, string, string, string];
  className?: string;
};

export function TimelineButtonBar({
  activeIndex,
  onChange,
  labels,
  className,
}: TimelineButtonBarProps) {
  return (
    <div className={`${styles.bar} ${className ?? ""}`}>
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
