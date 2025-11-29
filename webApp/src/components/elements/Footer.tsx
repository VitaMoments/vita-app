import styles from "./Footer.module.css";

const Footer = () => {
  return (
    <footer className={styles.footer}>
      <div className={styles.footerInner}>
        <div className={styles.footerLeft}>
          <span className={styles.footerBrand}>HealtyProduct</span>
          <span>© {new Date().getFullYear()} • Preventie & vitaliteit</span>
        </div>
        <div className={styles.footerRight}>
          <a href="/privacy" className={styles.link}>Privacy</a>
          <a href="/terms" className={styles.link}>Voorwaarden</a>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
