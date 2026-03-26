import React from "react";
import styles from "./ImageSection.module.css";
import heroImage from "../../../../assets/HomePage_ImageSection.png";
import { useNavigate } from "react-router-dom";

import { Button } from "../../../../components/buttons/Button";

const ImageSection: React.FC = () => {
  const navigate = useNavigate();
  return (
    <section
      className={styles.content}
      style={{ backgroundImage: `url(${heroImage})` }}
    >
      <div className={styles.overlay} />
      <div className={styles.inner}>
        <div className={styles.pill}>🌿 Jouw gezondheids-community</div>

        <h1 className={styles.title}>
          Deel jouw <span className={styles.brand}>VitaMoments.</span>
        </h1>

        <p className={styles.subtitle}>
          Het social platform voor gezondheid. Deel recepten, volg je
          wearable-data, lees blogs en inspireer anderen in een community die
          écht om welzijn geeft.
        </p>

        <div className={styles.actions}>
          <Button
            className={styles.primary}
            onClick={() => navigate("/registration")}
          >
            Maak je profiel <span className={styles.arrow}>→</span>
          </Button>
        </div>
      </div>
    </section>
  );
};

export default ImageSection;
