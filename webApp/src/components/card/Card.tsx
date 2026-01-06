import React from "react";
import styles from "./Card.module.css";

type CardProps = {
  title?: React.ReactNode;
  subtitle?: React.ReactNode;
  actions?: React.ReactNode;
  footer?: React.ReactNode;
  children?: React.ReactNode;
  className?: string;
};

export const Card: React.FC<DefaultCardProps> = ({
  title,
  subtitle,
  actions,
  footer,
  children,
  className,
}) => {
  return (
    <section className={[styles.card, className].filter(Boolean).join(" ")}>
      {(title || subtitle || actions) && (
        <header className={styles.header}>
          <div className={styles.headerText}>
            {title && <div className={styles.title}>{title}</div>}
            {subtitle && <div className={styles.subtitle}>{subtitle}</div>}
          </div>
          {actions && <div className={styles.actions}>{actions}</div>}
        </header>
      )}

      {children && <div className={styles.body}>{children}</div>}

      {footer && <footer className={styles.footer}>{footer}</footer>}
    </section>
  );
};
