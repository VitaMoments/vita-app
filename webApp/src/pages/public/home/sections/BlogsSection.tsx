import React from "react";
import styles from "./BlogsSection.module.css";
import { useNavigate } from "react-router-dom";

import { Button } from "../../../../components/buttons/Button"

type BlogCategory = "Voeding" | "Welzijn" | "Mentaal";

type BlogCard = {
  id: string;
  category: BlogCategory;
  title: string;
  excerpt: string;
  author: string;
  readMinutes: number;
};

const blogs: BlogCard[] = [
  {
    id: "1",
    category: "Voeding",
    title: "De kracht van intermittent fasting",
    excerpt:
      "Ontdek hoe periodiek vasten je energie en focus kan verbeteren en waarom steeds meer mensen het proberen.",
    author: "Dr. Eva Jansen",
    readMinutes: 5,
  },
  {
    id: "2",
    category: "Welzijn",
    title: "Slaap: het geheime wapen voor herstel",
    excerpt:
      "Waarom 7-9 uur slaap cruciaal is voor je immuunsysteem, mentale gezondheid en sportprestaties.",
    author: "Mark de Vries",
    readMinutes: 4,
  },
  {
    id: "3",
    category: "Mentaal",
    title: "Mindfulness in je dagelijkse routine",
    excerpt:
      "Drie simpele oefeningen die je vandaag nog kunt toepassen om stress te verminderen en bewuster te leven.",
    author: "Lisa Bakker",
    readMinutes: 3,
  },
];

const BlogsSection: React.FC = () => {
    const navigate = useNavigate();

  return (
    <section className={styles.section}>
      <div className={styles.container}>

       <header className={styles.header}>
                <h2 className={styles.title}>Gezondheidsblogs</h2>
                <p className={styles.subtitle}>
                 Lees de nieuwste inzichten over voeding, beweging en mentale gezondheid.
                </p>
              </header>

        <div className={styles.grid}>
          {blogs.map((b) => (
            <article key={b.id} className={styles.card}>
              <div className={styles.cardTop}>
                <span className={styles.badge}>{b.category}</span>
              </div>

              <h3 className={styles.cardTitle}>{b.title}</h3>
              <p className={styles.excerpt}>{b.excerpt}</p>

              <div className={styles.metaRow}>
                <div className={styles.metaItem}>
                  <span className={styles.metaIcon} aria-hidden="true">
                    👤
                  </span>
                  <span>{b.author}</span>
                </div>

                <div className={styles.metaItem}>
                  <span className={styles.metaIcon} aria-hidden="true">
                    🕒
                  </span>
                  <span>{b.readMinutes} min</span>
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

export default BlogsSection;