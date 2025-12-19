import React from "react";
import styles from "./Tabs.module.css";

export type TabItem = {
  value: string;
  label: string;
  icon?: React.ReactNode;
  content: React.ReactNode;
  disabled?: boolean;
};

type TabsProps = {
  tabs: TabItem[];
  defaultValue?: string;
  value?: string; // controlled (optional)
  onChange?: (value: string) => void;
  ariaLabel?: string;
  mobileLabel?: string;
};

const Tabs: React.FC<TabsProps> = ({
  tabs,
  defaultValue,
  value,
  onChange,
  ariaLabel = "Tabs",
  mobileLabel = "Sectie",
}) => {
  const isControlled = value !== undefined;
  const first = tabs[0]?.value ?? "";
  const [internal, setInternal] = React.useState<string>(defaultValue ?? first);

  const active = isControlled ? (value as string) : internal;

  const setActive = (next: string) => {
    if (!isControlled) setInternal(next);
    onChange?.(next);
  };

  const selectId = React.useId();
  const activeIndex = Math.max(0, tabs.findIndex((t) => t.value === active));

  return (
    <section className={styles.tabs}>
      {/* Mobile dropdown */}
      <div className={styles.mobileSelectWrap}>
        <label className={styles.mobileLabel} htmlFor={selectId}>
          {mobileLabel}
        </label>

        <select
          id={selectId}
          className={styles.mobileSelect}
          value={active}
          onChange={(e) => setActive(e.target.value)}
        >
          {tabs.map((t) => (
            <option key={t.value} value={t.value} disabled={t.disabled}>
              {t.label}
            </option>
          ))}
        </select>
      </div>

      {/* Desktop tabs */}
      <div className={styles.tabList} role="tablist" aria-label={ariaLabel}>
        {tabs.map((t) => {
          const isActive = t.value === active;

          return (
            <button
              key={t.value}
              type="button"
              className={`${styles.tab} ${isActive ? styles.activeTab : ""}`}
              role="tab"
              aria-selected={isActive}
              aria-controls={`panel-${t.value}`}
              tabIndex={isActive ? 0 : -1}
              disabled={t.disabled}
              onClick={() => setActive(t.value)}
            >
              {t.icon && <span className={styles.tabIcon}>{t.icon}</span>}
              <span className={styles.tabLabel}>{t.label}</span>
            </button>
          );
        })}
      </div>

      {/* Optional indicator (only really useful for desktop) */}
      <div
        className={styles.indicator}
        style={{
          width: `${100 / Math.max(1, tabs.length)}%`,
          transform: `translateX(${activeIndex * 100}%)`,
        }}
        aria-hidden="true"
      />

      {/* Panels */}
      <div className={styles.panels}>
        {tabs.map((t) => (
          <div
            key={t.value}
            id={`panel-${t.value}`}
            role="tabpanel"
            hidden={t.value !== active}
            className={styles.panel}
          >
            {t.content}
          </div>
        ))}
      </div>
    </section>
  );
};

export default Tabs;
