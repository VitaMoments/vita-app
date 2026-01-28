import React from "react";
import styles from "./Tabs.module.css";

export type TabItem<T extends string = string> = {
  value: T;
  label: string;
  icon?: React.ReactNode;
  content: React.ReactNode;
  disabled?: boolean;
};

export type TabsProps<T extends string = string> = {
  tabs: TabItem<T>[];
  defaultValue?: T;
  value?: T; // controlled (optional)
  onChange?: (value: T) => void;
  ariaLabel?: string;
  mobileLabel?: string;
};

function Tabs<T extends string = string>({
  tabs,
  defaultValue,
  value,
  onChange,
  ariaLabel = "Tabs",
  mobileLabel = "Sectie",
}: TabsProps<T>) {
  const isControlled = value !== undefined;
  const first = tabs[0]?.value as T | undefined;

  const [internal, setInternal] = React.useState<T>(
    (defaultValue ?? first ?? ("" as T)) as T
  );

  const active = (isControlled ? value : internal) as T;

  const setActive = (next: T) => {
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
          onChange={(e) => setActive(e.target.value as T)}
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

      {/* Indicator */}
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
}

export default Tabs;