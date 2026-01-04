import React, { forwardRef, useId, useState } from "react";
import styles from "./Input.module.css";
import { FiEye, FiEyeOff } from "react-icons/fi";

type InputProps = Omit<React.InputHTMLAttributes<HTMLInputElement>, "className"> & {
  label?: string;
  rightAdornment?: React.ReactNode;
  inputClassName?: string;
};

type PasswordInputProps = Omit<React.InputHTMLAttributes<HTMLInputElement>, "type" | "className"> & {
  label?: string;
  value: string;
};

const Input = forwardRef<HTMLInputElement, InputProps>(function Input(
  { label, id, name, rightAdornment, inputClassName, ...rest },
  ref
) {
  const reactId = useId();

  // Alleen id forceren als we een label moeten koppelen
  const inputId = id ?? (label ? (name ?? `input-${reactId}`) : undefined);

  return (
    <div className={styles.formGroup}>
      {label && inputId && <label htmlFor={inputId}>{label}</label>}

      <div className={styles.inputWrapper}>
        <input
          ref={ref}
          id={inputId}
          name={name}
          className={`${styles.input} ${inputClassName ?? ""}`.trim()}
          {...rest}
        />

        {rightAdornment ? (
          <div className={styles.rightAdornment}>{rightAdornment}</div>
        ) : null}
      </div>
    </div>
  );
});

export function PasswordInput({ label, value, ...rest }: PasswordInputProps) {
  const [visible, setVisible] = useState(false);
  const toggleLabel = visible ? "Verberg wachtwoord" : "Toon wachtwoord";
  const { type: _ignored, ...safeRest } = rest as any;

  return (
    <Input
      label={label}
      value={value}
      {...safeRest}
      type={visible ? "text" : "password"}
      rightAdornment={
          <button
              type="button"
              className={styles.iconButton}
              onClick={() => setVisible((v) => !v)}
              onMouseDown={(e) => e.preventDefault()}
              aria-label={toggleLabel}
              aria-pressed={visible}
              title={toggleLabel}
            >
              {visible ? <FiEyeOff /> : <FiEye />}
            </button>
      }
    />
  );
}

export default Input;