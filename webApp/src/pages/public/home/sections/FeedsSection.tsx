import React from "react";
import styles from "./FeedsSection.module.css";
import { useNavigate } from "react-router-dom";

import { Button } from "../../../../components/buttons/Button";

type Stat = {
  label: string;
  value: string;
  icon: "users" | "flame" | "heart" | "chat";
};

type FeedCategory = "Beweging" | "Voeding" | "Welzijn";

type FeedPost = {
  id: string;
  initials: string;
  name: string;
  timeAgo: string;
  category: FeedCategory;
  text: string;
  likes: number;
  comments: number;
  shares: number;
};

const stats: Stat[] = [
  { value: "12.4K", label: "Leden", icon: "users" },
  { value: "847", label: "Posts vandaag", icon: "flame" },
  { value: "3.2K", label: "Likes vandaag", icon: "heart" },
  { value: "1.1K", label: "Reacties", icon: "chat" },
];

const posts: FeedPost[] = [
  {
    id: "1",
    initials: "S",
    name: "Sophie V.",
    timeAgo: "2 uur geleden",
    category: "Beweging",
    text: "Net mijn eerste 10km hardgelopen! 🏃‍♀️ Dankzij de tips van deze community ben ik in 8 weken van 3km naar 10km gegaan.",
    likes: 47,
    comments: 12,
    shares: 5,
  },
  {
    id: "2",
    initials: "J",
    name: "Jasper K.",
    timeAgo: "5 uur geleden",
    category: "Voeding",
    text: "Mijn meal prep voor deze week 🥗 Alles plantaardig en onder 30 minuten per maaltijd. Swipe voor de recepten!",
    likes: 89,
    comments: 23,
    shares: 31,
  },
  {
    id: "3",
    initials: "L",
    name: "Lotte M.",
    timeAgo: "gisteren",
    category: "Welzijn",
    text: "Eindelijk 8 uur slaap dankzij mijn avondroutine. Geen schermen na 21:00 + 10 min ademhalingsoefeningen. Wie doet mee? 💤",
    likes: 134,
    comments: 45,
    shares: 18,
  },
];

function iconFor(stat: Stat["icon"]) {
  switch (stat) {
    case "users":
      return "👥";
    case "flame":
      return "🔥";
    case "heart":
      return "♡";
    case "chat":
      return "💬";
  }
}

const FeedsSection: React.FC = () => {
  const navigate = useNavigate();
  return (
    <section className={styles.section}>
      <div className={styles.container}>
        {/* Foto 1: titel blok */}
        <header className={styles.header}>
          <div className={styles.kicker}>SOCIAL FEED</div>
          <h2 className={styles.title}>Wat deelt de community?</h2>
          <p className={styles.subtitle}>
            Bekijk wat anderen delen, laat je inspireren en post jouw eigen
            gezonde momenten.
          </p>

          <div className={styles.statsRow}>
            {stats.map((s) => (
              <div key={s.label} className={styles.statCard}>
                <div className={styles.statIcon} aria-hidden="true">
                  {iconFor(s.icon)}
                </div>
                <div className={styles.statText}>
                  <div className={styles.statValue}>{s.value}</div>
                  <div className={styles.statLabel}>{s.label}</div>
                </div>
              </div>
            ))}
          </div>
        </header>

        {/* Foto 2: posts + button blok */}
        <div className={styles.feedWrap}>
          <div className={styles.posts}>
            {posts.map((p) => (
              <article key={p.id} className={styles.post}>
                <div className={styles.postTop}>
                  <div className={styles.user}>
                    <div className={styles.avatar}>{p.initials}</div>
                    <div className={styles.userMeta}>
                      <div className={styles.userName}>{p.name}</div>
                      <div className={styles.userTime}>{p.timeAgo}</div>
                    </div>
                  </div>

                  <span className={styles.badge}>{p.category}</span>
                </div>

                <p className={styles.postText}>{p.text}</p>

                <div className={styles.postActions}>
                  <div className={styles.action}>
                    <span className={styles.actionIcon} aria-hidden="true">
                      ♡
                    </span>
                    {p.likes}
                  </div>
                  <div className={styles.action}>
                    <span className={styles.actionIcon} aria-hidden="true">
                      💬
                    </span>
                    {p.comments}
                  </div>
                  <div className={styles.action}>
                    <span className={styles.actionIcon} aria-hidden="true">
                      ↗
                    </span>
                    {p.shares}
                  </div>
                </div>
              </article>
            ))}
          </div>

          <div className={styles.ctaRow}>
            <Button
              className={styles.primary}
              onClick={() => navigate("/registration")}
            >
              Maak je profiel <span className={styles.arrow}>→</span>
            </Button>
          </div>
        </div>
      </div>
    </section>
  );
};

export default FeedsSection;
