import React from "react";
import styles from "./Banner.module.css";

type BannerVariant = "error" | "warning" | "info";

type BannerProps = {
  message?: string | null;
  variant: BannerVariant;
  className?: string;
};

const variantClassMap: Record<BannerVariant, string> = {
  error: styles.error,
  warning: styles.warning,
  info: styles.info,
};

export const Banner: React.FC<BannerProps> = ({ message, variant, className }) => {
  if (!message) return null;

  return (
    <div className={`${styles.banner} ${variantClassMap[variant]}${className ? ` ${className}` : ""}`}>
      {message}
    </div>
  );
};

type WrapperProps = Omit<BannerProps, "variant">;

export const ErrorBanner: React.FC<WrapperProps> = (props) => (
  <Banner {...props} variant="error" />
);

export const WarningBanner: React.FC<WrapperProps> = (props) => (
  <Banner {...props} variant="warning" />
);

export const InfoBanner: React.FC<WrapperProps> = (props) => (
  <Banner {...props} variant="info" />
);
