import React, { useEffect, useId, useMemo, useRef } from "react";
import styles from "./BaseDialog.module.css";

export type DialogSize = "sm" | "md" | "lg" | "xl";

type Props = {
  open: boolean;
  onClose: () => void;

  title?: React.ReactNode;
  description?: React.ReactNode;

  children: React.ReactNode;

  size?: DialogSize;
  closeOnBackdrop?: boolean;
  closeOnEsc?: boolean;
  showCloseButton?: boolean;

  /** Optioneel: knoppen onderin (bijv. Opslaan/Annuleren) */
  footer?: React.ReactNode;

  /** Voor forms: zet true als je de dialog niet wil sluiten tijdens submit/loading */
  disableClose?: boolean;

  /** Extra className hooks */
  className?: string;
};

const FOCUSABLE = [
  "a[href]",
  "button:not([disabled])",
  "textarea:not([disabled])",
  "input:not([disabled])",
  "select:not([disabled])",
  "[tabindex]:not([tabindex='-1'])",
].join(",");

function getFocusable(container: HTMLElement | null) {
  if (!container) return [];
  return Array.from(container.querySelectorAll<HTMLElement>(FOCUSABLE)).filter(
    (el) => !el.hasAttribute("disabled") && !el.getAttribute("aria-hidden")
  );
}

export default function BaseDialog({
  open,
  onClose,
  title,
  description,
  children,
  size = "md",
  closeOnBackdrop = true,
  closeOnEsc = true,
  showCloseButton = true,
  footer,
  disableClose = false,
  className,
}: Props) {
  const titleId = useId();
  const descId = useId();
  const panelRef = useRef<HTMLDivElement | null>(null);

  const canClose = useMemo(() => !disableClose, [disableClose]);

  // ESC + focus trap
  useEffect(() => {
    if (!open) return;

    const panel = panelRef.current;

    // focus initial
    const focusables = getFocusable(panel);
    (focusables[0] ?? panel)?.focus?.();

    const onKeyDown = (e: KeyboardEvent) => {
      if (!open) return;

      if (e.key === "Escape" && closeOnEsc && canClose) {
        e.preventDefault();
        onClose();
        return;
      }

      if (e.key === "Tab") {
        const items = getFocusable(panel);
        if (items.length === 0) {
          e.preventDefault();
          panel?.focus?.();
          return;
        }
        const first = items[0];
        const last = items[items.length - 1];
        const active = document.activeElement as HTMLElement | null;

        if (e.shiftKey) {
          if (!active || active === first) {
            e.preventDefault();
            last.focus();
          }
        } else {
          if (active === last) {
            e.preventDefault();
            first.focus();
          }
        }
      }
    };

    document.addEventListener("keydown", onKeyDown);
    return () => document.removeEventListener("keydown", onKeyDown);
  }, [open, closeOnEsc, canClose, onClose]);

  // scroll lock
  useEffect(() => {
    if (!open) return;
    const prev = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = prev;
    };
  }, [open]);

  if (!open) return null;

  return (
    <div
      className={styles.backdrop}
      role="presentation"
      onMouseDown={(e) => {
        // sluit alleen als je echt op de backdrop klikt (niet binnen panel)
        if (!closeOnBackdrop || !canClose) return;
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div
        ref={panelRef}
        className={[
          styles.panel,
          styles[`size_${size}`],
          className ?? "",
        ].join(" ")}
        role="dialog"
        aria-modal="true"
        aria-labelledby={title ? titleId : undefined}
        aria-describedby={description ? descId : undefined}
        tabIndex={-1}
      >
        {(title || showCloseButton) && (
          <div className={styles.header}>
            <div className={styles.headerText}>
              {title && (
                <h2 id={titleId} className={styles.title}>
                  {title}
                </h2>
              )}
              {description && (
                <p id={descId} className={styles.description}>
                  {description}
                </p>
              )}
            </div>

            {showCloseButton && (
              <button
                type="button"
                className={styles.close}
                aria-label="Sluiten"
                onClick={() => {
                  if (canClose) onClose();
                }}
                disabled={!canClose}
              >
                ✕
              </button>
            )}
          </div>
        )}

        <div className={styles.body}>{children}</div>

        {footer && <div className={styles.footer}>{footer}</div>}
      </div>
    </div>
  );
}
