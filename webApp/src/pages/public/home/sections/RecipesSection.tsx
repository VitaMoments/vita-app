import React from "react";
import styles from "./RecipesSection.module.css";
import { useNavigate } from "react-router-dom";

import { Button } from "../../../../components/buttons/Button"

type Recipe = {
  id: string;
  title: string;
  author: string;
  minutes: number;
  likes: number;
  imageUrl: string;
};

const recipes: Recipe[] = [
  {
    id: "1",
    title: "Quinoa Power Bowl",
    author: "Anna Smit",
    minutes: 20,
    likes: 234,
    imageUrl:
      "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=1200&q=80",
  },
  {
    id: "2",
    title: "Groene Smoothie Boost",
    author: "Pieter de Groot",
    minutes: 5,
    likes: 189,
    imageUrl:
      "https://images.unsplash.com/photo-1553530666-ba11a7da3888?auto=format&fit=crop&w=1200&q=80",
  },
  {
    id: "3",
    title: "Açaí Bowl met Granola",
    author: "Sarah Visser",
    minutes: 10,
    likes: 312,
    imageUrl:
      "https://images.unsplash.com/photo-1641579719214-534970165dc9?auto=format&fit=crop&fm=jpg&q=80&w=1200",
  },
];

const RecipesSection: React.FC = () => {
        const navigate = useNavigate();
  return (
    <section className={styles.section}>
      <div className={styles.container}>
        <header className={styles.header}>
          <h2 className={styles.title}>Gezonde recepten</h2>
          <p className={styles.subtitle}>
            Ontdek recepten gedeeld door onze community en deel jouw favorieten.
          </p>
        </header>

        <div className={styles.grid}>
          {recipes.map((r) => (
            <article key={r.id} className={styles.card}>
              <div className={styles.imageWrap}>
                <img className={styles.image} src={r.imageUrl} alt={r.title} />
                <div className={styles.likesBadge} aria-label={`${r.likes} likes`}>
                  <span className={styles.heart} aria-hidden="true">❤</span>
                  <span className={styles.likesText}>{r.likes}</span>
                </div>
              </div>

              <div className={styles.cardBody}>
                <h3 className={styles.cardTitle}>{r.title}</h3>
                <p className={styles.author}>door {r.author}</p>

                <div className={styles.meta}>
                  <span className={styles.metaItem}>
                    <span className={styles.metaIcon} aria-hidden="true">🕒</span>
                    {r.minutes} min
                  </span>
                </div>
              </div>
            </article>
          ))}
        </div>
        <div className={styles.ctaRow}>
          <Button className={styles.primary} onClick={()=> navigate("/registration")}>
            Maak je profiel <span className={styles.arrow}>→</span>
          </Button>
        </div>
      </div>
    </section>
  );
};

export default RecipesSection;