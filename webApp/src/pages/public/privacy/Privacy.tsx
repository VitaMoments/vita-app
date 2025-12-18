import React from "react";
import appStyles from "../../../App.module.css";

const Privacy: React.FC = () => {
  return (
      <>
        <section className={appStyles.section}>
            <h1 className={appStyles.title}>
              Gezonde medewerkers, gezonde bedrijven
            </h1>
            <p className={appStyles.subtitle}>
              Met onze preventieve health-checks en challenges helpen we bedrijven
              om hun mensen duurzaam gezond en vitaal te houden.
            </p>
          </section>

          <section className={appStyles.sectionHighlight}>
            <h2 className={appStyles.title}>
              Wat is onze Health APK?
            </h2>
            <p className={appStyles.subtitle}>
              Een laagdrempelige scan van leefstijl, werkbeleving en vitaliteit.
              Medewerkers krijgen inzicht, jij krijgt anonieme rapportages om
              gericht beleid te maken.
            </p>
          </section>

          <section className={appStyles.section}>
            <h2 className={appStyles.title}>
              Waarom preventie?
            </h2>
            <p className={appStyles.subtitle}>
              Voorkomen is goedkoper dan genezen. Minder verzuim, meer energie op
              de werkvloer en een sterker werkgeversmerk.
            </p>
          </section>
      </>
      )
  }

export default Privacy;